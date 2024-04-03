package com.mastrosql.app.data.datasource.network

import okhttp3.Interceptor
import okhttp3.Response

/*
Class to add the session cookie to the request header

 */
class AddCookieInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val sessionCookie = SessionManager.getSessionCookie()
        //Log.d("AddCookieInterceptor", "Adding cookie to request: $sessionCookie")
        val modifiedRequest = chain.request().newBuilder()
            .addHeader("Cookie", sessionCookie ?: "")
            .build()
        return chain.proceed(modifiedRequest)
    }
}
