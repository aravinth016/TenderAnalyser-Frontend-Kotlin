package com.simats.tenderanalyser

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.simats.tenderanalyser.bidderhome.TenderAiViewModel
import com.simats.tenderanalyser.bidderhome.TenderDetails
import com.simats.tenderanalyser.screens.AboutUsScreen
import com.simats.tenderanalyser.screens.AuthorityAboutUsScreen
import com.simats.tenderanalyser.screens.AuthorityAccountDetailsScreen
import com.simats.tenderanalyser.screens.AuthorityAwardDetailsScreen
import com.simats.tenderanalyser.screens.AuthorityAwardSuccessScreen
import com.simats.tenderanalyser.screens.AuthorityBidReviewScreen
import com.simats.tenderanalyser.screens.AuthorityBiddingAnalyticsScreen
import com.simats.tenderanalyser.screens.AuthorityBidsReceivedScreen
import com.simats.tenderanalyser.screens.AuthorityChangePasswordScreen
import com.simats.tenderanalyser.screens.AuthorityDashboardScreen
import com.simats.tenderanalyser.screens.AuthorityDepartmentInfoScreen
import com.simats.tenderanalyser.screens.AuthorityDocumentViewerScreen
import com.simats.tenderanalyser.screens.AuthorityEditAccountScreen
import com.simats.tenderanalyser.screens.AuthorityEditProfileScreen
import com.simats.tenderanalyser.screens.AuthorityEditTenderScreen
import com.simats.tenderanalyser.screens.AuthorityEditTimelineScreen
import com.simats.tenderanalyser.screens.AuthorityGenerateAwardScreen
import com.simats.tenderanalyser.screens.AuthorityHelpCenterScreen
import com.simats.tenderanalyser.screens.AuthorityLoginScreen
import com.simats.tenderanalyser.screens.AuthorityManageTendersScreen
import com.simats.tenderanalyser.screens.AuthorityPreviewAwardScreen
import com.simats.tenderanalyser.screens.AuthorityProfileScreen
import com.simats.tenderanalyser.screens.AuthorityRegStep1Screen
import com.simats.tenderanalyser.screens.AuthorityRegStep2Screen
import com.simats.tenderanalyser.screens.AuthorityRegStep3Screen
import com.simats.tenderanalyser.screens.AuthorityRegSuccessScreen
import com.simats.tenderanalyser.screens.AuthoritySignedLetterViewerScreen
import com.simats.tenderanalyser.screens.AuthorityTenderDetailsScreen
import com.simats.tenderanalyser.screens.AuthorityTermsScreen
import com.simats.tenderanalyser.screens.AuthorityVerificationHubScreen
import com.simats.tenderanalyser.screens.BidFlowScreen
import com.simats.tenderanalyser.screens.BidderAwardLetterScreen
import com.simats.tenderanalyser.screens.BidderChangePasswordScreen
import com.simats.tenderanalyser.screens.BidderDashboardScreen
import com.simats.tenderanalyser.screens.BidderPasswordSuccessScreen
import com.simats.tenderanalyser.screens.ComparisonAnalyticsScreen
import com.simats.tenderanalyser.screens.ContactUsScreen
import com.simats.tenderanalyser.screens.CreateTenderStep1Screen
import com.simats.tenderanalyser.screens.CreateTenderStep2Screen
import com.simats.tenderanalyser.screens.CreateTenderStep3Screen
import com.simats.tenderanalyser.screens.EditProfileScreen
import com.simats.tenderanalyser.screens.ManageGstPanScreen
import com.simats.tenderanalyser.screens.ManageProjectScreen
import com.simats.tenderanalyser.screens.MyBidsScreen
import com.simats.tenderanalyser.screens.PortalScreen
import com.simats.tenderanalyser.screens.RequiredDocumentsScreen
import com.simats.tenderanalyser.screens.TenderAgentScreen
import com.simats.tenderanalyser.screens.TenderPublishSuccessScreen
import com.simats.tenderanalyser.screens.TermsScreen
import kotlinx.coroutines.launch
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                val navController = rememberNavController()
                val context = LocalContext.current

                val sharedPrefSession = context.getSharedPreferences("AppSession", Context.MODE_PRIVATE)
                val isLoggedIn = sharedPrefSession.getBoolean("IS_LOGGED_IN", false)
                val userRole = sharedPrefSession.getString("USER_ROLE", "")

                val startDestination = if (isLoggedIn) {
                    if (userRole == "authority") "authority_dashboard" else "dashboard"
                } else {
                    "portalscreen"
                }

                val registrationViewModel: RegistrationViewModel = viewModel()
                val authorityViewModel: AuthorityViewModel = viewModel()
                val createTenderViewModel: AuthorityCreateTenderViewModel = viewModel()
                val aiViewModel: TenderAiViewModel = viewModel()
                val bidderSettingsViewModel: BidderSettingsViewModel = viewModel()

                NavHost(
                    navController = navController,
                    startDestination = startDestination,
                    enterTransition = { fadeIn(animationSpec = tween(0)) },
                    exitTransition = { fadeOut(animationSpec = tween(0)) },
                    popEnterTransition = { fadeIn(animationSpec = tween(0)) },
                    popExitTransition = { fadeOut(animationSpec = tween(0)) }
                ) {
                    composable("portalscreen") {
                        PortalScreen(
                            onBidderSignUpClick = { navController.navigate("register_step_1") },
                            onBidderLoginClick = { navController.navigate("login") },
                            onAuthoritySignUpClick = { navController.navigate("auth_register_step_1") },
                            onAuthorityLoginClick = { navController.navigate("authority_login") }
                        )
                    }

                    // ==========================================
                    // 🏛️ AUTHORITY PORTAL
                    // ==========================================
                    composable("authority_login") {
                        val coroutineScope = rememberCoroutineScope()
                        AuthorityLoginScreen(
                            onLoginClick = { email, password ->
                                if (email.isBlank() || password.isBlank()) {
                                    Toast.makeText(context, "Please enter both Email and Password.", Toast.LENGTH_SHORT).show()
                                    return@AuthorityLoginScreen
                                }
                                coroutineScope.launch {
                                    try {
                                        val request = AuthorityLoginRequest(email, password)
                                        val response = ApiClient.apiService.loginAuthority(request)
                                        if (response.success && response.admin != null) {
                                            val sharedPref = context.getSharedPreferences("AppSession", Context.MODE_PRIVATE)
                                            sharedPref.edit().apply {
                                                putBoolean("IS_LOGGED_IN", true)
                                                putString("USER_ROLE", "authority")
                                                putString("ADMIN_ID", response.admin.id.toString())
                                                putString("USER_ID", response.admin.id.toString())
                                                putString("ADMIN_NAME", response.admin.adminName)
                                                putString("ADMIN_DESIGNATION", response.admin.designation)
                                                putString("ADMIN_DEPT", response.admin.deptName)
                                                putString("ADMIN_OFFICE_CODE", response.admin.officeCode)
                                                putString("ADMIN_SECTOR", response.admin.sector)
                                                putString("ADMIN_ADDRESS", response.admin.hqAddress)
                                                putString("ADMIN_SIGNATORY", response.admin.signatoryName)
                                                putString("ADMIN_CONTACT", response.admin.contactNumber)
                                                putString("ADMIN_EMAIL", response.admin.email)
                                                putString("ADMIN_STATUS", response.admin.status)
                                                putString("ADMIN_PROFILE_PIC", response.admin.profileImageUrl ?: "")
                                                apply()
                                            }

                                            Toast.makeText(context, "Welcome Admin!", Toast.LENGTH_SHORT).show()
                                            navController.navigate("authority_dashboard") {
                                                popUpTo("portalscreen") { inclusive = false }
                                            }
                                        } else {
                                            Toast.makeText(context, response.message ?: "Invalid Credentials", Toast.LENGTH_LONG).show()
                                        }
                                    } catch (e: Exception) {
                                        Toast.makeText(context, "Server Error.", Toast.LENGTH_LONG).show()
                                    }
                                }
                            },
                            onForgotClick = {},
                            onRegisterClick = { navController.navigate("auth_register_step_1") },
                            onBackClick = { navController.popBackStack() }
                        )
                    }

                    composable("auth_register_step_1") { AuthorityRegStep1Screen(viewModel = authorityViewModel, onNextClick = { navController.navigate("auth_register_step_2") }, onBackClick = { navController.popBackStack() }) }
                    composable("auth_register_step_2") { AuthorityRegStep2Screen(viewModel = authorityViewModel, onNextClick = { navController.navigate("auth_register_step_3") }, onBackClick = { navController.popBackStack() }) }
                    composable("auth_register_step_3") { AuthorityRegStep3Screen(viewModel = authorityViewModel, onNextClick = { navController.navigate("auth_register_success") }, onBackClick = { navController.popBackStack() }) }
                    composable("auth_register_success") { AuthorityRegSuccessScreen(onReturnClick = { navController.navigate("authority_login") { popUpTo("portalscreen") { inclusive = false } } }) }

                    composable("authority_dashboard") {
                        AuthorityDashboardScreen(
                            onCreateNewClick = { navController.navigate("create_tender_step_1") },
                            onManageProjectClick = { tenderId -> navController.navigate("manage_project/$tenderId") },
                            onTenderImageClick = { tenderId -> navController.navigate("authority_tender_details/$tenderId") },
                            onNavBidsClick = { navController.navigate("authority_manage_tenders") },
                            onNavProfileClick = { navController.navigate("authority_profile") }
                        )
                    }

                    composable(
                        route = "manage_project/{tenderId}",
                        arguments = listOf(navArgument("tenderId") { type = NavType.StringType })
                    ) { backStackEntry ->
                        val tenderId = backStackEntry.arguments?.getString("tenderId") ?: ""
                        val coroutineScope = rememberCoroutineScope()
                        ManageProjectScreen(
                            tenderId = tenderId,
                            onBackClick = { navController.popBackStack() },
                            onEditClick = { id -> navController.navigate("authority_edit_tender/$id") },
                            onViewBidsClick = { id -> navController.navigate("authority_bids_received/$id") },
                            onViewAnalyticsClick = { id -> navController.navigate("authority_bidding_analytics/$id") },
                            onDeleteClick = { id ->
                                coroutineScope.launch {
                                    try {
                                        val requestMap = mapOf("tender_id" to id)
                                        val response = ApiClient.apiService.deleteTender(requestMap)
                                        if (response.success) {
                                            Toast.makeText(context, "Tender deleted successfully", Toast.LENGTH_SHORT).show()
                                            navController.popBackStack()
                                        } else {
                                            Toast.makeText(context, response.message ?: "Deletion failed", Toast.LENGTH_SHORT).show()
                                        }
                                    } catch (e: Exception) {
                                        Toast.makeText(context, "Network Error. Check connection.", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        )
                    }

                    composable("authority_manage_tenders") {
                        AuthorityManageTendersScreen(
                            onNavTendersClick = { navController.navigate("authority_dashboard") { popUpTo("authority_dashboard") { inclusive = true } } },
                            onNavProfileClick = { navController.navigate("authority_profile") },
                            onManageSubmissionsClick = { tenderId -> navController.navigate("authority_bids_received/$tenderId") },
                            onViewAwardClick = { tenderId -> navController.navigate("authority_award_details/$tenderId") }
                        )
                    }

                    composable("authority_profile") {
                        AuthorityProfileScreen(
                            onNavTendersClick = { navController.navigate("authority_dashboard") { popUpTo("authority_dashboard") { inclusive = true } } },
                            onNavBidsClick = { navController.navigate("authority_manage_tenders") { popUpTo("authority_dashboard") { inclusive = false } } },
                            onLogoutClick = {
                                context.getSharedPreferences("AppSession", Context.MODE_PRIVATE).edit().clear().apply()
                                navController.navigate("portalscreen") { popUpTo(0) { inclusive = true } }
                            },
                            onNavigateToSetting = { route ->
                                when(route) {
                                    "authority_about" -> navController.navigate("authority_about_us")
                                    "authority_terms" -> navController.navigate("authority_terms")
                                    "authority_help" -> navController.navigate("authority_help_center")
                                    "docs" -> navController.navigate("authority_verification_hub")
                                    "dept" -> navController.navigate("authority_department_info")
                                    "account" -> navController.navigate("authority_account_details")
                                    "password" -> navController.navigate("authority_change_password")
                                    "authority_edit_profile" -> navController.navigate("authority_edit_profile")
                                }
                            }
                        )
                    }

                    composable("authority_edit_profile") { AuthorityEditProfileScreen(onBackClick = { navController.popBackStack() }) }

                    composable(
                        route = "authority_bids_received/{tenderId}",
                        arguments = listOf(navArgument("tenderId") { type = NavType.StringType })
                    ) { backStackEntry ->
                        val tenderId = backStackEntry.arguments?.getString("tenderId") ?: ""
                        AuthorityBidsReceivedScreen(
                            tenderId = tenderId,
                            onBackClick = { navController.popBackStack() },
                            onReviewBidClick = { bidId -> navController.navigate("authority_bid_review/$bidId") },
                            onViewAnalyticsClick = { id -> navController.navigate("authority_bidding_analytics/$id") }
                        )
                    }

                    composable(
                        route = "authority_bid_review/{bidId}",
                        arguments = listOf(navArgument("bidId") { type = NavType.StringType })
                    ) { backStackEntry ->
                        val bidId = backStackEntry.arguments?.getString("bidId") ?: ""
                        AuthorityBidReviewScreen(
                            bidId = bidId,
                            onBackClick = { navController.popBackStack() }
                        )
                    }

                    composable(route = "authority_document_viewer/{docType}") { backStackEntry ->
                        val docType = backStackEntry.arguments?.getString("docType") ?: "technical"
                        AuthorityDocumentViewerScreen(docType = docType, onBackClick = { navController.popBackStack() })
                    }

                    composable(route = "authority_generate_award/{bidId}") { backStackEntry ->
                        val bidId = backStackEntry.arguments?.getString("bidId") ?: ""
                        AuthorityGenerateAwardScreen(onBackClick = { navController.popBackStack() }, onPreviewClick = { navController.navigate("authority_preview_award/$bidId") }, onGenerateClick = { navController.navigate("authority_award_success") })
                    }

                    composable(route = "authority_preview_award/{bidId}") { AuthorityPreviewAwardScreen(onBackClick = { navController.popBackStack() }, onIssueClick = { navController.navigate("authority_award_success") }) }

                    composable("authority_award_success") {
                        AuthorityAwardSuccessScreen(onHomeClick = { navController.navigate("authority_dashboard") { popUpTo("portalscreen") { inclusive = false } } })
                    }

                    composable(route = "authority_award_details/{tenderId}") { backStackEntry ->
                        val tenderId = backStackEntry.arguments?.getString("tenderId") ?: ""
                        AuthorityAwardDetailsScreen(
                            tenderId = tenderId,
                            onBackClick = { navController.popBackStack() },
                            onAnalyticsClick = { navController.navigate("authority_bidding_analytics/$tenderId") },
                            onViewLetterClick = { navController.navigate("authority_signed_letter/$tenderId") }
                        )
                    }

                    composable(
                        route = "authority_bidding_analytics/{tenderId}",
                        arguments = listOf(navArgument("tenderId") { type = NavType.StringType })
                    ) { backStackEntry ->
                        val tenderId = backStackEntry.arguments?.getString("tenderId") ?: ""
                        AuthorityBiddingAnalyticsScreen(
                            tenderId = tenderId,
                            onBackClick = { navController.popBackStack() }
                        )
                    }

                    composable(route = "authority_signed_letter/{tenderId}") { AuthoritySignedLetterViewerScreen(onBackClick = { navController.popBackStack() }) }

                    composable(route = "authority_tender_details/{tenderId}") { backStackEntry ->
                        val tenderId = backStackEntry.arguments?.getString("tenderId") ?: ""
                        AuthorityTenderDetailsScreen(
                            tenderId = tenderId,
                            onBackClick = { navController.popBackStack() },
                            onEditTenderClick = { navController.navigate("authority_edit_tender/$tenderId") },
                            onEditTimelineClick = { navController.navigate("authority_edit_timeline/$tenderId") },
                            onShareClick = { Toast.makeText(context, "Sharing Options", Toast.LENGTH_SHORT).show() }
                        )
                    }

                    composable(route = "authority_edit_timeline/{tenderId}") { backStackEntry ->
                        val tenderId = backStackEntry.arguments?.getString("tenderId") ?: ""
                        AuthorityEditTimelineScreen(tenderId = tenderId, onCloseClick = { navController.popBackStack() }, onSaveSuccess = { navController.popBackStack() })
                    }

                    composable(route = "authority_edit_tender/{tenderId}") { backStackEntry ->
                        val tenderId = backStackEntry.arguments?.getString("tenderId") ?: ""
                        AuthorityEditTenderScreen(
                            tenderId = tenderId,
                            onBackClick = { navController.popBackStack() },
                            onUpdateClick = { navController.popBackStack() }
                        )
                    }

                    composable("authority_terms") { AuthorityTermsScreen(onBackClick = { navController.popBackStack() }) }
                    composable("authority_help_center") { AuthorityHelpCenterScreen(onBackClick = { navController.popBackStack() }) }
                    composable("authority_about_us") { AuthorityAboutUsScreen(onBackClick = { navController.popBackStack() }) }
                    composable("authority_verification_hub") { AuthorityVerificationHubScreen(onBackClick = { navController.popBackStack() }) }
                    composable("authority_department_info") { AuthorityDepartmentInfoScreen(onBackClick = { navController.popBackStack() }) }

                    composable("authority_account_details") {
                        AuthorityAccountDetailsScreen(
                            onBackClick = { navController.popBackStack() },
                            onEditClick = { navController.navigate("authority_edit_account") }
                        )
                    }
                    composable("authority_edit_account") {
                        AuthorityEditAccountScreen(
                            onBackClick = { navController.popBackStack() },
                            onSaveSuccess = { navController.popBackStack() }
                        )
                    }

                    composable("authority_change_password") { AuthorityChangePasswordScreen(onBackClick = { navController.popBackStack() }) }

                    composable("create_tender_step_1") { CreateTenderStep1Screen(viewModel = createTenderViewModel, onNextClick = { navController.navigate("create_tender_step_2") }, onBackClick = { navController.popBackStack() }) }
                    composable("create_tender_step_2") { CreateTenderStep2Screen(viewModel = createTenderViewModel, onNextClick = { navController.navigate("create_tender_step_3") }, onBackClick = { navController.popBackStack() }) }
                    composable("create_tender_step_3") { CreateTenderStep3Screen(viewModel = createTenderViewModel, onPublishClick = { navController.navigate("tender_publish_success") }, onBackClick = { navController.popBackStack() }) }
                    composable("tender_publish_success") {
                        TenderPublishSuccessScreen(
                            onViewLiveClick = { navController.navigate("authority_dashboard") },
                            onReturnHomeClick = { navController.navigate("authority_dashboard") { popUpTo("portalscreen") { inclusive = false } } }
                        )
                    }

                    // ==========================================
                    // 🔵 BIDDER PORTAL
                    // ==========================================
                    composable("login") {
                        BidderPortalLoginScreen(
                            onLoginClick = { navController.navigate("dashboard") { popUpTo(0) { inclusive = true } } },
                            onForgotClick = { navController.navigate("forgot_password") },
                            onBackClick = { navController.popBackStack() },
                            onRegisterClick = { navController.navigate("register_step_1") }
                        )
                    }

                    composable("register_step_1") {
                        RegistrationStep1Screen(
                            viewModel = registrationViewModel,
                            onNextClick = { navController.navigate("register_step_2") },
                            onBackClick = { navController.popBackStack() },
                            onLoginClick = { navController.navigate("login") { popUpTo("portalscreen") { inclusive = false } } }
                        )
                    }

                    composable("register_step_2") {
                        RegistrationStep2Screen(
                            viewModel = registrationViewModel,
                            onBackClick = { navController.popBackStack() },
                            onRegistrationSuccess = { navController.navigate("registration_success") { popUpTo("portalscreen") { inclusive = false } } }
                        )
                    }

                    composable("registration_success") {
                        RegistrationSuccessScreen(onLoginClick = { navController.navigate("login") { popUpTo("portalscreen") { inclusive = false } } })
                    }

                    composable("forgot_password") { ForgotPasswordScreen() }

                    composable("dashboard") {
                        BidderDashboardScreen(
                            onTenderClick = { tenderId ->
                                val safeId = URLEncoder.encode(tenderId, StandardCharsets.UTF_8.toString())
                                navController.navigate("tender_details/$safeId")
                            },
                            onLogoutClick = {
                                context.getSharedPreferences("AppSession", Context.MODE_PRIVATE).edit().clear().apply()
                                navController.navigate("portalscreen") { popUpTo(0) { inclusive = true } }
                            },
                            onAIAgentClick = { navController.navigate("tender_agent") },
                            onBidsClick = { navController.navigate("my_bids") },
                            onProfileClick = { navController.navigate("profile") }
                        )
                    }

                    composable(
                        route = "tender_details/{tenderId}",
                        arguments = listOf(navArgument("tenderId") { type = NavType.StringType })
                    ) { backStackEntry ->
                        val tenderId = backStackEntry.arguments?.getString("tenderId") ?: "1"

                        TenderDetails(
                            tenderId = tenderId,
                            onBackClick = { navController.popBackStack() },
                            onAnalyzeClick = {
                                aiViewModel.setTenderContext(tenderId, "Analysis for ID: $tenderId")
                                navController.navigate("tender_agent")
                            },
                            onBidClick = {
                                val safeId = URLEncoder.encode(tenderId, StandardCharsets.UTF_8.toString())
                                navController.navigate("new_bid_flow/$safeId")
                            },
                            onNavigateToMyBids = {
                                navController.navigate("my_bids")
                            },
                            onNavigateToDashboard = {
                                navController.navigate("dashboard") { popUpTo("dashboard") { inclusive = true } }
                            }
                        )
                    }

                    composable("tender_agent") {
                        TenderAgentScreen(viewModel = aiViewModel, onBackClick = { navController.popBackStack() })
                    }

                    composable("my_bids") {
                        val coroutineScope = rememberCoroutineScope()
                        val sharedPrefs = context.getSharedPreferences("AppSession", Context.MODE_PRIVATE)
                        val currentUserId = sharedPrefs.getString("USER_ID", "1") ?: "1"

                        MyBidsScreen(
                            onNavigateBack = { navController.popBackStack() },
                            onEditBidClick = { tenderId ->
                                val safeId = URLEncoder.encode(tenderId, StandardCharsets.UTF_8.toString())
                                navController.navigate("new_bid_flow/$safeId")
                            },
                            onDeleteBidClick = { tenderId ->
                                coroutineScope.launch {
                                    try {
                                        val requestMap = mapOf(
                                            "tender_id" to tenderId,
                                            "user_id" to currentUserId
                                        )
                                        val response = ApiClient.apiService.deleteBid(requestMap)
                                        if (response.success) {
                                            Toast.makeText(context, "Bid withdrawn successfully", Toast.LENGTH_SHORT).show()
                                            navController.navigate("my_bids") {
                                                popUpTo("my_bids") { inclusive = true }
                                            }
                                        } else {
                                            Toast.makeText(context, response.message ?: "Failed to withdraw bid", Toast.LENGTH_SHORT).show()
                                        }
                                    } catch (e: Exception) {
                                        Toast.makeText(context, "Network error withdrawing bid", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            },
                            onViewAnalyticsClick = { tenderId, isAwarded ->
                                val safeId = URLEncoder.encode(tenderId, StandardCharsets.UTF_8.toString())
                                navController.navigate("awarded_status/$safeId?isAwarded=$isAwarded")
                            },
                            onNavigateToHome = {
                                navController.navigate("dashboard") { popUpTo("dashboard") { inclusive = true } }
                            },
                            onNavigateToProfile = { navController.navigate("profile") }
                        )
                    }

                    composable(
                        route = "awarded_status/{tenderId}?isAwarded={isAwarded}",
                        arguments = listOf(
                            navArgument("tenderId") { type = NavType.StringType },
                            navArgument("isAwarded") { type = NavType.BoolType; defaultValue = true }
                        )
                    ) { backStackEntry ->
                        val tenderId = backStackEntry.arguments?.getString("tenderId") ?: ""
                        val isAwarded = backStackEntry.arguments?.getBoolean("isAwarded") ?: true

                        BidderAwardLetterScreen(
                            tenderId = tenderId,
                            isAwarded = isAwarded,
                            onBackClick = { navController.popBackStack() },
                            onViewAnalyticsClick = { id ->
                                val safeId = URLEncoder.encode(id, StandardCharsets.UTF_8.toString())
                                navController.navigate("comparison_analytics/$safeId")
                            }
                        )
                    }

                    // 2. Competitive Comparison Analytics Screen
                    composable(
                        route = "comparison_analytics/{tenderId}",
                        arguments = listOf(navArgument("tenderId") { type = NavType.StringType })
                    ) { backStackEntry ->
                        val tenderId = backStackEntry.arguments?.getString("tenderId") ?: ""
                        val sharedPrefs = context.getSharedPreferences("AppSession", Context.MODE_PRIVATE)
                        val userId = sharedPrefs.getString("USER_ID", "1") ?: "1"

                        ComparisonAnalyticsScreen(
                            tenderId = tenderId,
                            userId = userId,
                            onBack = { navController.popBackStack() }
                        )
                    }

                    composable(
                        route = "new_bid_flow/{tenderId}",
                        arguments = listOf(navArgument("tenderId") { type = NavType.StringType })
                    ) { backStackEntry ->
                        val tenderId = backStackEntry.arguments?.getString("tenderId") ?: "1"
                        BidFlowScreen(
                            tenderId = tenderId,
                            onNavigateToDashboard = {
                                navController.navigate("dashboard") { popUpTo("dashboard") { inclusive = true } }
                            }
                        )
                    }

                    composable("profile") {
                        ProfileScreen(
                            navController = navController,
                            onBackClick = { navController.popBackStack() },
                            onLogoutClick = {
                                context.getSharedPreferences("AppSession", Context.MODE_PRIVATE).edit().clear().apply()
                                navController.navigate("portalscreen") { popUpTo(0) { inclusive = true } }
                            },
                            onNavigate = { route -> navController.navigate(route) }
                        )
                    }

                    composable("edit_profile") { EditProfileScreen(onBackClick = { navController.popBackStack() }, onSaveClick = { navController.popBackStack() }) }
                    composable("manage_gst") { ManageGstPanScreen(onBackClick = { navController.popBackStack() }) }
                    composable("required_documents") { RequiredDocumentsScreen(onBackClick = { navController.popBackStack() }) }

                    composable("bidder_change_password") {
                        val sharedPrefs = context.getSharedPreferences("AppSession", Context.MODE_PRIVATE)
                        val currentUserId = sharedPrefs.getString("USER_ID", "1") ?: "1"

                        BidderChangePasswordScreen(
                            viewModel = bidderSettingsViewModel,
                            userId = currentUserId,
                            onBackClick = { navController.popBackStack() },
                            onSuccess = { navController.navigate("bidder_password_success") }
                        )
                    }

                    composable("bidder_password_success") {
                        BidderPasswordSuccessScreen(
                            onDoneClick = {
                                navController.popBackStack("profile", inclusive = false)
                            }
                        )
                    }

                    composable("terms") { TermsScreen(onBackClick = { navController.popBackStack() }) }
                    composable("about_us") { AboutUsScreen(onBackClick = { navController.popBackStack() }) }
                    composable("contact_us") { ContactUsScreen(onBackClick = { navController.popBackStack() }) }
                }
            }
        }
    }
}