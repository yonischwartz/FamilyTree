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
import com.example.familytree.data.dataManagement.FamilyTreeData
import com.example.familytree.data.FamilyMember

@Composable
fun MemberListDialog(familyTreeData: FamilyTreeData, onDismiss: () -> Unit) {
    var selectedMember by remember { mutableStateOf<FamilyMember?>(null) }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("רשימת בני משפחה", style = MaterialTheme.typography.titleMedium) },
            text = {
                LazyColumn(modifier = Modifier.fillMaxHeight()) {
                    items(familyTreeData.getAllMembers()) { member ->
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

    selectedMember?.let { member ->
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            MemberDetailDialog(member = member, onDismiss = { selectedMember = null })
        }
    }
}

@Composable
fun MemberDetailDialog(member: FamilyMember, onDismiss: () -> Unit) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("פרטי בן משפחה", style = MaterialTheme.typography.titleMedium) },
            text = {
                Column(modifier = Modifier.padding(8.dp)) {
                    Text("שם פרטי: ${member.getFirstName()}", style = MaterialTheme.typography.bodyMedium)
                    Text("שם משפחה: ${member.getLastName()}", style = MaterialTheme.typography.bodyMedium)
                    Text("מין: ${if (member.getGender()) "זכר" else "נקבה"}", style = MaterialTheme.typography.bodyMedium)
                }
            },
            confirmButton = {
                Button(onClick = onDismiss) {
                    Text("סגור")
                }
            }
        )
    }
}
