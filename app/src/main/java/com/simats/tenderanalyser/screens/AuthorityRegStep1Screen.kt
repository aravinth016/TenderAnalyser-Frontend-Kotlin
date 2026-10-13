package com.simats.tenderanalyser.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.tenderanalyser.AuthorityViewModel

@Composable
fun AuthorityRegStep1Screen(viewModel: AuthorityViewModel, onNextClick: () -> Unit, onBackClick: () -> Unit) {
    var name by remember { mutableStateOf(viewModel.adminName) }
    var designation by remember { mutableStateOf(viewModel.designation) }
    var email by remember { mutableStateOf(viewModel.email) }
    var password by remember { mutableStateOf(viewModel.password) }

    AuthorityRegScaffold(step = 1, title = "Admin Information", subtitle = "Please provide the details of the primary administrator for this authority account.", onBackClick = onBackClick) {
        AuthorityTextField("FULL NAME", "e.g. John Doe", name) { name = it }
        AuthorityTextField("OFFICIAL DESIGNATION", "e.g. Chief Executive Officer", designation) { designation = it }
        AuthorityTextField("OFFICIAL EMAIL ID", "name@department.gov", email) { email = it }
        AuthorityTextField("ADMIN PASSWORD", "........", password, isPassword = true) { password = it }

        Spacer(modifier = Modifier.height(32.dp))
        AuthorityNextButton("NEXT: DEPARTMENT DETAILS") {
            viewModel.adminName = name
            viewModel.designation = designation
            viewModel.email = email
            viewModel.password = password
            onNextClick()
        }
    }
}