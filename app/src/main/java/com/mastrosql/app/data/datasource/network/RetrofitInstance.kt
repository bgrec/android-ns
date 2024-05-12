package com.mastrosql.app.data.datasource.network

import android.annotation.SuppressLint
import com.mastrosql.app.BuildConfig
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

object RetrofitInstance {

    private const val LOGIN_PATH = "authentication"

    private var retrofitService: MastroAndroidApiService? = null
    private val loggingInterceptor =
        HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)

    private var trustManager: X509TrustManager? = null
    private var sslSocketFactory: SSLSocketFactory

    init {
        val sslContext = SSLContext.getInstance("TLS").apply {
            init(null, trustAllCertificates, java.security.SecureRandom())
        }
        sslSocketFactory = sslContext.socketFactory
    }

    fun getRetrofitInstance(
        baseUrl: String?
    ): MastroAndroidApiService {
        return retrofitService ?: createRetrofitInstance(
            baseUrl
        ).also { retrofitService = it }
    }

    fun setLoggingLevel(level: HttpLoggingInterceptor.Level) {
        loggingInterceptor.level = level
    }

    fun setCustomSslConfiguration(trustManager: X509TrustManager, socketFactory: SSLSocketFactory) {
        RetrofitInstance.trustManager = trustManager
        sslSocketFactory = socketFactory
    }

    fun setConnectTimeout(timeout: Long, timeUnit: TimeUnit) {
        // Set connect timeout
        httpClientBuilder.connectTimeout(timeout, timeUnit)
    }

    fun setReadTimeout(timeout: Long, timeUnit: TimeUnit) {
        // Set read timeout
        httpClientBuilder.readTimeout(timeout, timeUnit)
    }

    fun setWriteTimeout(timeout: Long, timeUnit: TimeUnit) {
        // Set write timeout
        httpClientBuilder.writeTimeout(timeout, timeUnit)
    }

    fun updateBaseUrl(newBaseUrl: String): MastroAndroidApiService {
        retrofitService = createRetrofitInstance(newBaseUrl)
        return retrofitService!!
    }

    private val httpClientBuilder: OkHttpClient.Builder
        get() {
            val builder = OkHttpClient.Builder()

            if (BuildConfig.BUILD_TYPE == "debug") {

                //Show the log in debug mode of the retrofit request
                builder.addInterceptor(loggingInterceptor)
            }

            if (trustManager != null && sslSocketFactory != null) {
                builder.sslSocketFactory(sslSocketFactory, trustManager!!)
                builder.hostnameVerifier { _, _ -> true }
            } else {
                builder.trustAllCertificates()
            }

            builder.addInterceptor { chain ->
                val originalRequest = chain.request()
                val newRequest = modifyRequestUrl(originalRequest)
                chain.proceed(newRequest)
            }

            //TODO: Remove the cookieJar when the session management is implemented in the app
            //builder.cookieJar(CookieJar.NO_COOKIES)

            // Disable automatic redirects because the login endpoint makes a redirect to the status page
            // but we want to capture the Set-Cookie header from the response, the session cookie
            builder.followRedirects(false)
            builder.followSslRedirects(false)

            // Add interceptor for adding cookies to request headers with the session cookie
            builder.addInterceptor(AddCookieInterceptor())

            return builder
        }

    private fun OkHttpClient.Builder.trustAllCertificates(): OkHttpClient.Builder {
        val trustAllSslContext = SSLContext.getInstance("TLS").apply {
            init(null, trustAllCertificates, java.security.SecureRandom())
        }
        val trustAllSslSocketFactory = trustAllSslContext.socketFactory
        sslSocketFactory(trustAllSslSocketFactory, trustAllCertificates[0] as X509TrustManager)
        hostnameVerifier { _, _ -> true }
        return this
    }

    private fun modifyRequestUrl(request: okhttp3.Request): okhttp3.Request {
        val url = request.url
        val pathSegments = url.encodedPathSegments

        //Log.d("pathSegments", pathSegments.toString())

        // Check if the LOGIN_PATH is present in the path segments
        if (pathSegments.contains(LOGIN_PATH)) {
            // Remove the second path segment
            val modifiedPathSegments = pathSegments.toMutableList().apply {
                removeAt(1)
            }

            // Construct the new base URL without the second path segment
            val newBaseUrl = buildString {
                append("https://")
                append(url.host)
                append(":")
                append(url.port)
                append("/")
                append(modifiedPathSegments.joinToString("/"))
            }

            //Log.d("newBaseUrl", newBaseUrl)

            // Build the modified URL
            val modifiedUrl = newBaseUrl.toHttpUrlOrNull()?.newBuilder()
                ?.encodedQuery(null)
                ?.encodedFragment(null)
                ?.build()

            if (modifiedUrl != null) {
                // Create a new request with the modified URL
                return request.newBuilder()
                    .url(modifiedUrl)
                    .build()
            }
        }

        // Return the original request if the LOGIN_PATH is not present
        return request
    }

    private val trustAllCertificates = arrayOf<TrustManager>(
        @SuppressLint("CustomX509TrustManager")
        object : X509TrustManager {
            @SuppressLint("TrustAllX509TrustManager")
            override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {
            }

            @SuppressLint("TrustAllX509TrustManager")
            override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {
            }

            override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
        }
    )

    private fun createRetrofitInstance(
        baseUrl: String?
    ): MastroAndroidApiService {

        return Retrofit.Builder()
            .baseUrl(baseUrl!!)
            .addConverterFactory(GsonConverterFactory.create())
            .client(httpClientBuilder.build())
            .build()
            .create(MastroAndroidApiService::class.java)
    }
}

