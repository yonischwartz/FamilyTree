package com.example.familytree.ui.theme.homeScreen

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

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
            text = "הוסף בן משפחה חדש", // Add New Family Member
            style = MaterialTheme.typography.bodyMedium.copy(textAlign = TextAlign.Center)
        )
    }
}
