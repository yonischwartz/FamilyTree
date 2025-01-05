package com.example.familytree.ui.theme

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.familytree.data.dataManagement.*


@Composable
fun MemberListDialog(familyTreeData: FamilyTreeData, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("רשימת בני משפחה") }, // "Family Members List"
        text = {
            LazyColumn(modifier = Modifier.fillMaxHeight()) {
                items(familyTreeData.getAllMembers().indices.toList()) { index ->
                    val member = familyTreeData.getAllMembers()[index]
                    Text(
                        text = member.getFullName(),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }

        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("סגור") // "Close"
            }
        }
    )
}
