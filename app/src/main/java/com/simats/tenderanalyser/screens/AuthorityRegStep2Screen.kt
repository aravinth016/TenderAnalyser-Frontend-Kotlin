package com.simats.tenderanalyser.screens

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.simats.tenderanalyser.AuthorityViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthorityRegStep2Screen(viewModel: AuthorityViewModel, onNextClick: () -> Unit, onBackClick: () -> Unit) {
    var deptName by remember { mutableStateOf(viewModel.deptName) }
    var officeCode by remember { mutableStateOf(viewModel.officeCode) }
    var address by remember { mutableStateOf(viewModel.hqAddress) }

    // State for the Sector Dropdown
    var sector by remember { mutableStateOf(viewModel.sector.ifEmpty { "Select Sector" }) }
    var expanded by remember { mutableStateOf(false) }

    // Pre-defined list of standard sectors
    val sectorOptions = listOf(
        "Infrastructure & Construction",
        "Energy & Power",
        "IT & Telecommunications",
        "Healthcare & Medical",
        "Defense & Security",
        "Education & Training",
        "Public Works & Logistics"
    )

    AuthorityRegScaffold(
        step = 2,
        title = "Department Details",
        subtitle = "ENTITY INFORMATION FOR TENDERING",
        onBackClick = onBackClick
    ) {
        AuthorityTextField("DEPARTMENT / PSU NAME", "e.g. Ministry of Infrastructure", deptName) { deptName = it }
        AuthorityTextField("OFFICE CODE / DIVISION", "e.g. DIV-NORTH-702", officeCode) { officeCode = it }

        // --- SECTOR CATEGORY DROPDOWN ---
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "SECTOR CATEGORY",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(4.dp))

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
        ) {
            OutlinedTextField(
                value = sector,
                onValueChange = {},
                readOnly = true, // Prevents manual typing
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier.menuAnchor().fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                sectorOptions.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            sector = option
                            expanded = false
                        }
                    )
                }
            }
        }

        AuthorityTextField("HEADQUARTERS ADDRESS", "Full office address with PIN code", address) { address = it }

        Spacer(modifier = Modifier.height(32.dp))
        AuthorityNextButton("NEXT: VERIFICATION DETAIL") {
            viewModel.deptName = deptName
            viewModel.officeCode = officeCode
            viewModel.hqAddress = address
            // Save the sector to ViewModel (prevent saving the "Select Sector" placeholder)
            viewModel.sector = if (sector == "Select Sector") "" else sector

            onNextClick()
        }
    }
}