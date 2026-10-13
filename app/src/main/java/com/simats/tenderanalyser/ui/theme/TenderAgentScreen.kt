package com.simats.tenderanalyser.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.tenderanalyser.bidderhome.ChatMessage
import com.simats.tenderanalyser.bidderhome.TenderAiViewModel
import com.simats.tenderanalyser.swipeToNavigate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TenderAgentScreen(
    viewModel: TenderAiViewModel,
    onBackClick: () -> Unit
) {
    val chatHistory by viewModel.chatHistory.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    // Ensure global assistant context is set when entering the global agent screen
    LaunchedEffect(Unit) {
        viewModel.setTenderContext(
            tenderId = "GLOBAL_PLATFORM",
            details = "General Tender Analyser Assistant. Help the bidder explore active tenders, understand bidding guidelines, review document requirements, and navigate platform features."
        )
    }

    // Auto-scroll to the bottom when new messages arrive
    LaunchedEffect(chatHistory.size) {
        if (chatHistory.isNotEmpty()) {
            listState.animateScrollToItem(chatHistory.size - 1)
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .swipeToNavigate(
                onSwipeRight = { onBackClick() }
            ),
        topBar = {
            Surface(shadowElevation = 4.dp) {
                Column(modifier = Modifier.background(Color(0xFF0F172A)).statusBarsPadding()) {
                    Row(
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = onBackClick) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(Icons.Default.AutoAwesome, contentDescription = "AI", tint = Color(0xFFFFC107), modifier = Modifier.size(22.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Global Tender Assistant", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text("Platform & Tender Guidance", color = Color(0xFF93C5FD), fontSize = 11.sp)
                        }
                    }
                }
            }
        },
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shadowElevation = 8.dp,
                color = Color.White
            ) {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .navigationBarsPadding()
                        .imePadding(), // Keeps input visible above keyboard
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("Ask about tenders, documents, or rules...", fontSize = 13.sp) },
                        shape = RoundedCornerShape(24.dp),
                        maxLines = 3,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF2563EB),
                            unfocusedBorderColor = Color(0xFFE2E8F0)
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))

                    FloatingActionButton(
                        onClick = {
                            val query = inputText.trim()
                            if (query.isNotBlank() && !isLoading) {
                                viewModel.askQuestion(query)
                                inputText = ""
                            }
                        },
                        containerColor = Color(0xFF2563EB),
                        contentColor = Color.White,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.size(48.dp)
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                        } else {
                            Icon(Icons.Default.Send, contentDescription = "Send", modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }
        },
        containerColor = Color(0xFFF8FAFC)
    ) { paddingValues ->
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 16.dp)
        ) {
            items(chatHistory) { message ->
                GlobalChatBubble(message)
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
fun GlobalChatBubble(message: ChatMessage) {
    val isUser = message.isUser
    val alignment = if (isUser) Alignment.End else Alignment.Start
    val bubbleColor = if (isUser) Color(0xFF2563EB) else Color.White
    val textColor = if (isUser) Color.White else Color(0xFF1E293B)
    val shape = if (isUser) {
        RoundedCornerShape(16.dp, 16.dp, 4.dp, 16.dp)
    } else {
        RoundedCornerShape(16.dp, 16.dp, 16.dp, 4.dp)
    }

    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = alignment) {
        Surface(
            color = bubbleColor,
            shape = shape,
            shadowElevation = 1.dp,
            border = if (!isUser) BorderStroke(1.dp, Color(0xFFE2E8F0)) else null
        ) {
            Text(
                text = message.text,
                modifier = Modifier.padding(12.dp),
                color = textColor,
                fontSize = 13.sp,
                lineHeight = 18.sp
            )
        }
        if (!isUser) {
            Text(
                text = "Tender AI",
                fontSize = 10.sp,
                color = Color.Gray,
                modifier = Modifier.padding(start = 4.dp, top = 2.dp)
            )
        }
    }
}