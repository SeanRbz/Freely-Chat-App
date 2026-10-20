package com.example.composeapp.di.module

import com.example.composeapp.di.qualifiers.IoDispatcher
import com.example.composeapp.repository.FreelyRepository
import com.example.composeapp.repository.SessionRepository
import com.example.composeapp.repository.entities.ChatMessage
import com.example.composeapp.repository.local.LocalSessionRepository
import com.example.composeapp.repository.remote.BaseUrlService
import com.example.composeapp.repository.remote.ChatMessageDeserializer
import com.example.composeapp.repository.remote.FreelyService
import com.example.composeapp.repository.remote.NetworkFreelyRepository
import com.example.composeapp.repository.remote.UserRepositoryImpl
import com.example.composeapp.repository.remote.UserService
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    fun provideGson(): Gson = GsonBuilder()
        .setLenient()
        .registerTypeAdapter(ChatMessage::class.java, ChatMessageDeserializer())
        .create()

    @Singleton
    @Provides
    fun provideBaseUrlService(): BaseUrlService = BaseUrlService()

    @Provides
    fun provideServiceAPI(@Named("retrofitUserService") retrofit: Retrofit): UserService =
        retrofit.create(
            UserService::class.java
        )

    @Singleton
    @Provides
    @Named("retrofitUserService")
    fun providesChatAPI(
        gson: Gson, api: BaseUrlService, provideOkHttpClient: OkHttpClient
    ): Retrofit = Retrofit.Builder().baseUrl(api.baseUrl).client(provideOkHttpClient)
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .addConverterFactory(GsonConverterFactory.create(gson)).build()

    // Shares the Retrofit above: BaseUrlService.baseUrl is the Freely API's Postman mock server.
    @Provides
    fun provideFreelyService(@Named("retrofitUserService") retrofit: Retrofit): FreelyService =
        retrofit.create(FreelyService::class.java)

    @Singleton
    @Provides
    fun provideInterceptor(): HttpLoggingInterceptor =
        HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)

    @Singleton
    @Provides
    fun provideOkHttpClient(interceptor: HttpLoggingInterceptor): OkHttpClient =
        OkHttpClient.Builder().addInterceptor(interceptor).build()

    @Singleton
    @Provides
    fun provideChatRepositoryImpl(
        api: UserService,
        @IoDispatcher ioDispatcher: CoroutineDispatcher
    ): UserRepositoryImpl = UserRepositoryImpl(api, ioDispatcher)

    // Reads from the Freely API (the Postman mock server), falling back to the built-in MockFreelyData when a call fails.
    // Bind MockFreelyRepository instead to skip the network entirely.
    @Singleton
    @Provides
    fun provideFreelyRepository(network: NetworkFreelyRepository): FreelyRepository = network

    @Singleton
    @Provides
    fun provideSessionRepository(local: LocalSessionRepository): SessionRepository = local

}