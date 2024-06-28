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

/**
 * Singleton object responsible for providing and configuring Retrofit instances
 * for making network requests to the MastroAndroidApiService.
 */
object RetrofitInstance {

    private const val LOGIN_PATH = "authentication"

    private var retrofitService: MastroAndroidApiService? = null
    private val loggingInterceptor =
        HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)

    private var trustManager: X509TrustManager? = null
    private var sslSocketFactory: SSLSocketFactory

    private val trustAllCertificates = arrayOf<TrustManager>(

        /**
         * Indicates that lint should suppress warnings related to the usage of a custom
         * X509TrustManager implementation.
         *
         * This annotation is used to suppress lint warnings that would otherwise be generated
         * for custom implementations of X509TrustManager, which may deviate from standard
         * security practices or lint rules.
         */
        @SuppressLint("CustomX509TrustManager")

        /**
         * X509TrustManager implementation that trusts all certificates without any validation.
         *
         * This implementation bypasses all certificate validation checks for both client and server
         * trust verification.
         *
         * Note: Using this implementation can expose the application to security vulnerabilities
         *       and should only be used for testing or in controlled environments.
         */
        object : X509TrustManager {

            /**
             * Trusts all client certificates without validation.
             *
             * This method overrides the default behavior of X509TrustManager to trust all client certificates
             * without performing any validation checks.
             */
            @SuppressLint("TrustAllX509TrustManager")
            override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {
            }

            /**
             * Trusts all server certificates without validation.
             *
             * This method overrides the default behavior of X509TrustManager to trust all server certificates
             * without performing any validation checks.
             */
            @SuppressLint("TrustAllX509TrustManager")
            override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {
            }

            /**
             * Returns an empty array of accepted issuers.
             *
             * This method overrides the default behavior of X509TrustManager to return an empty array,
             * indicating that no specific certificate issuers are trusted.
             */
            override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
        })

    /**
     * Initializes SSL socket factory using a TLS SSL context configured with trust all certificates.
     */
    init {
        val sslContext = SSLContext
            .getInstance("TLS")
            .apply {
                init(null, trustAllCertificates, java.security.SecureRandom())
            }
        sslSocketFactory = sslContext.socketFactory
    }

    /**
     * Retrieves the singleton instance of MastroAndroidApiService Retrofit client.
     *
     * If the Retrofit service instance has already been created, it returns the existing instance.
     * Otherwise, it creates a new instance using the provided base URL and initializes it.
     */
    fun getRetrofitInstance(
        baseUrl: String?
    ): MastroAndroidApiService {
        return retrofitService ?: createRetrofitInstance(
            baseUrl
        ).also { retrofitService = it }
    }

    /**
     * Sets the logging level for HTTP request/response logging.
     */
    fun setLoggingLevel(level: HttpLoggingInterceptor.Level) {
        loggingInterceptor.level = level
    }

    /**
     * Sets a custom SSL configuration for secure connections.
     */
    fun setCustomSslConfiguration(trustManager: X509TrustManager, socketFactory: SSLSocketFactory) {
        RetrofitInstance.trustManager = trustManager
        sslSocketFactory = socketFactory
    }

    /**
     * Sets the connection timeout for HTTP requests.
     */
    fun setConnectTimeout(timeout: Long, timeUnit: TimeUnit) {
        // Set connect timeout
        httpClientBuilder.connectTimeout(timeout, timeUnit)
    }

    /**
     * Sets the read timeout for HTTP requests.
     */
    fun setReadTimeout(timeout: Long, timeUnit: TimeUnit) {
        // Set read timeout
        httpClientBuilder.readTimeout(timeout, timeUnit)
    }

    /**
     * Sets the write timeout for HTTP requests.
     */
    fun setWriteTimeout(timeout: Long, timeUnit: TimeUnit) {
        // Set write timeout
        httpClientBuilder.writeTimeout(timeout, timeUnit)
    }

    /**
     * Updates the base URL of the Retrofit service and returns the updated service instance.
     */
    fun updateBaseUrl(newBaseUrl: String): MastroAndroidApiService {
        retrofitService = createRetrofitInstance(newBaseUrl)
        return retrofitService!!
    }

    private val httpClientBuilder: OkHttpClient.Builder
        /**
         * Provides a configured OkHttpClient.Builder instance for HTTP client configuration.
         */
        get() {
            val builder = OkHttpClient.Builder()

            /**
             * Adds a logging interceptor to the OkHttpClient.Builder if the application is in debug mode.
             * This interceptor logs the HTTP requests and responses made by Retrofit for debugging purposes.
             */
            if (BuildConfig.BUILD_TYPE == "debug") {

                //Show the log in debug mode of the retrofit request
                /**
                 * Adds a logging interceptor to the OkHttpClient.Builder for debugging purposes.
                 *
                 * This interceptor logs the HTTP requests and responses made by Retrofit for debugging purposes.
                 */
                // TODO remove this
                builder.addInterceptor(loggingInterceptor)
            }

            /**
             * Configures SSL socket factory and hostname verifier if custom SSL configuration is provided.
             */
            if (trustManager != null && sslSocketFactory != null) {
                builder.sslSocketFactory(sslSocketFactory, trustManager!!)
                builder.hostnameVerifier { _, _ -> true }
            }

            /**
             * Sets up the OkHttpClient.Builder to trust all certificates if custom SSL configuration
             * is not provided.
             */
            else {
                builder.trustAllCertificates()
            }

            /**
             * Adds an interceptor to modify the request URL before proceeding with the HTTP request.
             * The interceptor modifies the original request by applying changes using `modifyRequestUrl`.
             */
            builder.addInterceptor { chain ->
                val originalRequest = chain.request()
                val newRequest = modifyRequestUrl(originalRequest)
                chain.proceed(newRequest)
            }

            //TODO: Remove the cookieJar when the session management is implemented in the app
            //builder.cookieJar(CookieJar.NO_COOKIES)

            // Disable automatic redirects because the login endpoint makes a redirect to the status page
            // but we want to capture the Set-Cookie header from the response, the session cookie

            /**
             * Disables automatic HTTP redirects in the OkHttpClient.Builder.
             *
             * By default, OkHttp automatically follows HTTP redirects (3xx responses) for requests.
             * Calling this method with `false` disables this behavior, so redirects must be handled manually.
             */
            builder.followRedirects(false)

            /**
             * Disables automatic HTTPS redirects in the OkHttpClient.Builder.
             *
             * By default, OkHttp automatically follows HTTPS redirects (3xx responses) for requests.
             * Calling this method with `false` disables this behavior, so HTTPS redirects must be handled manually.
             */
            builder.followSslRedirects(false)

            /**
             * Adds an interceptor to the OkHttpClient.Builder that adds a session cookie to outgoing requests.
             *
             * The interceptor is responsible for appending a session cookie retrieved from `SessionManager.getSessionCookie()`
             * to the headers of outgoing HTTP requests.
             */
            // Add interceptor for adding cookies to request headers with the session cookie
            builder.addInterceptor(AddCookieInterceptor())


            return builder
        }

    /**
     * Configures the OkHttpClient.Builder to trust all SSL certificates.
     *
     * This method sets up the OkHttpClient.Builder to trust all SSL certificates,
     * effectively disabling SSL certificate validation. It uses a custom SSL context
     * initialized with a trust manager that trusts all certificates.
     */
    private fun OkHttpClient.Builder.trustAllCertificates(): OkHttpClient.Builder {
        val trustAllSslContext = SSLContext
            .getInstance("TLS")
            .apply {
                init(null, trustAllCertificates, java.security.SecureRandom())
            }
        val trustAllSslSocketFactory = trustAllSslContext.socketFactory
        sslSocketFactory(trustAllSslSocketFactory, trustAllCertificates[0] as X509TrustManager)
        hostnameVerifier { _, _ -> true }
        return this
    }

    /**
     * Modifies the URL of an HTTP request by removing the second path segment if it matches LOGIN_PATH.
     *
     * This function checks if the request URL contains LOGIN_PATH in its path segments. If found,
     * it removes the second path segment and constructs a new base URL without it. It then builds
     * a modified URL and creates a new request with this modified URL.
     */
    private fun modifyRequestUrl(request: okhttp3.Request): okhttp3.Request {
        val url = request.url
        val pathSegments = url.encodedPathSegments

        //Log.d("pathSegments", pathSegments.toString())

        /**
         * Modifies the URL of an HTTP request if it contains LOGIN_PATH in its path segments.
         *
         * If the request URL contains LOGIN_PATH, this function removes the second path segment,
         * constructs a new base URL without it, and creates a new request with the modified URL.
         */
        // Check if the LOGIN_PATH is present in the path segments
        if (pathSegments.contains(LOGIN_PATH)) {
            // Remove the second path segment
            val modifiedPathSegments = pathSegments
                .toMutableList()
                .apply {
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
            val modifiedUrl = newBaseUrl
                .toHttpUrlOrNull()
                ?.newBuilder()
                ?.encodedQuery(null)
                ?.encodedFragment(null)
                ?.build()

            /**
             * Creates a new okhttp3.Request object with the modified URL if it is not null.
             */
            if (modifiedUrl != null) {
                // Create a new request with the modified URL
                return request
                    .newBuilder()
                    .url(modifiedUrl)
                    .build()
            }
        }

        // Return the original request if the LOGIN_PATH is not present
        return request
    }


    private fun createRetrofitInstance(
        baseUrl: String?
    ): MastroAndroidApiService {

        return Retrofit
            .Builder()
            .baseUrl(baseUrl!!)
            .addConverterFactory(GsonConverterFactory.create())
            .client(httpClientBuilder.build())
            .build()
            .create(MastroAndroidApiService::class.java)
    }
}
