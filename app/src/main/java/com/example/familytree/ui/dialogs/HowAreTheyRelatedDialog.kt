package com.example.familytree.ui.dialogs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.familytree.data.FamilyMember
import com.example.familytree.data.Relations
import com.example.familytree.ui.CustomizedText
import com.example.familytree.ui.HebrewText
import com.example.familytree.ui.InlineDropdown

/**
 * Displays a dialog for selecting the relationship between an existing family member and a new member.
 *
 * @param existingMember The existing family member for whom the relationship is being defined.
 * @param onRelationSelected Callback invoked when a relationship is selected.
 * @param onPrevious Callback invoked when the previous button is clicked.
 * @param onDismiss Callback invoked when the dialog is dismissed.
 */
@Composable
fun HowAreTheyRelatedDialog(
    existingMember: FamilyMember,
    onRelationSelected: (Relations, Boolean) -> Unit,
    onPrevious: () -> Unit,
    onDismiss: () -> Unit
) {

    var selectedRelation by remember { mutableStateOf<Relations?>(null) }
    var selectedRelationGender by remember { mutableStateOf(true) }
    var chosenRelation by remember { mutableStateOf(HebrewText.BLANK) }


    // Get all valid relations
    val validRelationOptions = Relations.getValidRelationOptions(existingMember)

    val title = "${HebrewText.HOW_IS_OTHER_MEMBER_RELATED_TO}${existingMember.getFullName()}?"

    val text = HebrewText.THE_OTHER_MEMBER_IS +
            " $chosenRelation ${HebrewText.OF} ${existingMember.getFullName()}"

    // The lambda for when the user clicks the NEXT button
    var assignChosenRelation: () -> Unit = {}
    assignChosenRelation = {
        selectedRelation?.let { onRelationSelected(it, selectedRelationGender) }
    }

    // The condition for enabling the NEXT button
    val wasRelationChosen = selectedRelation != null

    DialogWithButtons(
        title = title,
        onLeftButtonClick = assignChosenRelation,
        textForLeftButton = HebrewText.NEXT,
        enabledForLeftButton = wasRelationChosen,
        onRightButtonClick = onPrevious,
        textForRightButton = HebrewText.PREVIOUS,
        onDismiss = onDismiss,
        contentOfDialog = {
            Column (
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            ) {

                CustomizedText(HebrewText.THE_OTHER_MEMBER_IS + ":")

                Row() {

                    InlineDropdown(
                        options = validRelationOptions,
                        selectedOption = chosenRelation,
                        onOptionSelected = { selectedOption ->
                            selectedOption?.let { relation ->
                                selectedRelation =
                                    Relations.relationStringToRelation(relation).first
                                selectedRelationGender =
                                    Relations.relationStringToRelation(relation).second
                                chosenRelation = relation
                            }
                        }
                    )

                    CustomizedText("${HebrewText.OF} ${existingMember.getFullName()}")
                }
            }
        }
    )
}

