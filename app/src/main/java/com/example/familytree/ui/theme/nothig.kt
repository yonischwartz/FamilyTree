package com.example.familytree.ui.theme

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.familytree.data.FamilyMember

class nothig {

    @Composable
    fun ChooseMemberToRelateTo(
        members: List<FamilyMember>,
        onMemberSelected: (FamilyMember) -> Unit,
    ) {
        var selectedMember by remember { mutableStateOf<FamilyMember?>(null) }
        var expanded by remember { mutableStateOf(false) }
        var showDialog by remember { mutableStateOf(true) }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = {
                    Text(text = "בני משפחה חדשים נדרשים להיות קשורים לבן משפחה קיים בעץ.")
                },
                text = {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "לאיזה בן משפחה בעץ, מקושר בן המשפחה שאתה רוצה להוסיף?",
                            fontSize = 16.sp,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                        Box(modifier = Modifier.fillMaxWidth()) {
                            TextButton(
                                onClick = { expanded = true },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(text = selectedMember?.getFullName() ?: "בחר בן משפחה", fontSize = 16.sp)
                            }
                            DropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }
                            ) {
                                members.forEach { member ->
                                    DropdownMenuItem(
                                        onClick = {
                                            selectedMember = member
                                            expanded = false
                                        },
                                        text = {
                                            Text(text = member.getFullName())
                                        }
                                    )
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            selectedMember?.let {
                                onMemberSelected(it)
                                showDialog = false
                            }
                        },
                        enabled = selectedMember != null
                    ) {
                        Text(text = "המשך")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDialog = false }) {
                        Text(text = "בטל")
                    }
                }
            )
        }
    }

}