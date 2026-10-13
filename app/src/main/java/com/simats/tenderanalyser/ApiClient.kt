package com.simats.tenderanalyser

import com.google.gson.annotations.SerializedName
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

// ==========================================
// DATA CLASSES
// ==========================================

data class LoginRequest(val email: String, val password: String)
data class AuthorityLoginRequest(val email: String, val password: String)

data class ChangePasswordRequest(
    val userId: String,
    val currentPassword: String,
    val newPassword: String
)

data class ChangePasswordResponse(
    val success: Boolean,
    val message: String?
)

data class AdminChangePasswordRequest(
    val admin_id: String,
    val currentPassword: String,
    val newPassword: String
)

data class UpdateTenderRequest(
    val tender_id: String,
    val title: String,
    val est_value: String,
    val emd_amount: String,
    val location: String,
    val description: String,
    val deadline: String
)

data class UpdateAuthorityProfileRequest(
    val admin_id: String,
    val adminName: String,
    val designation: String,
    val deptName: String,
    val officeCode: String,
    val sector: String,
    val hqAddress: String,
    val signatoryName: String,
    val contactNumber: String
)

data class TenderListItem(
    val id: String,
    val projectTitle: String,
    val nitNumber: String,
    val estimatedValue: String,
    val deadline: String,
    val status: String
)

data class GetTendersResponse(
    val success: Boolean,
    val message: String?,
    val tenders: List<TenderListItem>?
)

// 🚨 NEW: Dedicated model for Tender Documents matching Flask's dictionary response
data class TenderDoc(
    val title: String?,
    @SerializedName("fileUrl") val fileUrl: String?,
    @SerializedName("extractedText") val extractedText: String?
)

data class TenderDetail(
    val id: String,
    val projectTitle: String,
    val nitNumber: String,
    val estimatedValue: String,
    val emdAmount: String,
    val sector: String,
    val location: String,
    val deadline: String,
    val projectDescription: String,
    val status: String,
    @SerializedName("tenderDocs") val tenderDocs: List<TenderDoc>? // ✅ Updated from List<String> to List<TenderDoc>
)

data class SingleTenderResponse(
    val success: Boolean,
    val message: String?,
    val tender: TenderDetail?
)

data class BidListItem(
    val bidId: String,
    val rank: String,
    val companyName: String,
    val bidAmount: String,
    val status: String,
    val isVerified: Boolean,
    val mlScore: Double? = 0.0
)

data class GetBidsResponse(
    val success: Boolean,
    val message: String?,
    val bids: List<BidListItem>?
)

data class MyBidItem(
    @SerializedName("tender_id") val tenderId: String,
    @SerializedName("tender_title") val tenderTitle: String,
    @SerializedName("tender_ref") val tenderRef: String,
    @SerializedName("bid_amount") val quotedPrice: String,
    val status: String,
    val rank: String? = null,
    val gapToL1: String? = null,
    val l1Price: String? = null,
    val techDoc: String? = null,
    val finDoc: String? = null,
    val emdDoc: String? = null,
    val gstDoc: String? = null,
    val affidavitDoc: String? = null,
    val pastWorkDoc: String? = null,
    val finSolvencyDoc: String? = null
)

data class GetMyBidsResponse(
    val success: Boolean,
    val message: String?,
    val bids: List<MyBidItem>?
)

data class ComparisonBidItem(
    val companyName: String,
    val amount: Double,
    val rank: String,
    val isYou: Boolean
)

data class TenderComparisonResponse(
    val success: Boolean,
    val projectTitle: String,
    val nitNumber: String,
    val userBid: Double,
    val userRank: String,
    val marketAvg: Double,
    val deviation: Double,
    val competitors: Int,
    val bids: List<ComparisonBidItem>
)

data class BidDetail(
    val bidId: String,
    val companyName: String,
    val tenderEstimate: String,
    val bidAmount: String,
    val techDoc: String?,
    val finDoc: String?,
    val emdDoc: String?,
    val gstDoc: String?,
    val affidavitDoc: String?,
    val pastWorkDoc: String?,
    val finSolvencyDoc: String?,
    val status: String,
    val mlScore: Double? = 0.0
)

data class BidDetailResponse(
    val success: Boolean,
    val message: String?,
    val bidDetails: BidDetail?
)

data class LoggedInUser(
    val id: Int,
    val name: String? = null,
    val company: String? = null,
    val status: String? = null,
    val fullName: String? = null,
    val jobTitle: String? = null,
    val companyName: String? = null,
    val regNumber: String? = null,
    val businessDesc: String? = null,
    val hqAddress: String? = null,
    val businessEmail: String? = null,
    val profileImageUrl: String? = null
)

data class LoggedInAdmin(
    val id: Int,
    val adminName: String? = null,
    val designation: String? = null,
    val email: String? = null,
    val deptName: String? = null,
    val officeCode: String? = null,
    val sector: String? = null,
    val hqAddress: String? = null,
    val signatoryName: String? = null,
    val contactNumber: String? = null,
    val status: String? = null,
    val profileImageUrl: String? = null
)

