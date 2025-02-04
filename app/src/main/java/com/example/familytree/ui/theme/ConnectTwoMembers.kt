package com.example.familytree.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.familytree.data.FamilyMember
import com.example.familytree.data.Relations
import com.example.familytree.ui.theme.dialogs.ChooseMemberToRelateToDialog

@Composable
fun ConnectTwoMembers(
    existingMembers: List<FamilyMember>,
    onDismiss:() -> Unit
) {

    var memberOne by remember { mutableStateOf<FamilyMember?>(null) }
    var memberTwo by remember { mutableStateOf<FamilyMember?>(null) }
    var relationFromMemberOnePerspective by remember { mutableStateOf<Relations?>(null) }

    // Choose first member
    if (memberOne != null) {
        ChooseMemberToRelateToDialog(
            existingMembers = existingMembers,
            onMemberSelected = { memberOne = it },
            showPreviousButton = false,
            onDismiss = onDismiss
        )
    }

    // Choose the relation between the members
    else if (relationFromMemberOnePerspective != null) {

    }

    // Choose second member
    else if (memberTwo != null) {
        ChooseMemberToRelateToDialog(
            existingMembers = existingMembers,
            onMemberSelected = { memberTwo = it },
            onPrevious = { memberOne = null },
            onDismiss = onDismiss
        )
    }



}

