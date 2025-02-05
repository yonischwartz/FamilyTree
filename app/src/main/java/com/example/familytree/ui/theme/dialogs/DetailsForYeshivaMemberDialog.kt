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
import com.example.familytree.ui.theme.intToMachzor

/**
 * Composable function that displays detailed information about a yeshiva family member.
 *
 * @param member The yeshiva family member whose details are shown.
 * @param onDismiss The action to perform when the detail dialog is dismissed.
 */
@Composable
fun DetailsForYeshivaMemberDialog(member: FamilyMember, onDismiss: () -> Unit) {
    // Set right-to-left layout direction for Hebrew content.
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        AlertDialog(
            onDismissRequest = onDismiss,  // Handles dialog dismissal when clicking outside the dialog.
            title = {
                Text(HebrewText.FAMILY_MEMBER_DETAILS, style = MaterialTheme.typography.titleMedium)
            },
            text = {
                Column(modifier = Modifier.padding(8.dp)) {

                    // Define first name display based on whether the member is a rabbi
                    val firstNameDisplay = when {
                        member.getIsRabbi() && member.getGender() -> "${HebrewText.RABBI}${member.getFirstName()}"
                        member.getIsRabbi() && !member.getGender() -> "${HebrewText.RABBI_WIFE}${member.getFirstName()}"
                        else -> member.getFirstName()
                    }

                    // סוג בן משפחה
                    Text("${HebrewText.FAMILY_MEMBER_TYPE}: ${member.getMemberType()}",
                        style = MaterialTheme.typography.bodyMedium)

                    // שם פרטי
                    Text("${HebrewText.FIRST_NAME}: $firstNameDisplay",
                        style = MaterialTheme.typography.bodyMedium)

                    // שם משפחה
                    Text("${HebrewText.LAST_NAME}: ${member.getLastName()}",
                        style = MaterialTheme.typography.bodyMedium)

                    // מין
                    Text("${HebrewText.SEX}: ${if (member.getGender()) HebrewText.MALE else HebrewText.FEMALE}",
                        style = MaterialTheme.typography.bodyMedium)

                    // מחזור
                    Text("${HebrewText.MACHZOR}: ${intToMachzor[member.getMachzor()] ?: HebrewText.UNKNOWN}",
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
