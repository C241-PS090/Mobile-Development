// ApiService.kt

import com.google.gson.annotations.SerializedName
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import java.io.File
import java.util.concurrent.TimeUnit

data class SignUpResponse(


    @field:SerializedName("message")
    val message: String

)

data class LoginResponse(
    @SerializedName("message")
    val message: String,
    @SerializedName("data")
    val data: LoginResult
)

data class LoginResult(
    @SerializedName("userId")
    val userId: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("role")
    val role: String,
    @SerializedName("token")
    val token: String
)

data class profileResponse(
    @SerializedName("id")
    val message: String,
    @SerializedName("data")
    val data: profileResult
)

data class profileResult(
    @SerializedName("password")
    val userId: String,
    @SerializedName("role")
    val role: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("email")
    val email: String,
)

data class PredictResponse(
    @SerializedName("predictions")
    val predictions: Predictions
)

data class Predictions(
    @SerializedName("class")
    val predictionClass: String,

    @SerializedName("confidence")
    val confidence: Double,

    @SerializedName("userId")
    val userId: String,

    @SerializedName("created_at")
    val createdAt: String
)

data class UpdateProfileResponse(
    @SerializedName("message")
    val message: String,

    @SerializedName("data")
    val data: UserData
)

data class UserData(
    @SerializedName("password")
    val password: String,

    @SerializedName("role")
    val role: String,

    @SerializedName("email")
    val email: String,

    @SerializedName("createdAt")
    val createdAt: CreatedAtData,

    @SerializedName("profilePictureUrl")
    val profilePictureUrl: String,

    @SerializedName("age")
    val age: String,

    @SerializedName("token")
    val token: String,

    @SerializedName("gender")
    val gender: String,

    @SerializedName("name")
    val name: String,

    @SerializedName("updatedAt")
    val updatedAt: UpdatedAtData
)

data class CreatedAtData(
    @SerializedName("_seconds")
    val seconds: Long,

    @SerializedName("_nanoseconds")
    val nanoseconds: Long
)

data class UpdatedAtData(
    @SerializedName("_seconds")
    val seconds: Long,

    @SerializedName("_nanoseconds")
    val nanoseconds: Long
)


interface ApiService {
    @POST("register")
    @FormUrlEncoded
    fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<SignUpResponse>

    @POST("login")
    @FormUrlEncoded
    fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<LoginResponse>

    @GET("users/{userId}")
    fun getUserProfile(
        @Header("Authorization") token: String,
        @Path("userId") userId: String,
    ): Call<profileResponse>

    fun profile(
        @Header("authorization") token: String
    ): Call<LoginResponse>

    @POST("predict")
    @Multipart
    fun Predict(
        @Part file: MultipartBody.Part,
        @Part("userId") userId: String,
    ): Call<PredictResponse>

    @PUT("api/users/{userId}")
    @Multipart
    fun updateUserProfile(
        @Header("Authorization") token: String,
        @Path("userId") userId: String,
        @Part("name") name: String,
        @Part("gender") gender: String,
        @Part("age") age: String,
        @Part profilePicture: MultipartBody.Part
    ): Call<UpdateProfileResponse>

}

class ApiConfig {
    fun getApiService(): ApiService {
        val loggingInterceptor =
            HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
        val client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
        val retrofit = Retrofit.Builder()
            .baseUrl("https://backend-api-olsevrwmiq-et.a.run.app/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
        return retrofit.create(ApiService::class.java)
    }
    fun getPredictApiService(): ApiService {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS) // Increase timeout to 30 seconds
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(loggingInterceptor)
            .retryOnConnectionFailure(true) // Enable retry on connection failure
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://predict-api-olsevrwmiq-et.a.run.app/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

        return retrofit.create(ApiService::class.java)
    }
}
