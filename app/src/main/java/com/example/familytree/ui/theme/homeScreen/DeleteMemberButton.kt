package com.example.familytree.ui.theme.homeScreen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.familytree.data.FamilyMember
import com.example.familytree.data.dataManagement.FamilyTreeData

/**
 * Composable button to delete a family member.
 *
 * @param member The family member to delete.
 * @param onDeleted Callback to perform actions after deletion.
 */
@RequiresApi(Build.VERSION_CODES.N)
@Composable
fun DeleteMemberButton(member: FamilyMember, onDeleted: () -> Unit) {
    Button(onClick = {
        FamilyTreeData.deleteFamilyMember(member.documentId)
        onDeleted()
    }) {
        Text("הסר")
    }
}
