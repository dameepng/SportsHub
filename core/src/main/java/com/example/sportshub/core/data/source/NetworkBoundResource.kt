package com.example.sportshub.core.data.source

import com.example.sportshub.core.data.source.remote.network.ApiResponse
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject

abstract class NetworkBoundResource<ResultType, RequestType> {

    private val result = PublishSubject.create<Resource<ResultType>>()
    private val mCompositeDisposable = CompositeDisposable()

    init {
        @Suppress("LeakingThis")
        val dbSource = loadFromDB()
        val db = dbSource
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .take(1)
            .subscribe({ value ->
                if (shouldFetch(value)) {
                    fetchFromNetwork()
                } else {
                    result.onNext(Resource.Success(value))
                }
            }, { error ->
                result.onNext(Resource.Error(error.message ?: "Unknown error", null))
            })
        mCompositeDisposable.add(db)
    }

    protected abstract fun loadFromDB(): Flowable<ResultType>

    protected abstract fun shouldFetch(data: ResultType?): Boolean

    protected abstract fun createCall(): Flowable<ApiResponse<RequestType>>

    protected abstract fun saveCallResult(data: RequestType)

    private fun fetchFromNetwork() {
        val apiResponse = createCall()
        result.onNext(Resource.Loading(null))

        val response = apiResponse
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .take(1)
            .subscribe({ response ->
                when (response) {
                    is ApiResponse.Success -> {
                        saveCallResult(response.data)
                        val dbSource = loadFromDB()
                        val dbSub = dbSource.subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .take(1)
                            .subscribe({ newData ->
                                result.onNext(Resource.Success(newData))
                            }, { error ->
                                result.onNext(Resource.Error(error.message ?: "Unknown error", null))
                            })
                        mCompositeDisposable.add(dbSub)
                    }
                    is ApiResponse.Empty -> {
                        val dbSource = loadFromDB()
                        val dbSub = dbSource.subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .take(1)
                            .subscribe({ emptyData ->
                                result.onNext(Resource.Success(emptyData))
                            }, { error ->
                                result.onNext(Resource.Error(error.message ?: "Unknown error", null))
                            })
                        mCompositeDisposable.add(dbSub)
                    }
                    is ApiResponse.Error -> {
                        result.onNext(Resource.Error(response.errorMessage, null))
                    }
                }
            }, { error ->
                result.onNext(Resource.Error(error.message ?: "Unknown error", null))
            })

        mCompositeDisposable.add(response)
    }

    fun asFlowable(): Flowable<Resource<ResultType>> =
        result.toFlowable(BackpressureStrategy.BUFFER)
            .doFinally { mCompositeDisposable.clear() }
}
