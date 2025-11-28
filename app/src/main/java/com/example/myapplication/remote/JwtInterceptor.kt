package com.example.myapplication.remote

import okhttp3.Interceptor
import okhttp3.Response

class JwtInterceptor(private val tokenProvider: () -> String?): Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val requestBuilder = chain.request().newBuilder()

        val token = tokenProvider.invoke()
        if(!token.isNullOrEmpty()){
            requestBuilder.addHeader("Authorization", "Bearer $token")
        }
        return chain.proceed(requestBuilder.build())
        }

    }