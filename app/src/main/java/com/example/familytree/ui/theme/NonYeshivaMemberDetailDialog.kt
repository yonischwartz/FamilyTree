// Package declaration
package com.example.familytree.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.foundation.layout.*
import com.example.familytree.data.FamilyMember
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.unit.dp

/**
 * Composable function that displays detailed information about a selected family member.
 *
 * @param member The family member whose details are shown.
 * @param onDismiss The action to perform when the detail dialog is dismissed.
 */
@Composable
fun NonYeshivaMemberDetailDialog(member: FamilyMember, onDismiss: () -> Unit) {
    // Set right-to-left layout direction for Hebrew content.
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        AlertDialog(
            onDismissRequest = onDismiss,  // Handles dialog dismissal when clicking outside the dialog.
            title = {
                Text("פרטי בן משפחה", style = MaterialTheme.typography.titleMedium)  // Dialog title in Hebrew.
            },
            text = {
                // Column layout stacks member details vertically, making the information clear and readable.
                Column(modifier = Modifier.padding(8.dp)) {
                    Text("שם פרטי: ${member.getFirstName()}", style = MaterialTheme.typography.bodyMedium)  // Display first name.
                    Text("שם משפחה: ${member.getLastName()}", style = MaterialTheme.typography.bodyMedium)  // Display last name.
                    Text("מין: ${if (member.getGender()) "זכר" else "נקבה"}", style = MaterialTheme.typography.bodyMedium)  // Display gender based on boolean.
                }
            },
            confirmButton = {
                Button(onClick = onDismiss) {
                    Text("סגור")  // Hebrew text for "Close" button.
                }
            }
        )
    }
}
