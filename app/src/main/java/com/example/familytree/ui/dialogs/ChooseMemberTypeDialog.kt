package com.example.familytree.ui.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.familytree.data.MemberType
import com.example.familytree.ui.HebrewText
import com.example.familytree.ui.DialogButton
import com.example.familytree.ui.DialogTitle
import com.example.familytree.ui.WideBlueButton

@Composable
fun ChooseMemberTypeDialog(
    onMemberTypeSelected: (MemberType) -> Unit,
    showPreviousButton: Boolean = true,
    onPrevious: () -> Unit = {},
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { DialogTitle(HebrewText.CHOOSE_FAMILY_MEMBER_TYPE) },
        text = {
            Column {
                MemberTypeSelection(onMemberTypeSelected = onMemberTypeSelected)
            }
        },
        confirmButton = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {
                if (showPreviousButton) {

                    DialogButton(
                        text = HebrewText.PREVIOUS,
                        onClick = onPrevious
                    )
                }
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