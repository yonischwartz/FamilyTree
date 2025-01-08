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
 * Composable function that displays detailed information about a yeshiva family member.
 *
 * @param member The yeshiva family member whose details are shown.
 * @param onDismiss The action to perform when the detail dialog is dismissed.
 */
@Composable
fun YeshivaMemberDetailDialog(member: FamilyMember, onDismiss: () -> Unit) {
    // Set right-to-left layout direction for Hebrew content.
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        AlertDialog(
            onDismissRequest = onDismiss,  // Handles dialog dismissal when clicking outside the dialog.
            title = {
                Text("פרטי בן משפחה", style = MaterialTheme.typography.titleMedium)  // Dialog title in Hebrew.
            },
            text = {
                Column(modifier = Modifier.padding(8.dp)) {
                    // Add "הרב" before the first name if the member is a rabbi.
                    val firstNameDisplay = if (member.getIsRabbi() == true) {
                        "הרב ${member.getFirstName()}"
                    } else {
                        member.getFirstName()
                    }
                    Text("שם פרטי: $firstNameDisplay", style = MaterialTheme.typography.bodyMedium)
                    Text("שם משפחה: ${member.getLastName()}", style = MaterialTheme.typography.bodyMedium)
                    Text("מין: ${if (member.getGender()) "זכר" else "נקבה"}", style = MaterialTheme.typography.bodyMedium)
                    Text("מחזור: ${intToMachzor[member.getMachzor()] ?: "לא ידוע"}", style = MaterialTheme.typography.bodyMedium)
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
