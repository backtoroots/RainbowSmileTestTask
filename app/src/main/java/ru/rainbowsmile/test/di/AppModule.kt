package ru.rainbowsmile.test.di

import android.app.Application
import android.os.SystemClock
import android.util.Log
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Credentials
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import ru.rainbowsmile.test.R
import ru.rainbowsmile.test.repository.DocumentsApi
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
abstract class AppModule {

    companion object {
        private fun provideAuthInterceptor(
            authHeaderTitle: String,
            authLogin: String,
            authPassword: String
        ): Interceptor {
            return Interceptor { chain ->
                val request = chain.request()
                val authenticationRequest = request.newBuilder()
                    .header(
                        authHeaderTitle,
                        Credentials.basic(authLogin, authPassword)
                    )
                    .build()
                chain.proceed(authenticationRequest)
            }
        }

        private fun provideRetryInterceptor(): Interceptor {
            val tag = "RetryInterceptor"
            return Interceptor { chain ->
                val request = chain.request()
                var response = chain.proceed(request)
                var tryCount = 0

                while (!response.isSuccessful && tryCount < 3) {
                    response.close()
                    tryCount++
                    Log.d(tag, "Request is not successful - $tryCount")

                    SystemClock.sleep(3000)

                    response = chain.proceed(request)
                }

                Log.d(tag, "Request is successful!")
                response
            }
        }

        @Singleton
        @Provides
        fun provideRetrofitInstance(application: Application): Retrofit {
            val client = OkHttpClient.Builder()
                .addInterceptor(
                    HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
                )
                .addInterceptor(provideRetryInterceptor())
                .addInterceptor(
                    provideAuthInterceptor(
                        application.resources.getString(R.string.auth_header_title),
                        application.resources.getString(R.string.auth_login),
                        application.resources.getString(R.string.auth_password)
                    )
                )
                .build()

            val moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()

            return Retrofit.Builder()
                .baseUrl(application.resources.getString(R.string.base_url))
                .client(client)
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build()
        }

        @Singleton
        @Provides
        fun provideDocumentsApi(retrofit: Retrofit): DocumentsApi =
            retrofit.create(DocumentsApi::class.java)
    }
}