data class ApiResponse(
    val success: Boolean,
    val message: String?,
    val referenceId: String? = null,
    val status: String? = null,
    val user: LoggedInUser? = null,
    val admin: LoggedInAdmin? = null
)

data class UpdateAuthorityProfileResponse(
    val success: Boolean,
    val message: String?,
    val profileImageUrl: String? = null
)

data class UserProfileResponse(
    val success: Boolean,
    val fullName: String?,
    val jobTitle: String?,
    val companyName: String?,
    val regNumber: String?,
    val businessDesc: String?,
    val hqAddress: String?,
    val businessEmail: String?,
    val profileImageUrl: String?,
    val message: String?
)

data class GstPanResponse(
    val success: Boolean,
    val gstin: String?,
    val panNumber: String?,
    val gstFileName: String?,
    val panFileName: String?,
    val message: String?
)

data class ComplianceDocsResponse(
    val success: Boolean,
    val status: String?,
    val documents: Map<String, String?>?,
    val message: String?
)

data class UpdateDocsResponse(
    val success: Boolean,
    val status: String?,
    val message: String?
)

data class AdminDocsResponse(
    val success: Boolean,
    val status: String?,
    val documents: Map<String, String?>?,
    val message: String?
)


interface ApiService {

    // ==========================================
    // 🔵 BIDDER ENDPOINTS
    // ==========================================

    @POST("login")
    suspend fun loginUser(@Body request: LoginRequest): ApiResponse

    @Multipart
    @POST("register")
    suspend fun registerUser(
        @Part("full_name") fullName: RequestBody,
        @Part("email") email: RequestBody,
        @Part("password") password: RequestBody,
        @Part("company_name") companyName: RequestBody,
        @Part("gstin") gstin: RequestBody,
        @Part gst_certificate: MultipartBody.Part?,
        @Part incorporation_certificate: MultipartBody.Part?,
        @Part pan_card: MultipartBody.Part?
    ): ApiResponse

    @GET("get_profile")
    suspend fun getUserProfile(@Query("user_id") userId: String): UserProfileResponse

    @Multipart
    @POST("update_profile")
    suspend fun updateUserProfile(
        @Part("user_id") userId: RequestBody,
        @Part("fullName") fullName: RequestBody,
        @Part("jobTitle") jobTitle: RequestBody,
        @Part("companyName") companyName: RequestBody,
        @Part("regNumber") regNumber: RequestBody,
        @Part("businessDesc") businessDesc: RequestBody,
        @Part("hqAddress") hqAddress: RequestBody,
        @Part("businessEmail") businessEmail: RequestBody,
        @Part profileImage: MultipartBody.Part?
    ): ApiResponse

    @POST("api/bidder/change-password")
    suspend fun changeBidderPassword(@Body request: ChangePasswordRequest): ChangePasswordResponse

    @GET("get_gst_pan")
    suspend fun getGstPan(@Query("user_id") userId: String): GstPanResponse

    @Multipart
    @POST("update_gst_pan")
    suspend fun updateGstPan(
        @Part("user_id") userId: RequestBody,
        @Part("gstin") gstin: RequestBody,
        @Part("pan_number") panNumber: RequestBody,
        @Part new_gst_certificate: MultipartBody.Part?,
        @Part new_pan_card: MultipartBody.Part?
    ): ApiResponse

    @GET("get_compliance_docs")
    suspend fun getComplianceDocs(@Query("user_id") userId: String): ComplianceDocsResponse

    @Multipart
    @POST("update_compliance_docs")
    suspend fun updateComplianceDocs(
        @Part("user_id") userId: RequestBody,
        @Part document: MultipartBody.Part?
    ): UpdateDocsResponse

    @Multipart
    @POST("submit_bid")
    suspend fun submitBid(
        @Part("tender_id") tenderId: RequestBody,
        @Part("bidder_id") bidderId: RequestBody,
        @Part("bid_amount") bidAmount: RequestBody,
        @Part technicalDoc: MultipartBody.Part?,
        @Part financialDoc: MultipartBody.Part?,
        @Part emdReceipt: MultipartBody.Part?,
        @Part gstPanDoc: MultipartBody.Part?,
        @Part affidavitDoc: MultipartBody.Part?,
        @Part pastWorkDoc: MultipartBody.Part?,
        @Part finSolvencyDoc: MultipartBody.Part?
    ): ApiResponse

    @GET("api/my-bids/{userId}")
    suspend fun getMyBids(@Path("userId") userId: String): GetMyBidsResponse

    @GET("api/tender-comparison/{tenderId}/{userId}")
    suspend fun getTenderComparison(
        @Path("tenderId") tenderId: String,
        @Path("userId") userId: String
    ): TenderComparisonResponse

    @POST("api/delete_bid")
    suspend fun deleteBid(
        @Body request: Map<String, String>
    ): ApiResponse


    // ==========================================
    // 🟡 AUTHORITY / ADMIN ENDPOINTS
    // ==========================================

    @POST("api/admin/login")
    suspend fun loginAuthority(@Body request: AuthorityLoginRequest): ApiResponse

    @POST("api/admin/change_password")
    suspend fun changeAuthorityPassword(@Body request: AdminChangePasswordRequest): ChangePasswordResponse

