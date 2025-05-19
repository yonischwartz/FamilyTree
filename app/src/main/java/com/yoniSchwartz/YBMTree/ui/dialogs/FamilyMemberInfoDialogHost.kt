package com.yoniSchwartz.YBMTree.ui.dialogs

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.yoniSchwartz.YBMTree.data.FamilyMember

@RequiresApi(Build.VERSION_CODES.N)
@Composable
fun FamilyMemberInfoDialogHost(
    initialMember: FamilyMember,
    onDismiss: () -> Unit,
    showRemoveButton: Boolean = false,
    showEditButton: Boolean = false,
    onMemberRemoval: (memberId: String) -> Unit = {},
    onMemberEdit: (member: FamilyMember) -> Unit = {}
) {
    var currentMember by remember { mutableStateOf(initialMember) }

    InfoOnMemberDialog(
        member = currentMember,
        onDismiss = onDismiss,
        showRemoveButton = showRemoveButton,
        showEditButton = showEditButton,
        onMemberRemoval = { memberId ->
            onMemberRemoval(memberId)
        },
        onMemberEdit = { member ->
            onMemberEdit(member)
        }
    )
}
