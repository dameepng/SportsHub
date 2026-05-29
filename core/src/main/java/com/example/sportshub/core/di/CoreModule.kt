package com.example.sportshub.core.di

import androidx.room.Room
import com.example.sportshub.core.data.source.SportRepository
import com.example.sportshub.core.data.source.local.LocalDataSource
import com.example.sportshub.core.data.source.local.room.SportDatabase
import com.example.sportshub.core.data.source.remote.RemoteDataSource
import com.example.sportshub.core.data.source.remote.network.ApiService
import com.example.sportshub.core.domain.repository.ISportRepository
import com.example.sportshub.core.security.DatabasePassphraseProvider
import com.example.sportshub.core.utils.AppExecutors
import net.sqlcipher.database.SupportFactory
import okhttp3.CertificatePinner
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

private const val SPORT_DATABASE_NAME = "Sport.db"

val databaseModule = module {
    factory { get<SportDatabase>().sportDao() }
    single {
        DatabasePassphraseProvider.deleteLegacyPlaintextDatabase(
            androidContext(),
            SPORT_DATABASE_NAME
        )
        val passphrase = DatabasePassphraseProvider.getPassphrase(androidContext())
        val supportFactory = SupportFactory(passphrase)
        Room.databaseBuilder(
            androidContext(),
            SportDatabase::class.java, SPORT_DATABASE_NAME
        )
            .openHelperFactory(supportFactory)
            .fallbackToDestructiveMigration(true)
            .build()
    }
}

val networkModule = module {
    single {
        val certificatePinner = CertificatePinner.Builder()
            .add(
                "www.thesportsdb.com",
                "sha256/H7mAMOV8siGjb14MWbJo0WZO0Fr0uiFZVX1bzdUGC6I=",
                "sha256/kIdp6NNEd8wsugYyyIYFsi1ylMCED3hZbSR8ZFsa/A4="
            )
            .build()

        OkHttpClient.Builder()
            .certificatePinner(certificatePinner)
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .connectTimeout(120, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .build()
    }
    single {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://www.thesportsdb.com/api/v1/json/3/")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(get())
            .build()
        retrofit.create(ApiService::class.java)
    }
}


val repositoryModule = module {
    single { LocalDataSource(get()) }
    single { RemoteDataSource(get()) }
    factory { AppExecutors() }
    single<ISportRepository> { SportRepository(get(), get(), get()) }
}
