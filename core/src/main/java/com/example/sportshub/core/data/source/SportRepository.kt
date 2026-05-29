package com.example.sportshub.core.data.source

import com.example.sportshub.core.data.source.local.LocalDataSource
import com.example.sportshub.core.data.source.remote.RemoteDataSource
import com.example.sportshub.core.data.source.remote.network.ApiResponse
import com.example.sportshub.core.data.source.remote.response.SportResponse
import com.example.sportshub.core.domain.model.Sport
import com.example.sportshub.core.domain.repository.ISportRepository
import com.example.sportshub.core.utils.AppExecutors
import com.example.sportshub.core.utils.DataMapper
import io.reactivex.Flowable
import io.reactivex.schedulers.Schedulers

class SportRepository(
    private val remoteDataSource: RemoteDataSource,
    private val localDataSource: LocalDataSource,
    private val appExecutors: AppExecutors
) : ISportRepository {

    override fun getAllSport(sport: String, country: String): Flowable<Resource<List<Sport>>> =
        object : NetworkBoundResource<List<Sport>, SportResponse>() {

            override fun loadFromDB(): Flowable<List<Sport>> {
                return localDataSource.getAllSport().map { DataMapper.mapEntitiesToDomain(it) }
            }

            override fun shouldFetch(data: List<Sport>?): Boolean {
                return data.isNullOrEmpty()
            }

            override fun createCall(): Flowable<ApiResponse<SportResponse>> =
                remoteDataSource.getAllSport(sport, country)

            override fun saveCallResult(data: SportResponse) {
                data.teams?.let {
                    val sportList = DataMapper.mapResponsesToEntities(it.filterNotNull())
                    localDataSource.deleteNonFavoriteSport()
                        .andThen(localDataSource.insertSport(sportList))
                        .subscribeOn(Schedulers.io())
                        .subscribe()
                }
            }

        }.asFlowable()

    override fun getFavoriteSport(): Flowable<List<Sport>> {
        return localDataSource.getFavoriteSport().map { DataMapper.mapEntitiesToDomain(it) }
    }

    override fun setFavoriteSport(sport: Sport, state: Boolean) {
        val sportEntity = DataMapper.mapDomainToEntity(sport)
        appExecutors.diskIO().execute { localDataSource.setFavoriteSport(sportEntity, state) }
    }
}
