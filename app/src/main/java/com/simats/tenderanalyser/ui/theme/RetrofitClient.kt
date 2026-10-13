package com.simats.tenderanalyser

import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import java.util.concurrent.TimeUnit

// 1. Data Class
data class BidResponse(
    val success: Boolean,
    val message: String?,
    val referenceId: String?
)

// 2. API Interface
interface TenderApiService {
    @Multipart
    @POST("api/submit-bid")
    suspend fun submitTenderBid(
        @Part("companyName") companyName: RequestBody,
        @Part("address") address: RequestBody,
        @Part("gstin") gstin: RequestBody,
        @Part("contact") contact: RequestBody,
        @Part("signatory") signatory: RequestBody,
        @Part("designation") designation: RequestBody,
        @Part("price") price: RequestBody,
        @Part techProposal: MultipartBody.Part?,
        @Part financialBid: MultipartBody.Part?,
        @Part emdReceipt: MultipartBody.Part?
    ): BidResponse
}

// 3. The Client (Using your Mac's Wi-Fi IP!)
object RetrofitClient {
    private const val BASE_URL = "http://192.168.1.2:3000/"

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    val api: TenderApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TenderApiService::class.java)
    }
}