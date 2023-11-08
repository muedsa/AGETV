package com.muedsa.agetv

import android.content.Context
import com.google.common.net.HttpHeaders
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.muedsa.agetv.repository.AppRepository
import com.muedsa.agetv.repository.DataStoreRepo
import com.muedsa.agetv.service.AgeApiService
import com.muedsa.agetv.service.AgePlayerService
import com.muedsa.agetv.service.DanDanPlayApiService
import com.muedsa.uitl.ChromeUserAgent
import com.muedsa.uitl.LenientJson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object AppModule {

    @Provides
    @Singleton
    fun provideAgeApiService(): AgeApiService {
        val client = OkHttpClient.Builder()
            .addInterceptor {
                it.proceed(
                    it.request().newBuilder()
                        .header(HttpHeaders.USER_AGENT, ChromeUserAgent)
                        .header(HttpHeaders.REFERER, AgeMobileUrl)
                        .build()
                )
            }
            .also {
                if (BuildConfig.DEBUG) {
                    val loggingInterceptor = HttpLoggingInterceptor()
                    loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
                    it.addInterceptor(loggingInterceptor)
                }
            }
            .build()

        return Retrofit.Builder()
            .baseUrl(AgeMobileApiUrl)
            .addConverterFactory(LenientJson.asConverterFactory("application/json".toMediaType()))
            .client(client)
            .build()
            .create(AgeApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideAgePlayerService() = AgePlayerService()

    @Provides
    @Singleton
    fun provideDanDanPlayApiService(): DanDanPlayApiService {
        val client = OkHttpClient.Builder()
            .also {
                if (BuildConfig.DEBUG) {
                    val loggingInterceptor = HttpLoggingInterceptor()
                    loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
                    it.addInterceptor(loggingInterceptor)
                }
            }
            .build()

        return Retrofit.Builder()
            .baseUrl("https://api.dandanplay.net/api/")
            .addConverterFactory(LenientJson.asConverterFactory("application/json".toMediaType()))
            .client(client)
            .build()
            .create(DanDanPlayApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideAppRepository(
        apiService: AgeApiService,
        playerService: AgePlayerService,
        danDanPlayApiService: DanDanPlayApiService
    ) =
        AppRepository(
            apiService = apiService,
            playerService = playerService,
            danDanPlayApiService = danDanPlayApiService
        )

    @Singleton
    @Provides
    fun provideDataStoreRepository(@ApplicationContext app: Context) = DataStoreRepo(app)
}