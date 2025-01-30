// Package declaration
package com.example.familytree.ui.theme.dialogs

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.foundation.layout.*
import com.example.familytree.data.FamilyMember
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.unit.dp
import com.example.familytree.ui.theme.HebrewText

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
                Text(HebrewText.FAMILY_MEMBER_DETAILS, style = MaterialTheme.typography.titleMedium)  // Dialog title in Hebrew.
            },
            text = {
                // Column layout stacks member details vertically, making the information clear and readable.
                Column(modifier = Modifier.padding(8.dp)) {

                    // סוג בן משפחה
                    Text("${HebrewText.FAMILY_MEMBER_TYPE}:${member.getMemberType()}",
                        style = MaterialTheme.typography.bodyMedium)

                    // שם פרטי
                    Text("${HebrewText.FIRST_NAME}: ${member.getFirstName()}",
                        style = MaterialTheme.typography.bodyMedium)

                    // שם משפחה
                    Text("${HebrewText.LAST_NAME}: ${member.getLastName()}",
                        style = MaterialTheme.typography.bodyMedium)

                    // מין
                    Text("${HebrewText.SEX}: ${if (member.getGender()) HebrewText.MALE else HebrewText.FEMALE}",
                        style = MaterialTheme.typography.bodyMedium)
                }
            },
            confirmButton = {
                Button(onClick = onDismiss) {
                    Text(HebrewText.CLOSE)
                }
            }
        )
    }
}
