package com.example.familytree.ui.pages

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.platform.LocalLayoutDirection
import com.example.familytree.ui.HebrewText
import com.example.familytree.ui.pages.homeScreenPage.functionForButtons.AddFamilyMember
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.navigation.NavController
import com.example.familytree.data.FamilyMember
import com.example.familytree.data.dataManagement.DatabaseManager
import com.example.familytree.data.dataManagement.DatabaseManager.removeMemberFromLocalMemberMap
import com.example.familytree.data.dataManagement.DatabaseManager.saveLocalMapToFirebase
import com.example.familytree.ui.FamilyTreeTopBar
import com.example.familytree.ui.MembersSearchBar
import com.example.familytree.ui.pages.homeScreenPage.functionForButtons.ConnectTwoMembers
import com.example.familytree.ui.ButtonForPage
import com.example.familytree.ui.pages.homeScreenPage.functionForButtons.FindConnectionsBetweenTwoMembers
import com.example.familytree.ui.PageHeadLine
import com.example.familytree.ui.RightSubTitle
import com.example.familytree.ui.allMachzorim
import com.example.familytree.ui.ScreenBackgroundColor
import com.example.familytree.ui.appTextStyleLargeBlack
import com.example.familytree.ui.dialogs.DemoAdminInfoDialog
import com.example.familytree.ui.dialogs.InfoOnMemberDialog
import com.example.familytree.ui.intToMachzor
import com.example.familytree.ui.machzorToInt
import com.example.familytree.ui.dialogs.MemberListDialog


@RequiresApi(Build.VERSION_CODES.N)
@Composable
fun AdminPage(navController: NavController, isRealAdmin: Boolean) {

    // State variables for UI components
    var isBackButtonEnabled by remember { mutableStateOf(true) }
    var showAddMemberDialog by remember { mutableStateOf(false) }
    var showAddConnectionDialog by remember { mutableStateOf(false) }
    var showFindConnectionDialog by remember { mutableStateOf(false) }
    var showMemberListDialog by remember { mutableStateOf(false) }
    var displayMembersInfo by remember { mutableStateOf(false) }
    var clickedMemberOnList by remember { mutableStateOf<FamilyMember?>(null) }

    // Retrieve members sorted by machzor order
    val members = allMachzorim.flatMap { machzor ->
        DatabaseManager.getMembersByMachzor(machzorToInt[machzor])
    } + DatabaseManager.getMembersByMachzor(null)

    // State to hold filtered members based on search input
    var filteredMembers by remember { mutableStateOf(members) }

    var wasUserInformedAboutDemoAdminMode by remember { mutableStateOf(isRealAdmin) }

    // Filter out members that are yeshiva rabbis with a machzor
    val modifiedMembersToDisplay = filteredMembers.flatMap { member ->
        if (member.getIsYeshivaRabbi() && member.getMachzor() != 0) {
            listOf(member, member.getDuplicateRabbiWithNoMachzor())
        } else {
            listOf(member)
        }
    }

    // Group members by machzor
    val groupedMembers = modifiedMembersToDisplay.groupBy { it.getMachzor() }
        .toSortedMap(compareBy { it ?: Int.MAX_VALUE })

    // Get the current context of the Composable, used for operations requiring a Context (e.g., showing Toasts, accessing resources, etc.)
    val context = LocalContext.current

    // Inform user what is the demo admin mode
    if (!wasUserInformedAboutDemoAdminMode) {

        DemoAdminInfoDialog(
            onDismiss = {
                wasUserInformedAboutDemoAdminMode = true
            }
        )
    }

    Scaffold(
        topBar = {
            FamilyTreeTopBar(
                text = HebrewText.ADMIN_MODE,
                onClickBack = {
                    if (isBackButtonEnabled) {
                        isBackButtonEnabled = false
                        navController.popBackStack()
                    }
                },
                showSaveIcon = isRealAdmin,
                onSave = {
                    saveLocalMapToFirebase { success ->
                        if (success) {
                            Toast.makeText(
                                context,
                                HebrewText.SUCCESS_SAVING_MEMBERS_IN_FIREBASE,
                                Toast.LENGTH_LONG
                            ).show()
                        } else {
                            Toast.makeText(
                                context,
                                HebrewText.ERROR_SAVING_MEMBERS_IN_FIREBASE,
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            Box(
                modifier = Modifier
                    .padding(
                        start = innerPadding.calculateStartPadding(LayoutDirection.Rtl),
                        top = innerPadding.calculateTopPadding(),
                        end = innerPadding.calculateEndPadding(LayoutDirection.Rtl),
                        bottom = 0.dp // Removes bottom padding
                    )
                    .fillMaxSize()
                    .background(ScreenBackgroundColor)
            ) {
                Column(
                    modifier = Modifier.padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {

                    // Headline text
                    PageHeadLine(HebrewText.FAMILY_TREE_MEMBERS)

                    // Search Bar
                    MembersSearchBar(
                        members = members,
                        onSearchResults = { filteredMembers = it },
                        modifier = Modifier.padding(start = 8.dp, end = 8.dp)
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 4.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {

                        // List of family members
                        LazyColumn(
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 4.dp),
                            contentPadding = PaddingValues(vertical = 8.dp)
                        ) {
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
                                                clickedMemberOnList = member
                                                displayMembersInfo = true
                                            },
                                        style = appTextStyleLargeBlack()
                                    )
                                }
                            }
                        }

                        // Dialog to display details of the clicked member
                        clickedMemberOnList?.let { member ->
                            InfoOnMemberDialog(
                                member = member,
                                onDismiss = { clickedMemberOnList = null },
                                showRemoveButton = true,
                                showEditButton = true,
                                onMemberRemoval = { id ->
                                    removeMemberFromLocalMemberMap(id)
                                    filteredMembers = filteredMembers.filter { it.getId() != id }
                                    clickedMemberOnList = null // close dialog
                                },
                                onMemberEdit = { updatedMember ->
                                    val index = filteredMembers.indexOfFirst { it.getId() == updatedMember.getId() }
                                    if (index != -1) {
                                        filteredMembers = filteredMembers.toMutableList().also {
                                            it[index] = updatedMember
                                        }
                                    }
                                    clickedMemberOnList = updatedMember // update the dialog with new data
                                }
                            )
                        }

                        // Bottom buttons
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 4.dp)
                                .padding(horizontal = 0.dp)
                        ) {

                            // Button to add a new family member
                            ButtonForPage(
                                onClick = { showAddMemberDialog = true },
                                HebrewText.ADD_NEW_FAMILY_MEMBER,
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 0.dp)
                            )

                            // Button to add a new connection
                            ButtonForPage(
                                onClick = { showAddConnectionDialog = true },
                                HebrewText.ADD_CONNECTION_BETWEEN_TWO_EXISTING_MEMBERS,
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 0.dp)
                            )
                        }
                    }
                }

                // Dialog for adding a new family member
                if (showAddMemberDialog) {

                    AddFamilyMember( onDismiss = { showAddMemberDialog = false } )
                }

                // Dialog for connecting between two existing members
                if (showAddConnectionDialog) {

                    ConnectTwoMembers( onDismiss = { showAddConnectionDialog = false } )
                }

                // Dialog to display the list of all family members
                if (showFindConnectionDialog) {

                    FindConnectionsBetweenTwoMembers(
                        onDismiss = { showFindConnectionDialog = false }
                    )
                }

                // Dialog to display the list of all family members
                if (showMemberListDialog) {

                    MemberListDialog(
                        onDismiss = { showMemberListDialog = false }
                    )
                }
            }
        }
    }
}