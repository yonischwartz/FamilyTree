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
import com.example.familytree.data.dataManagement.DatabaseManager
import com.example.familytree.ui.theme.HebrewText

/**
 * A Composable function that represents an home screen button.
 *
 * @param onClick A lambda function to handle the action when the button is clicked.
 * @param text A text to display on the button.
 */
@Composable
internal fun HomeScreenButton(onClick: () -> Unit, text: String) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = text,
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
        member.getId().let { DatabaseManager.deleteMemberFromLocalMemberMap(it) }
        onDeleted()
    }) {
        Text(HebrewText.REMOVE)
    }
}
