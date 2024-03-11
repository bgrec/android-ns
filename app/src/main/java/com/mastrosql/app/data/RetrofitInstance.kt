import android.annotation.SuppressLint
import com.mastrosql.app.data.datasource.network.MastroAndroidApiService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

object RetrofitInstance {

    private var retrofitService: MastroAndroidApiService? = null

    fun getRetrofitInstance(baseUrl: String): MastroAndroidApiService {
        return retrofitService ?: createRetrofitInstance(baseUrl).also { retrofitService = it }
    }

    /**
     *  Custom TrustManager for testing purposes (not recommended for production)
     *  This TrustManager trusts all certificates
     *  This is used to bypass SSL certificate validation
     */

    //TODO - Remove this TrustManager and use a valid SSL certificate in production

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

    /**
     * SSL Context for testing purposes (not recommended for production)
     */
    private val sslContext = SSLContext.getInstance("TLS").apply {
        init(null, trustAllCertificates, java.security.SecureRandom())
    }

    /**
     * SSL Socket Factory for testing purposes (not recommended for production)
     */
    private val sslSocketFactory = sslContext.socketFactory

    // Function to create Retrofit instance
    private fun createRetrofitInstance(baseUrl: String): MastroAndroidApiService {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .client(
                OkHttpClient.Builder()
                    .sslSocketFactory(sslSocketFactory, trustAllCertificates[0] as X509TrustManager)
                    .hostnameVerifier { _, _ -> true }
                    .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                    .build()
            )
            .build()
            .create(MastroAndroidApiService::class.java)
    }

    // Function to update the base URL of the existing Retrofit instance
    fun updateBaseUrl(newBaseUrl: String): MastroAndroidApiService {

        retrofitService = createRetrofitInstance(newBaseUrl)
        return retrofitService!!
    }
}
