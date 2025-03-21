package com.example.familytree.ui.pages.memberListPage

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.navigation.NavController
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.familytree.data.FamilyMember
import com.example.familytree.data.dataManagement.DatabaseManager
import com.example.familytree.ui.FamilyTreeTopBar
import com.example.familytree.ui.HebrewText
import com.example.familytree.ui.MembersSearchBar
import com.example.familytree.ui.PageHeadLine
import com.example.familytree.ui.RightSubTitle
import com.example.familytree.ui.dialogs.InfoOnMemberDialog
import com.example.familytree.ui.intToMachzor
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.runtime.CompositionLocalProvider

@RequiresApi(Build.VERSION_CODES.N)
@Composable
fun MemberListPage(navController: NavController) {

    var isBackButtonEnabled by remember { mutableStateOf(true) }
    val membersToDisplay by remember { mutableStateOf(DatabaseManager.getAllMembers()) }
    var displayMembersInfo by remember { mutableStateOf(false) }
    var chosenMember by remember { mutableStateOf<FamilyMember?>(null) }

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
                MembersSearchBar()
                PageHeadLine(HebrewText.FAMILY_MEMBERS_LIST)

                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    val modifiedMembersToDisplay = membersToDisplay.flatMap { member ->
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

                        items(members.sortedBy { it.getFullName() }) { member ->
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