package com.example.familytree.ui.theme.dialogs

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.*
import com.example.familytree.data.FamilyMember
import com.example.familytree.ui.theme.AddNewFamilyMemberToEmptyTree
import com.example.familytree.ui.theme.AddNewMemberAndRelateToExistingMember

/**
 * A composable function that displays a dialog for adding a family member to the family tree.
 *
 * @param onDismiss Callback to handle dialog dismissal.
 * @param existingMembers A list of existing family members.
 */
@RequiresApi(Build.VERSION_CODES.N)
@Composable
fun AddFamilyMemberDialog(
    existingMembers: List<FamilyMember>,
    onDismiss: () -> Unit
) {

    if (existingMembers.isEmpty()) {
        // Add the first family member of the tree
        AddNewFamilyMemberToEmptyTree(
            existingMembers = existingMembers,
            onDismiss = onDismiss
        )

    } else {

        AddNewMemberAndRelateToExistingMember(
            existingMembers = existingMembers,
            onDismiss = onDismiss
        )
    }
}