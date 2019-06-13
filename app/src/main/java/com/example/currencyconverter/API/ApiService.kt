package com.example.currencyconverter.API

import com.example.currencyconverter.Constants
import com.google.gson.GsonBuilder
import io.reactivex.Single
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface ApiService {

    @GET("/")
    fun getCurrencyList(): Single<CurrencyVatModel>

    companion object {
        /*fun create(): ApiService {

            val gson = GsonBuilder()
                .setLenient()
                .create()

            val interceptor = HttpLoggingInterceptor()
            interceptor.level = HttpLoggingInterceptor.Level.BODY

            val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .build()

            val retrofit = Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .baseUrl(Constants.jsonvaBbaseURL)
                .build()
            return retrofit.create(ApiService::class.java)
        }*/

        @Volatile
        private var retrofit: Retrofit? = null


        @Synchronized
        fun create(): ApiService {

            retrofit ?: synchronized(this) {
                retrofit = buildRetrofit()
            }

            return retrofit?.create(ApiService::class.java)!!
        }


        private fun buildRetrofit(): Retrofit {

            val gson = GsonBuilder()
                .setLenient()
                .create()

            val interceptor = HttpLoggingInterceptor()
            interceptor.level = HttpLoggingInterceptor.Level.BODY

            val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .build()

            return Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .baseUrl(Constants.jsonvaBbaseURL)
                .build()
        }
    }
}