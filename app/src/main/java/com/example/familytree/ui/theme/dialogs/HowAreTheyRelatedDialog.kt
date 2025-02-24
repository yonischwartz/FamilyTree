package com.example.familytree.ui.theme.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.familytree.data.FamilyMember
import com.example.familytree.data.Relations
import com.example.familytree.ui.theme.CustomizedText
import com.example.familytree.ui.theme.DialogButton
import com.example.familytree.ui.theme.DialogTitle
import com.example.familytree.ui.theme.HebrewText
import com.example.familytree.ui.theme.TextFieldWithDropdownMenu

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
    var expanded by remember { mutableStateOf(false) }
    var chosenRelation by remember { mutableStateOf(HebrewText.BLANK) }


    // Get all valid relations
    val validRelationOptions = Relations.getValidRelationOptions(existingMember)

    val title = "${HebrewText.HOW_IS_OTHER_MEMBER_RELATED_TO}${existingMember.getFullName()}?"

    val text = HebrewText.THE_OTHER_MEMBER_IS +
            " ${chosenRelation} ${HebrewText.OF} ${existingMember.getFullName()}"

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { DialogTitle(title) },
        text = {
            Column (
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            ) {

                CustomizedText(text)

                // Dropdown menu listing all relation options.
                TextFieldWithDropdownMenu(
                    label = "",
                    modifier = Modifier.width(150.dp),
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
            }
        },
        confirmButton = @androidx.compose.runtime.Composable {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                // Previous button
                DialogButton(
                    text = HebrewText.PREVIOUS,
                    onClick = onPrevious
                )

                // Next button
                DialogButton(
                    text = HebrewText.NEXT,
                    onClick = { selectedRelation?.let { onRelationSelected(it, selectedRelationGender) } },
                    enabled = selectedRelation != null
                )
            }
        }


//            Row (
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(16.dp),
//                verticalArrangement = Arrangement.Center,
//                horizontalAlignment = Alignment.CenterHorizontally
//            ) {
//                // Displays the question prompting the user to choose how the new member is related.
//                CustomizedText(
//                    text = HebrewText.THE_OTHER_MEMBER_IS
//                )
//
//                // Dropdown menu listing all relation options.
//                TextFieldWithDropdownMenu(
//                    label = "",
//                    modifier = Modifier.width(100.dp).height(50.dp),
//                    options = validRelationOptions,
//                    selectedOption = chosenRelation,
//                    onOptionSelected = { selectedOption ->
//                        selectedOption?.let { relation ->
//                            selectedRelation = Relations.relationStringToRelation(relation).first
//                            selectedRelationGender = Relations.relationStringToRelation(relation).second
//                            chosenRelation = relation
//                        }
//                    }
//                )
//
////                // Dropdown button displaying the selected relation.
////                DialogButton(
////                    text = chooseRelationButtonText,
////                    onClick = { expanded = true },
////                )
//
//                CustomizedText(
//                    text = HebrewText.OF + " " + existingMember.getFullName()
//                )
//            }
//
//            // Dropdown menu listing all relation options.
//            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
//                relationOptions.forEach { relation ->
//                    DropdownMenuItem(
//                        onClick = {
//                            selectedRelation = relationStringToRelation(relation).first
//                            selectedRelationGender = relationStringToRelation(relation).second
//                            chooseRelationButtonText = relation
//                            expanded = false
//                        },
//                        text = { CustomizedText(relation) },
//                    )
//                }
//
//            }
//        },
//        confirmButton = {
//            Row(
//                modifier = Modifier.fillMaxWidth(),
//                horizontalArrangement = Arrangement.SpaceBetween
//            ) {
//
//                // Previous button
//                DialogButton(
//                    text = HebrewText.PREVIOUS,
//                    onClick = onPrevious
//                )
//
//                // Next button
//                DialogButton(
//                    text = HebrewText.NEXT,
//                    onClick = { selectedRelation?.let { onRelationSelected(it, selectedRelationGender) } },
//                    enabled = selectedRelation != null
//                )
//            }
//        }
    )
}

