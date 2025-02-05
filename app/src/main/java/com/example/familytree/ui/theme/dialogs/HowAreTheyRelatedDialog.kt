package com.example.familytree.ui.theme.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.familytree.data.FamilyMember
import com.example.familytree.data.Relations
import com.example.familytree.ui.theme.HebrewText

/**
 * A composable function that displays a user interface for selecting the relation
 * between an existing family member and a new family member to be added.
 *
 * @param existingMember The FamilyMember object representing the existing family member.
 * @param onRelationSelected A callback function that is invoked with the selected relation
 * when the user clicks the "המשך" (Next) button.
 */
@Composable
fun HowAreTheyRelatedDialog(
    existingMember: FamilyMember,
    onRelationSelected: (Relations) -> Unit,
    onPrevious: () -> Unit,
    onDismiss: () -> Unit
) {
    var selectedRelation by remember { mutableStateOf<Relations?>(null) }
    var expanded by remember { mutableStateOf(false) }

    // If it's a marriage relation, gender will determine weather it's a wife or a husband
    val gender = existingMember.getGender()

    val title = "${HebrewText.HOW} ${existingMember.getFullName()} ${HebrewText.RELATED_TO_THE_OTHER_MEMBER}"

    AlertDialog(
        onDismissRequest = {
            onDismiss()
        },
        title = {
            Text(text = title)
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Displays the question prompting the user to choose how the new member is related.
                Text(
                    text = HebrewText.THE_OTHER_MEMBER_IS,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Button(
                    onClick = { expanded = true },
                ) {
                    Text(
                        text = selectedRelation?.displayAsConnections(!gender) ?: HebrewText.CHOOSE_RELATION,
                        fontSize = 16.sp
                    )
                }

                Text(
                    text = existingMember.getFullName(),
                    fontSize = 16.sp
                )
            }

            // Dropdown menu listing all relation options.
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                Relations.entries.forEach { relation ->
                    DropdownMenuItem(
                        onClick = {
                            selectedRelation = relation
                            expanded = false
                        },
                        text = {
                            Text(text = relation.displayAsConnections(!existingMember.getGender()))
                        }
                    )
                }

            }
        },
        confirmButton = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                // Previous button
                Button(onClick = onPrevious) {
                    Text(HebrewText.PREVIOUS)
                }

                // Next button
                Button(
                    onClick = {
                        selectedRelation?.let {
                            onRelationSelected(it)
                        }
                    },
                    enabled = selectedRelation != null
                ) {
                    Text(text = HebrewText.NEXT)
                }
            }
        }
    )
}





















//Row(
//modifier = Modifier.fillMaxWidth(),
//verticalAlignment = Alignment.CenterVertically
//) {
//    Button(
//        onClick = { expanded = true },
//        modifier = Modifier.weight(1f) // Allows it to take equal space
//    ) {
//        Text(
//            text = selectedRelation?.displayAsConnections() ?: HebrewText.RELATION,
//            fontSize = 16.sp
//        )
//    }
//
//    Text(
//        text = existingMember.getFullName(),
//        fontSize = 16.sp,
//        modifier = Modifier
//            .padding(start = 8.dp) // Adds spacing between button and text
//            .weight(1f) // Ensures proper alignment
//    )
//}
//
//// Dropdown menu listing all relation options.
//DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
//    Relations.entries.forEach { relation ->
//        DropdownMenuItem(
//            onClick = {
//                selectedRelation = relation
//                expanded = false
//            },
//            text = {
//                Text(text = relation.displayAsConnections())
//            }
//        )
//    }
//}



