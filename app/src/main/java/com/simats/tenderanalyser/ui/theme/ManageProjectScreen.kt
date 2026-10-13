package com.simats.tenderanalyser.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.tenderanalyser.ApiClient
import com.simats.tenderanalyser.TenderDetail
import kotlinx.coroutines.launch
import retrofit2.HttpException

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageProjectScreen(
    tenderId: String,
    onBackClick: () -> Unit,
    onEditClick: (String) -> Unit = {},
    onViewBidsClick: (String) -> Unit = {},
    onViewAnalyticsClick: (String) -> Unit = {},
    onDeleteClick: (String) -> Unit = {}
) {
    val context = LocalContext.current
    var tender by remember { mutableStateOf<TenderDetail?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    val coroutineScope = rememberCoroutineScope()

    val darkBg = Color(0xFF121212)
    val cardBg = Color(0xFF1E1E1E)
    val goldAccent = Color(0xFFFFC107)
    val textGray = Color(0xFFA0A0A0)

    // Fetch the specific tender data
    LaunchedEffect(tenderId) {
        try {
            val response = ApiClient.apiService.getTenderById(tenderId)
            if (response.success && response.tender != null) {
                tender = response.tender
            } else {
                Toast.makeText(context, "Error: ${response.message}", Toast.LENGTH_SHORT).show()
            }
        } catch (e: HttpException) {
            Toast.makeText(context, "Server Error ${e.code()}", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(context, "Network Error", Toast.LENGTH_SHORT).show()
        } finally {
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Manage Project", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = darkBg)
            )
        },
        containerColor = darkBg
    ) { padding ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = goldAccent)
            }
        } else if (tender == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Failed to load tender details.", color = textGray)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                // STATUS BADGE
                Surface(color = Color(0xFF003314), shape = RoundedCornerShape(4.dp)) {
                    Text(
                        text = tender!!.status.uppercase(),
                        color = Color(0xFF10B981),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // TITLE & NIT
                Text(tender!!.projectTitle, color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Text("NIT: ${tender!!.nitNumber}", color = textGray, fontSize = 14.sp)

                Spacer(modifier = Modifier.height(24.dp))

                // QUICK STATS CARD
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = cardBg),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        DetailRow("Estimated Value", tender!!.estimatedValue)
                        HorizontalDivider(color = Color(0xFF333333), modifier = Modifier.padding(vertical = 12.dp))
                        DetailRow("EMD Amount", tender!!.emdAmount)
                        HorizontalDivider(color = Color(0xFF333333), modifier = Modifier.padding(vertical = 12.dp))
                        DetailRow("Deadline", tender!!.deadline)
                        HorizontalDivider(color = Color(0xFF333333), modifier = Modifier.padding(vertical = 12.dp))
                        DetailRow("Location", tender!!.location)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // DESCRIPTION
                Text("Project Description", color = goldAccent, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Text(tender!!.projectDescription, color = Color.White, fontSize = 14.sp, lineHeight = 20.sp)

                Spacer(modifier = Modifier.height(32.dp))

                // 1. VIEW SUBMITTED BIDS
                Button(
                    onClick = { onViewBidsClick(tender!!.id) },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = goldAccent),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Outlined.Group, contentDescription = null, tint = Color.Black)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("View Submitted Bids", color = Color.Black, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(12.dp))

                // 2. VIEW LIVE BIDDING ANALYTICS (NEW)
                OutlinedButton(
                    onClick = { onViewAnalyticsClick(tender!!.id) },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, goldAccent),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = goldAccent),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.Leaderboard, contentDescription = null, tint = goldAccent)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("View Bidding Analytics", color = goldAccent, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(12.dp))

                // 3. EDIT TENDER DETAILS
                Button(
                    onClick = { onEditClick(tender!!.id) },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = cardBg),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Outlined.Edit, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Edit Tender Details", color = Color.White, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(12.dp))

                // 4. DELETE TENDER BUTTON
                Button(
                    onClick = {
                        coroutineScope.launch {
                            try {
                                val requestMap = mapOf("tender_id" to tender!!.id)
                                val response = ApiClient.apiService.deleteTender(requestMap)
                                if (response.success) {
                                    Toast.makeText(context, "Tender deleted successfully", Toast.LENGTH_SHORT).show()
                                    onDeleteClick(tender!!.id)
                                    onBackClick()
                                } else {
                                    Toast.makeText(context, response.message ?: "Failed to delete tender", Toast.LENGTH_LONG).show()
                                }
                            } catch (e: Exception) {
                                Toast.makeText(context, "Error: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.Delete, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Delete Tender", color = Color.White, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, color = Color(0xFFA0A0A0), fontSize = 14.sp)
        Text(value, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
    }
}