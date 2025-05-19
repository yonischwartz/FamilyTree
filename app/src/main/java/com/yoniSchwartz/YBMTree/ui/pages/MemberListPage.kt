package com.yoniSchwartz.YBMTree.ui.pages

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.navigation.NavController
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.yoniSchwartz.YBMTree.data.FamilyMember
import com.yoniSchwartz.YBMTree.data.dataManagement.DatabaseManager
import com.yoniSchwartz.YBMTree.ui.FamilyTreeTopBar
import com.yoniSchwartz.YBMTree.ui.HebrewText
import com.yoniSchwartz.YBMTree.ui.MembersSearchBar
import com.yoniSchwartz.YBMTree.ui.PageHeadLine
import com.yoniSchwartz.YBMTree.ui.RightSubTitle
import com.yoniSchwartz.YBMTree.ui.dialogs.InfoOnMemberDialog
import com.yoniSchwartz.YBMTree.ui.intToMachzor

@RequiresApi(Build.VERSION_CODES.N)
@Composable
fun MemberListPage(navController: NavController) {

    // State variable for UI components
    var isBackButtonEnabled by remember { mutableStateOf(true) }

    // State to hold all family members
    val membersToDisplay by remember { mutableStateOf(DatabaseManager.getAllMembers()) }

    // State to hold whether to display member's info dialog
    var displayMembersInfo by remember { mutableStateOf(false) }

    // State to hold the member that was clicked
    var chosenMember by remember { mutableStateOf<FamilyMember?>(null) }

    // Get all family members
    val members = DatabaseManager.getAllMembers()

    // State to hold filtered members based on search input
    var filteredMembers by remember { mutableStateOf(members) }

    Scaffold(
        topBar = {
            FamilyTreeTopBar(
                text = HebrewText.FAMILY_TREE,
                onClickBack = {
                    if (isBackButtonEnabled) {
                        isBackButtonEnabled = false
                        navController.popBackStack()
                    }
                }
            )
        }
    ) { innerPadding ->

        // Force RTL layout
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp)
            ) {
                // Search Bar
                MembersSearchBar(
                    members = members,
                    onSearchResults = { filteredMembers = it }
                )
                PageHeadLine(HebrewText.FAMILY_MEMBERS_LIST)

                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    val modifiedMembersToDisplay = filteredMembers.flatMap { member ->
                        if (member.getIsYeshivaRabbi() && member.getMachzor() != 0) {
                            listOf(member, member.getDuplicateRabbiWithNoMachzor())
                        } else {
                            listOf(member)
                        }
                    }

                    val groupedMembers = modifiedMembersToDisplay.groupBy { it.getMachzor() }
                        .toSortedMap(compareBy { it ?: Int.MAX_VALUE })

                    groupedMembers.forEach { (group, members) ->
                        val subTitle: String = when {
                            group == null -> HebrewText.NON_YESHIVA_FAMILY_MEMBERS
                            group == 0 -> HebrewText.RABBIS_AND_STAFF
                            else -> "${HebrewText.MACHZOR} ${intToMachzor[group]}"
                        }.toString()

                        item {
                            RightSubTitle(subTitle)
                        }

                        items(items = members.sortedBy { it.getFullName() }) { member ->
                        Text(
                                text = member.getFullName(),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        chosenMember = member
                                        displayMembersInfo = true
                                    },
                                style = MaterialTheme.typography.bodyLarge
                            )

                            if (displayMembersInfo) {
                                InfoOnMemberDialog(
                                    member = chosenMember!!,
                                    onDismiss = { displayMembersInfo = false }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}