package com.aryanspatel.droidwire.di

import android.content.Context
import androidx.room.Room
import com.aryanspatel.droidwire.data.local.ApplicationDatabase
import com.aryanspatel.droidwire.data.remote.api.ArticleContentApi
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides @Singleton
    fun provideDatabase(@ApplicationContext ctx: Context) : ApplicationDatabase =
        Room.databaseBuilder(ctx, ApplicationDatabase::class.java, "application_database")
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideArticleDao(db: ApplicationDatabase) = db.articleDao()

    @Provides
    fun provideNotificationLogDao(db: ApplicationDatabase) = db.notificationLogDao()


    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient =
        OkHttpClient.Builder()
            .build()

    @Provides @Singleton
    fun provideGson(): Gson = GsonBuilder().create()

    @Provides @Singleton
    fun provideArticleContentApi(
        okHttp: OkHttpClient,
        gson: Gson
    ): ArticleContentApi =
        Retrofit.Builder()
//            .baseUrl("https://example.com/")
            .baseUrl("https://northamerica-northeast2-droidwire-57e82.cloudfunctions.net/")
            .client(okHttp)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(ArticleContentApi::class.java)
}