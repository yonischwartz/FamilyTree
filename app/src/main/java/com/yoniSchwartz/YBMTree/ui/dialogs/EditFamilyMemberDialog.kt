package com.yoniSchwartz.YBMTree.ui.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.yoniSchwartz.YBMTree.data.FamilyMember
import com.yoniSchwartz.YBMTree.data.MemberType
import com.yoniSchwartz.YBMTree.ui.BooleanCheckboxInput
import com.yoniSchwartz.YBMTree.ui.CustomizedTextFieldForEditingMembersDetails
import com.yoniSchwartz.YBMTree.ui.HebrewText
import com.yoniSchwartz.YBMTree.ui.MachzorInput
import com.yoniSchwartz.YBMTree.ui.MemberTypeInput

/**
 * A dialog that allows editing of an existing [FamilyMember]'s editable fields.
 *
 * @param member The family member to be edited.
 * @param onConfirm Callback that returns the updated member when the OK button is clicked.
 * @param onPrevious Callback invoked when the BACK button is clicked.
 * @param onDismiss Callback invoked when the dialog is dismissed.
 */
@Composable
fun EditFamilyMemberDialog(
    member: FamilyMember,
    onConfirm: (FamilyMember) -> Unit,
    onPrevious: () -> Unit,
    onDismiss: () -> Unit
) {
    // State for all editable fields
    var firstName by remember { mutableStateOf(member.getFirstName()) }
    var lastName by remember { mutableStateOf(member.getLastName()) }
    var machzor by remember { mutableStateOf(member.getMachzor()) }
    var isRabbi by remember { mutableStateOf(member.getIsRabbi()) }
    var isYeshivaRabbi by remember { mutableStateOf(member.getIsYeshivaRabbi()) }
    var memberType by remember { mutableStateOf(member.getMemberType()) }

    DialogWithButtons(
        title = HebrewText.EDIT_MEMBER,
        textForLeftButton = HebrewText.OK,
        onLeftButtonClick = {
            val updatedMember = FamilyMember(
                memberType = memberType,
                firstName = firstName.trimEnd(),
                lastName = lastName.trimEnd(),
                gender = member.getGender(), // immutable
                machzor = machzor,
                isRabbi = isRabbi,
                isYeshivaRabbi = isYeshivaRabbi,
                id = member.getId(), // immutable
                connections = member.getConnections().toMutableList() // preserve connections
            )
            onConfirm(updatedMember)
        },
        textForRightButton = HebrewText.PREVIOUS,
        onRightButtonClick = onPrevious,
        onDismiss = onDismiss,
        contentOfDialog = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                CustomizedTextFieldForEditingMembersDetails(
                    label = HebrewText.FIRST_NAME,
                    value = firstName,
                    onValueChange = { firstName = it }
                )

                CustomizedTextFieldForEditingMembersDetails(
                    label = HebrewText.LAST_NAME,
                    value = lastName,
                    onValueChange = { lastName = it }
                )

                MemberTypeInput(
                    selectedType = memberType,
                    onMemberTypeChange = { memberType = it }
                )

                // Show machzor only if the member is a Yeshiva member
                if (memberType == MemberType.Yeshiva) {
                    MachzorInput(
                        machzor = machzor,
                        onMachzorChange = { machzor = it }
                    )
                }

                BooleanCheckboxInput(
                    label = HebrewText.IS_THIS_FAMILY_MEMBER_A_RABBI,
                    checked = isRabbi,
                    onCheckedChange = { isRabbi = it }
                )

                // Show isYeshivaRabbi only if the member is a Yeshiva member *and* a Rabbi
                if (memberType == MemberType.Yeshiva && isRabbi) {
                    BooleanCheckboxInput(
                        label = HebrewText.IS_THIS_RABBI_A_YESHIVA_RABBI,
                        checked = isYeshivaRabbi,
                        onCheckedChange = { isYeshivaRabbi = it }
                    )
                }
            }
        }
    )
}