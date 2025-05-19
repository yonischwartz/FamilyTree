package com.yoniSchwartz.YBMTree.ui.dialogs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.yoniSchwartz.YBMTree.data.MemberType
import com.yoniSchwartz.YBMTree.ui.HebrewText
import com.yoniSchwartz.YBMTree.ui.ButtonForPage

/**
 * Displays a dialog that allows the user to choose a member type
 * (Yeshiva or Non-Yeshiva family member).
 *
 * @param onMemberTypeSelected Callback invoked with the selected [MemberType]
 *                             when the user chooses an option.
 * @param showPreviousButton Whether to show the "Previous" button (default is true).
 * @param onPrevious Callback invoked when the "Previous" button is clicked.
 * @param onDismiss Callback invoked when the dialog is dismissed.
 */
@Composable
fun ChooseMemberTypeDialog(
    onMemberTypeSelected: (MemberType) -> Unit,
    showPreviousButton: Boolean = true,
    onPrevious: () -> Unit = {},
    onDismiss: () -> Unit
) {
    DialogWithButtons(
        title = HebrewText.CHOOSE_FAMILY_MEMBER_TYPE,
        onRightButtonClick = onPrevious,
        textForRightButton = HebrewText.PREVIOUS,
        enabledForRightButton = showPreviousButton,
        onDismiss = onDismiss,
        contentOfDialog = {
            Column {
                MemberTypeSelection(onMemberTypeSelected = onMemberTypeSelected)
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

    ButtonForPage(
        text = HebrewText.YESHIVA_FAMILY_MEMBER,
        onClick = { onMemberTypeSelected(MemberType.Yeshiva) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
    )

    Spacer(modifier = Modifier.height(8.dp))

    ButtonForPage(
        text = HebrewText.NON_YESHIVA_FAMILY_MEMBER,
        onClick = { onMemberTypeSelected(MemberType.NonYeshiva) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
    )
}