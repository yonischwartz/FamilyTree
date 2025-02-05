package com.example.familytree.ui.theme.dialogs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.familytree.data.MemberType
import com.example.familytree.ui.theme.HebrewText
import com.example.familytree.ui.theme.WideBlueButton

/**
 * A composable function that displays a dialog for selecting the member type.
 *
 * @param onMemberTypeSelected A lambda function triggered when a member type is selected.
 * @param onDismiss A lambda function triggered when the dialog is dismissed.
 */
@Composable
fun MemberTypeSelectionDialog(
    onMemberTypeSelected: (MemberType) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(HebrewText.CHOOSE_FAMILY_MEMBER_TYPE, textAlign = TextAlign.End) },
        text = {
            Column {
                MemberTypeSelection(onMemberTypeSelected = onMemberTypeSelected)
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text(HebrewText.CANCEL)
            }
        }
    )
}

/**
 * Provides buttons for selecting the member type.
 *
 * @param onMemberTypeSelected Callback invoked when a member type is selected.
 */
@Composable
private fun MemberTypeSelection(onMemberTypeSelected: (MemberType) -> Unit) {

    WideBlueButton(
        text = HebrewText.YESHIVA_FAMILY_MEMBER,
        onClick = { onMemberTypeSelected(MemberType.Yeshiva) }
    )

    Spacer(modifier = Modifier.height(8.dp))

    WideBlueButton(
        text = HebrewText.NON_YESHIVA_FAMILY_MEMBER,
        onClick = { onMemberTypeSelected(MemberType.NonYeshiva) }
    )
}