    @Multipart
    @POST("api/admin/update_profile")
    suspend fun updateAuthorityProfile(
        @Part("admin_id") adminId: RequestBody,
        @Part("adminName") adminName: RequestBody,
        @Part("designation") designation: RequestBody,
        @Part("deptName") deptName: RequestBody,
        @Part("officeCode") officeCode: RequestBody,
        @Part("sector") sector: RequestBody,
        @Part("hqAddress") hqAddress: RequestBody,
        @Part("signatoryName") signatoryName: RequestBody,
        @Part("contactNumber") contactNumber: RequestBody,
        @Part profileImage: MultipartBody.Part?
    ): UpdateAuthorityProfileResponse

    @GET("api/admin/get_docs")
    suspend fun getAdminDocs(@Query("admin_id") adminId: String): AdminDocsResponse

    @Multipart
    @POST("api/admin/update_docs")
    suspend fun updateAdminDocs(
        @Part("admin_id") adminId: RequestBody,
        @Part authLetter: MultipartBody.Part? = null,
        @Part idProof: MultipartBody.Part? = null,
        @Part dscKey: MultipartBody.Part? = null,
        @Part deptReg: MultipartBody.Part? = null
    ): UpdateDocsResponse

    @Multipart
    @POST("api/admin/create_tender")
    suspend fun createTender(
        @Part("projectTitle") projectTitle: RequestBody,
        @Part("nitNumber") nitNumber: RequestBody,
        @Part("projectDescription") projectDescription: RequestBody,
        @Part("estimatedValue") estimatedValue: RequestBody,
        @Part("emdAmount") emdAmount: RequestBody,
        @Part("sector") sector: RequestBody,
        @Part("location") location: RequestBody,
        @Part("deadline") deadline: RequestBody,
        @Part("reqGstPan") reqGstPan: RequestBody,
        @Part("reqAffidavit") reqAffidavit: RequestBody,
        @Part("reqEmdReceipt") reqEmdReceipt: RequestBody,
        @Part("reqPastWork") reqPastWork: RequestBody,
        @Part("reqFinSolvency") reqFinSolvency: RequestBody,
        @Part("reqTechProposal") reqTechProposal: RequestBody,
        @Part("reqBoq") reqBoq: RequestBody,
        @Part tenderDocuments: List<MultipartBody.Part>?
    ): ApiResponse

    @POST("api/admin/update_tender")
    suspend fun updateTender(@Body request: UpdateTenderRequest): ApiResponse

    @Multipart
    @POST("api/admin/update_tender")
    suspend fun updateTenderWithFiles(
        @Part("tender_id") tender_id: RequestBody,
        @Part("title") title: RequestBody,
        @Part("est_value") est_value: RequestBody,
        @Part("emd_amount") emd_amount: RequestBody,
        @Part("location") location: RequestBody,
        @Part("description") description: RequestBody,
        @Part("deadline") deadline: RequestBody,
        @Part("retained_docs") retainedDocs: RequestBody?,
        @Part newDocuments: List<MultipartBody.Part>?
    ): ApiResponse

    @GET("api/get_tenders")
    suspend fun getAllTenders(): GetTendersResponse

    @GET("api/get_tender/{id}")
    suspend fun getTenderById(@Path("id") tenderId: String): SingleTenderResponse

    @GET("api/admin/get_bids/{tender_id}")
    suspend fun getBidsForTender(@Path("tender_id") tenderId: String): GetBidsResponse

    @GET("api/admin/get_bid_details/{bid_id}")
    suspend fun getBidDetails(@Path("bid_id") bidId: String): BidDetailResponse

    @FormUrlEncoded
    @POST("api/admin/review_bid")
    suspend fun reviewBid(
        @Field("bid_id") bidId: String,
        @Field("status") status: String,
        @Field("remarks") remarks: String
    ): ApiResponse

    @Multipart
    @POST("api/admin/register")
    suspend fun registerAuthority(
        @Part("adminName") adminName: RequestBody,
        @Part("designation") designation: RequestBody,
        @Part("email") email: RequestBody,
        @Part("password") password: RequestBody,
        @Part("deptName") deptName: RequestBody,
        @Part("officeCode") officeCode: RequestBody,
        @Part("sector") sector: RequestBody,
        @Part("hqAddress") hqAddress: RequestBody,
        @Part("signatoryName") signatoryName: RequestBody,
        @Part("contactNumber") contactNumber: RequestBody,
        @Part authLetter: MultipartBody.Part?,
        @Part idProof: MultipartBody.Part?,
        @Part dscKey: MultipartBody.Part?,
        @Part deptReg: MultipartBody.Part?
    ): ApiResponse

    @POST("api/admin/auto_award/{tender_id}")
    suspend fun autoAwardTender(@Path("tender_id") tenderId: String): ApiResponse

    @POST("api/admin/delete_tender")
    suspend fun deleteTender(
        @Body request: Map<String, String>
    ): ApiResponse
}

object ApiClient {
    const val BASE_URL = "https://tender-analyser-api-v1-5.onrender.com"

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val apiService: ApiService = retrofit.create(ApiService::class.java)
}