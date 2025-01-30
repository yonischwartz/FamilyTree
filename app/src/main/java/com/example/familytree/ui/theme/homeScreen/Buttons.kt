package com.example.familytree.ui.theme.homeScreen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.example.familytree.data.FamilyMember
import com.example.familytree.data.dataManagement.FireBaseManager
import com.example.familytree.ui.theme.HebrewText

/**
 * A Composable function that represents the button for showing all family members.
 *
 * @param onShowMembers A lambda function to handle the action when the button is clicked.
 */
@Composable
internal fun ShowMembersButton(onShowMembers: () -> Unit) {
    Button(
        onClick = onShowMembers,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = HebrewText.SHOW_ALL_FAMILY_MEMBERS,
            style = MaterialTheme.typography.bodyMedium.copy(textAlign = TextAlign.Center)
        )
    }
}

/**
 * A Composable function that represents the button for adding a new family member.
 *
 * @param onAddMember A lambda function to handle the action when the button is clicked.
 */
@Composable
fun AddMemberButton(onAddMember: () -> Unit) {
    Button(
        onClick = onAddMember,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = HebrewText.ADD_NEW_FAMILY_MEMBER,
            style = MaterialTheme.typography.bodyMedium.copy(textAlign = TextAlign.Center)
        )
    }
}

/**
 * Composable button to delete a family member.
 *
 * @param member The family member to delete.
 * @param onDeleted Callback to perform actions after deletion.
 */
@RequiresApi(Build.VERSION_CODES.N)
@Composable
fun DeleteMemberButton(member: FamilyMember, onDeleted: () -> Unit) {
    Button(onClick = {
        member.documentId?.let { FireBaseManager.deleteFamilyMember(it) }
        onDeleted()
    }) {
        Text(HebrewText.REMOVE)
    }
}
