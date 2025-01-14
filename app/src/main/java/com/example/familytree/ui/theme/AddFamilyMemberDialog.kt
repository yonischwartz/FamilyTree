package com.example.familytree.ui.theme

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import com.example.familytree.data.FamilyMember

/**
 * A composable function that displays a dialog for adding a family member to the family tree.
 *
 * @param onDismiss Callback to handle dialog dismissal.
 * @param onAddMember Callback to handle the addition of a new [FamilyMember] to the tree.
 */
@RequiresApi(Build.VERSION_CODES.N)
@Composable
fun AddFamilyMemberDialog(
    onDismiss: () -> Unit,
    onAddMember: (FamilyMember) -> Unit,
    existingMembers: List<FamilyMember>
) {

    if (existingMembers.isEmpty()) {
        // Add the first family member of the tree
        AddNewMemberToTree(
            onDismiss = onDismiss,
            onAddMember = onAddMember,
            existingMembers = existingMembers,
        )
    } else {
        AddNewMemberAndRelateToExistingMember(
            onDismiss = onDismiss,
            onAddMember = onAddMember,
            existingMembers = existingMembers,
        )
    }
}