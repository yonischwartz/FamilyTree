package com.example.familytree.ui.dialogs

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.familytree.data.FamilyMember
import androidx.compose.ui.unit.dp
import com.example.familytree.data.MemberType
import com.example.familytree.ui.CustomizedText
import com.example.familytree.ui.CustomizedTitleText
import com.example.familytree.ui.HebrewText
import com.example.familytree.ui.WideBlueButton
import com.example.familytree.ui.intToMachzor
import com.example.familytree.ui.pages.homeScreenPage.functionForButtons.AddFamilyMember
import com.example.familytree.ui.pages.homeScreenPage.functionForButtons.ConnectTwoMembers
import com.example.familytree.ui.pages.homeScreenPage.functionForButtons.FindConnectionsBetweenTwoMembers

/**
 * Composable function that displays detailed information about a yeshiva family member.
 *
 * @param member The yeshiva family member whose details are shown.
 * @param onDismiss The action to perform when the detail dialog is dismissed.
 */
@RequiresApi(Build.VERSION_CODES.N)
@Composable
fun InfoOnMemberDialog(
    member: FamilyMember,
    onDismiss: () -> Unit
) {

    DialogWithOneButton(
        title = HebrewText.FAMILY_MEMBER_DETAILS,
        textForButton = HebrewText.CLOSE ,
        onClick = onDismiss,
        contentOfDialog = {
            Column(modifier = Modifier.padding(4.dp)) {

                var showAddNewMemberDialog by remember { mutableStateOf(false) }
                var showConnectExistingMemberDialog by remember { mutableStateOf(false) }
                var showFindConnectionToAnotherMember by remember { mutableStateOf(false) }

                val machzorToDisplay: String

                // Add rabbi to name in case member is a rabbi
                val firstNameDisplay = when {
                    member.getIsRabbi() && member.getGender() -> "${HebrewText.RABBI}${member.getFirstName()}"
                    member.getIsRabbi() && !member.getGender() -> "${HebrewText.RABBI_WIFE}${member.getFirstName()}"
                    else -> member.getFirstName()
                }

//                // סוג בן משפחה
//
//                CustomizedTitleText(
//                    title = HebrewText.FAMILY_MEMBER_TYPE,
//                    text = member.getMemberType().toString()
//                )

                // שם מלא
                CustomizedTitleText(
                    title = HebrewText.NAME,
                    text = member.getFullName()
                )

//                // שם פרטי
//
//                CustomizedTitleText(
//                    title = HebrewText.FIRST_NAME,
//                    text = firstNameDisplay
//                )
//
//                // שם משפחה
//
//                CustomizedTitleText(
//                    title = HebrewText.LAST_NAME,
//                    text = member.getLastName()
//                )

                // מין

                CustomizedTitleText(
                    title = HebrewText.SEX,
                    text = if (member.getGender()) HebrewText.MALE else HebrewText.FEMALE
                )

                // מחזור

                // If the member is not from the yeshiva, display the appropriate text
                if (member.getMemberType() == MemberType.NonYeshiva) {

                    // Different text for male and for female
                    if (member.getGender()) {
                        machzorToDisplay = HebrewText.HE_IS_NOT_FROM_THE_YESHIVA
                    }
                    else {
                        machzorToDisplay = HebrewText.SHE_IS_NOT_FROM_THE_YESHIVA
                    }
                }

                // If the member is a rabbi or a staff member, display the appropriate text
                else if (member.getMachzor() == 0) {
                    machzorToDisplay = HebrewText.RABBIS_AND_STAFF
                }

                // If the member is from the yeshiva, display the machzor
                else {
                    machzorToDisplay = intToMachzor[member.getMachzor()].toString()
                }

                CustomizedTitleText(
                    title = HebrewText.MACHZOR,
                    text = machzorToDisplay
                )
            }

//                WideBlueButton(
//                    text = "${HebrewText.ADD_NEW_MEMBER_THAT_IS_RELATED_TO}${member.getFullName()}",
//                    onClick = { showAddNewMemberDialog = true }
//                )
//
//                Spacer(modifier = Modifier.height(8.dp))
//
//                WideBlueButton(
//                    text = "${HebrewText.ADD_CONNECTION_BETWEEN_EXISTING_MEMBER_AND} ${member.getFullName()}",
//                    onClick = { showConnectExistingMemberDialog = true }
//                )
//
//                Spacer(modifier = Modifier.height(8.dp))
//
//                WideBlueButton(
//                    text = "${HebrewText.FIND_CONNECTION_BETWEEN} ${member.getFullName()} ${HebrewText.AND_ANOTHER_MEMBER}",
//                    onClick = { showFindConnectionToAnotherMember = true }
//                )

//                if (showAddNewMemberDialog) {
//                    AddFamilyMember(
//                        onDismiss = { showAddNewMemberDialog = false },
//                        givenExistingMember = member
//                    )
//                }
//
//                if (showConnectExistingMemberDialog) {
//                    ConnectTwoMembers(
//                        onDismiss = { showConnectExistingMemberDialog = false },
//                        givenFirstMember = member
//                    )
//                }
//
//                if (showFindConnectionToAnotherMember) {
//                    FindConnectionsBetweenTwoMembers(
//                        onDismiss = { showFindConnectionToAnotherMember = false },
//                        givenFirstMember = member
//                    )
//                }
//            }
        }
    )
}
