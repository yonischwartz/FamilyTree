// Package declaration
package com.example.familytree.ui.theme

// Import statements for necessary Compose and data classes
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
    // A nullable FamilyMember is used to represent that no member is selected initially.
    var selectedMember by remember { mutableStateOf<FamilyMember?>(null) }

    // Set right-to-left layout direction for Hebrew content.
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        AlertDialog(
            onDismissRequest = onDismiss,  // Handles dialog dismissal when clicking outside the dialog.
            title = {
                Text("רשימת בני משפחה", style = MaterialTheme.typography.titleMedium)  // Dialog title in Hebrew.
            },
            text = {
                LazyColumn(modifier = Modifier.fillMaxHeight()) {
                    // Display a list of family members using a lazy column for efficient rendering.
                    items(familyMembers) { member ->
                        Text(
                            text = member.getFullName(),  // Display the member's full name.
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                                .clickable { selectedMember = member }  // Set selectedMember to the clicked member.
                        )
                    }
                }
            },
            confirmButton = {
                Button(onClick = onDismiss) {
                    Text("סגור")  // Hebrew text for "Close" button.
                }
            }
        )
    }

    // Conditionally display MemberDetailDialog only if a member is selected.
    selectedMember?.let { member ->
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            MemberDetailDialog(member = member, onDismiss = { selectedMember = null })  // Reset selectedMember to null on dismissal.
        }
    }
}

/**
 * Composable function that displays detailed information about a selected family member.
 *
 * @param member The family member whose details are shown.
 * @param onDismiss The action to perform when the detail dialog is dismissed.
 */
@Composable
fun MemberDetailDialog(member: FamilyMember, onDismiss: () -> Unit) {
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
