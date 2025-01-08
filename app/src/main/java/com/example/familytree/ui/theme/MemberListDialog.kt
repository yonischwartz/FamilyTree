package com.example.familytree.ui.theme

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.platform.LocalLayoutDirection
import com.example.familytree.data.FamilyMember

/**
 * Composable function that displays a dialog containing a list of all family members.
 * Each member can be clicked to view detailed information.
 *
 * @param familyMembers The list of family members to display.
 * @param onDismiss The action to perform when the dialog is dismissed.
 */
@Composable
fun MemberListDialog(familyMembers: List<FamilyMember>, onDismiss: () -> Unit) {
    // State variable to track the selected family member.
    var selectedMember by remember { mutableStateOf<FamilyMember?>(null) }

    // Set right-to-left layout direction for Hebrew content.
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = {
                Text("רשימת בני משפחה", style = MaterialTheme.typography.titleMedium)
            },
            text = {
                LazyColumn(modifier = Modifier.fillMaxHeight()) {
                    items(familyMembers) { member ->
                        Text(
                            text = member.getFullName(),
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                                .clickable { selectedMember = member }
                        )
                    }
                }
            },
            confirmButton = {
                Button(onClick = onDismiss) {
                    Text("סגור")
                }
            }
        )
    }

    // Display appropriate dialog based on member type.
    selectedMember?.let { member ->
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            if (member.getMachzor() != null) {
                // Show the dialog for yeshiva members
                YeshivaMemberDetailDialog(member = member, onDismiss = { selectedMember = null })
            } else {
                // Show the dialog for non-yeshiva members
                NonYeshivaMemberDetailDialog(member = member, onDismiss = { selectedMember = null })
            }
        }
    }
}
