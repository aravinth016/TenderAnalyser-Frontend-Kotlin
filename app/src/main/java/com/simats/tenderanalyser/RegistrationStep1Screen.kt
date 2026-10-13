package com.simats.tenderanalyser

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrationStep1Screen(
    viewModel: RegistrationViewModel, // NEW: Accepts the ViewModel
    onNextClick: () -> Unit,
    onBackClick: () -> Unit,
    onLoginClick: () -> Unit
) {
    var fullName by remember { mutableStateOf("") }
    var designation by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var companyName by remember { mutableStateOf("") }
    var gstin by remember { mutableStateOf("") }

    var turnoverExpanded by remember { mutableStateOf(false) }
    var selectedTurnover by remember { mutableStateOf("Select") }
    var businessTypeExpanded by remember { mutableStateOf(false) }
    var selectedBusinessType by remember { mutableStateOf("Select") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Bidder Registration", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = TextDark) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextDark) }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color.White
    ) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValues).verticalScroll(rememberScrollState()).padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Step Indicator
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(modifier = Modifier.size(32.dp).background(PrimaryBlue, CircleShape), contentAlignment = Alignment.Center) {
                        Text("1", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("INFO", fontSize = 10.sp, color = PrimaryBlue, fontWeight = FontWeight.Bold)
                }
                HorizontalDivider(modifier = Modifier.width(60.dp).padding(horizontal = 8.dp).padding(bottom = 16.dp), color = BorderLight, thickness = 2.dp)
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(modifier = Modifier.size(32.dp).background(BackgroundColor, CircleShape), contentAlignment = Alignment.Center) {
                        Text("2", color = TextGray, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("VERIFY", fontSize = 10.sp, color = TextGray, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            SectionTitle("USER INFORMATION")
            CustomTextField("FULL NAME", "Johnathan Doe", fullName) { fullName = it }
            CustomTextField("DESIGNATION", "e.g. Procurement Manager", designation) { designation = it }
            CustomTextField("OFFICIAL EMAIL", "john@company.com", email) { email = it }

            Text("PASSWORD", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextGray, modifier = Modifier.padding(bottom = 8.dp))
            OutlinedTextField(
                value = password, onValueChange = { password = it }, placeholder = { Text("........", color = TextGray) },
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(if (passwordVisible) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility, contentDescription = null, tint = TextGray)
                    }
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp), shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(unfocusedBorderColor = BorderLight, focusedBorderColor = PrimaryBlue), singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            SectionTitle("COMPANY REGISTRATION")
            CustomTextField("COMPANY NAME", "Global Solutions Pvt Ltd", companyName) { companyName = it }
            CustomTextField("GSTIN", "22AAAAA0000A1Z5", gstin) { gstin = it }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("ANNUAL TURNOVER", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextGray, modifier = Modifier.padding(bottom = 8.dp))
                    Box {
                        OutlinedTextField(
                            value = selectedTurnover, onValueChange = {}, readOnly = true, trailingIcon = { Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = TextGray) },
                            modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp), colors = OutlinedTextFieldDefaults.colors(unfocusedBorderColor = BorderLight, focusedBorderColor = PrimaryBlue)
                        )
                        Box(modifier = Modifier.matchParentSize().clickable { turnoverExpanded = true })
                        DropdownMenu(expanded = turnoverExpanded, onDismissRequest = { turnoverExpanded = false }) {
                            listOf("< 1 Cr", "1-5 Cr", "5-10 Cr", "> 10 Cr").forEach { option ->
                                DropdownMenuItem(text = { Text(option) }, onClick = { selectedTurnover = option; turnoverExpanded = false })
                            }
                        }
                    }
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text("BUSINESS TYPE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextGray, modifier = Modifier.padding(bottom = 8.dp))
                    Box {
                        OutlinedTextField(
                            value = selectedBusinessType, onValueChange = {}, readOnly = true, trailingIcon = { Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = TextGray) },
                            modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp), colors = OutlinedTextFieldDefaults.colors(unfocusedBorderColor = BorderLight, focusedBorderColor = PrimaryBlue)
                        )
                        Box(modifier = Modifier.matchParentSize().clickable { businessTypeExpanded = true })
                        DropdownMenu(expanded = businessTypeExpanded, onDismissRequest = { businessTypeExpanded = false }) {
                            listOf("Proprietorship", "Private Limited", "Partnership").forEach { option ->
                                DropdownMenuItem(text = { Text(option) }, onClick = { selectedBusinessType = option; businessTypeExpanded = false })
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
            Text("By proceeding, you agree to our Terms of Use and Data Privacy Policy.", fontSize = 11.sp, color = TextGray, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(24.dp))

            // NEW: Save data before going to next screen
            Button(
                onClick = {
                    viewModel.fullName = fullName
                    viewModel.email = email
                    viewModel.password = password
                    viewModel.companyName = companyName
                    viewModel.gstin = gstin
                    onNextClick()
                },
                modifier = Modifier.fillMaxWidth().height(56.dp), shape = RoundedCornerShape(12.dp), colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Next: Verify Documents", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                Text("Already have an account? ", fontSize = 14.sp, color = TextGray)
                Text("Login", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = PrimaryBlue, modifier = Modifier.clickable { onLoginClick() })
            }
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun SectionTitle(title: String) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 16.dp)) {
        Box(modifier = Modifier.width(3.dp).height(16.dp).background(PrimaryBlue))
        Spacer(modifier = Modifier.width(8.dp))
        Text(title, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, color = TextDark, letterSpacing = 1.sp)
    }
}

@Composable
fun CustomTextField(label: String, placeholder: String, value: String, onValueChange: (String) -> Unit) {
    Text(label, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextGray, modifier = Modifier.padding(bottom = 8.dp))
    OutlinedTextField(
        value = value, onValueChange = onValueChange, placeholder = { Text(placeholder, color = TextGray) },
        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp), shape = RoundedCornerShape(8.dp),
        colors = OutlinedTextFieldDefaults.colors(unfocusedBorderColor = BorderLight, focusedBorderColor = PrimaryBlue), singleLine = true
    )
}