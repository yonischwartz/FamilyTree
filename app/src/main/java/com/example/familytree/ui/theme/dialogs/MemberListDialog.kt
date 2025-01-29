package com.example.familytree.ui.theme.dialogs

import android.os.Build
import androidx.annotation.RequiresApi
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
import com.example.familytree.ui.theme.homeScreen.DeleteMemberButton

/**
 * Composable function that displays a dialog containing a list of all family members.
 * Each member can be clicked to view detailed information.
 *
 * @param existingMembers The list of family members to display.
 * @param onDismiss The action to perform when the dialog is dismissed.
 */
@RequiresApi(Build.VERSION_CODES.N)
@Composable
fun MemberListDialog(existingMembers: List<FamilyMember>, onDismiss: () -> Unit) {
    // Use state-backed mutable list for dynamic updates
    val memberList = remember { mutableStateListOf(*existingMembers.toTypedArray()) }
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
                    items(memberList) { member ->
                        Row(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
                            Text(
                                text = member.getFullName(),
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { selectedMember = member }
                            )
                            DeleteMemberButton(member = member) {
                                memberList.remove(member)
                            }
                        }
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
                YeshivaMemberDetailDialog(member = member, onDismiss = { selectedMember = null })
            } else {
                NonYeshivaMemberDetailDialog(member = member, onDismiss = { selectedMember = null })
            }
        }
    }
}

