package com.netflixclone.network.services

import com.netflixclone.BuildConfig
import com.netflixclone.data_models.FeedItem
import com.netflixclone.data_models.Media
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.PolymorphicJsonAdapterFactory
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.lang.reflect.InvocationTargetException

object ApiClient {
    private const val TMDB_BASE_URL = "https://api.themoviedb.org/3/"
    private const val FIREBASE_BASE_URL =
        "https://firebasestorage.googleapis.com/v0/b/sample-a8754.appspot.com/o/"

    private val moshi: Moshi = Moshi.Builder()
        .add(
            PolymorphicJsonAdapterFactory.of(Media::class.java, "media_type")
                .withSubtype(Media.Movie::class.java, "movie")
                .withSubtype(Media.Tv::class.java, "tv")
                .withSubtype(Media.Person::class.java, "person")
        )
        .add(
            PolymorphicJsonAdapterFactory.of(FeedItem::class.java, "type")
                .withSubtype(FeedItem.Header::class.java, "header")
                .withSubtype(FeedItem.HorizontalList::class.java, "horizontal_list")
        )
        .add(KotlinJsonAdapterFactory())
        .build()

    private fun getClient(addApiKey: Boolean): OkHttpClient {
        val builder = OkHttpClient.Builder()
        if (addApiKey) {
            builder.addInterceptor { chain ->
                var request = chain.request()
                val url = request.url.newBuilder().addQueryParameter("api_key",
                    BuildConfig.API_KEY).build()
                request = request.newBuilder().url(url).build()
                chain.proceed(request)
            }
        }

        try {
            val StethoInterceptorClass =
                Class.forName("com.facebook.stetho.okhttp3.StethoInterceptor")
            val stethoInterceptor =
                StethoInterceptorClass.getConstructor().newInstance() as Interceptor
            builder.addNetworkInterceptor(stethoInterceptor)
        } catch (e: ClassNotFoundException) {
            e.printStackTrace();
        } catch (e: NoSuchMethodException) {
            e.printStackTrace();
        } catch (e: IllegalAccessException) {
            e.printStackTrace();
        } catch (e: InvocationTargetException) {
            e.printStackTrace();
        }
        return builder.build()
    }

    private fun getTmdbClient(): TmdbService {
        return Retrofit.Builder()
            .baseUrl(TMDB_BASE_URL)
            .client(getClient(addApiKey = true))
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(TmdbService::class.java)
    }

    private fun getFirebaseClient(): FirebaseService {
        return Retrofit.Builder()
            .baseUrl(FIREBASE_BASE_URL)
            .client(getClient(addApiKey = false))
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(FirebaseService::class.java)
    }


    val TMDB = getTmdbClient()
    val Firebase = getFirebaseClient()
}