package com.example.familytree.ui.pages.homeScreenPage

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.platform.LocalLayoutDirection
import com.example.familytree.data.FamilyMember
import com.example.familytree.data.dataManagement.DatabaseManager
import com.example.familytree.ui.HebrewText
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.navigation.NavController
import com.example.familytree.ui.ArrowButton
import com.example.familytree.ui.BigRoundButton
import com.example.familytree.ui.CustomizedTextHomeScreenTwoLinesDisplay
import com.example.familytree.ui.FamilyMemberCube
import com.example.familytree.ui.JewishManButton
import com.example.familytree.ui.JewishWomanButton
import com.example.familytree.ui.MembersSearchBar
import com.example.familytree.ui.PageHeadLine
import com.example.familytree.ui.WideBlueButton
import com.example.familytree.ui.QuestionMarkButton
import com.example.familytree.ui.RightSubTitle
import com.example.familytree.ui.allMachzorim
import com.example.familytree.ui.backgroundColor
import com.example.familytree.ui.dialogs.DisplayConnectionBetweenTwoMembersDialog
import com.example.familytree.ui.dialogs.InfoOnMemberDialog
import com.example.familytree.ui.intToMachzor
import com.example.familytree.ui.machzorToInt

/**
 * Composable function that displays the home screen of the family tree application.
 *
 * @param modifier Modifier to apply layout customizations.
 * @param navController NavController for handling navigation between screens.
 */
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@RequiresApi(Build.VERSION_CODES.N)
@Composable
fun HomeScreenPage(
    modifier: Modifier = Modifier,
    navController: NavController
) {

    // Retrieve members sorted by machzor order
    val members = allMachzorim.flatMap { machzor ->
        DatabaseManager.getMembersByMachzor(machzorToInt[machzor])
    } + DatabaseManager.getMembersByMachzor(null)

    // State to hold filtered members based on search input
    var filteredMembers by remember { mutableStateOf(members) }

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

    // Boolean to determine if the "Find Connection" button was clicked
    var findConnectionButtonClicked by remember { mutableStateOf(false) }

    // Family members that were selected by the user
    var firstSelectedMember by remember { mutableStateOf<FamilyMember?>(null) }
    var secondSelectedMember by remember { mutableStateOf<FamilyMember?>(null) }

    // State to hold the member that was clicked
    var clickedMemberOnList by remember { mutableStateOf<FamilyMember?>(null) }

    // State to hold whether to display member's info dialog
    var displayMembersInfo by remember { mutableStateOf(false) }

    // Function to handle clicking on a family member cube
    val onClickCube: (FamilyMember) -> Unit

    // Boolean to determine if the connection dialog should be displayed
    var showConnectionDialog: Boolean by remember { mutableStateOf(false) }

    // Update the small buttons to display the selected family members
    if (findConnectionButtonClicked) {
        onClickCube = { clickedMember ->
            when {
                firstSelectedMember == clickedMember -> { firstSelectedMember = null }
                secondSelectedMember == clickedMember -> { secondSelectedMember = null }
                firstSelectedMember == null -> { firstSelectedMember = clickedMember }
                secondSelectedMember == null -> { secondSelectedMember = clickedMember }
                else -> {} // Do nothing if both are already taken and a different member is clicked
            }
        }
    }

    // ???
    else {
        onClickCube = { }
    }

    Scaffold() {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            Box(
                modifier = modifier
                    .background(backgroundColor)
                    .fillMaxSize()
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {

                    // Top 1/4 of the screen

                    Box(
                        modifier = Modifier.fillMaxWidth().weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        if (findConnectionButtonClicked) {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    PageHeadLine(HebrewText.CHOOSE_TWO_FAMILY_MEMBERS)
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceAround,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {

                                    // First button
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {

                                        // If member one isn't selected yet, display a question mark button
                                        if (firstSelectedMember == null) {
                                            QuestionMarkButton(onClick = { findConnectionButtonClicked = !findConnectionButtonClicked })

                                            // Placeholder for the name display to maintain alignment
                                            Spacer(modifier = Modifier.height(40.dp))
                                        }

                                        // If member one is selected, display his name and a person button
                                        else {

                                            if (firstSelectedMember!!.getGender()) {

                                                // Display a man's button
                                                JewishManButton(onClick = { findConnectionButtonClicked = !findConnectionButtonClicked })
                                            }

                                            else {

                                                // Display a woman's button
                                                JewishWomanButton(onClick = { findConnectionButtonClicked = !findConnectionButtonClicked })
                                            }

                                            CustomizedTextHomeScreenTwoLinesDisplay(firstSelectedMember!!.getFullName())
                                        }
                                    }

                                    // Arrow button (or placeholder)
                                    if (firstSelectedMember != null && secondSelectedMember != null) {
                                        ArrowButton(
                                            onClick = { showConnectionDialog = true }
                                        )
                                    }

                                    // Reserve space for the arrow button
                                    else {
                                        Spacer(modifier = Modifier.size(48.dp))
                                    }

                                    if (showConnectionDialog) {
                                        DisplayConnectionBetweenTwoMembersDialog(
                                            memberOne =  firstSelectedMember!!,
                                            memberTwo = secondSelectedMember!!,
                                            onDismiss = { showConnectionDialog = false } )
                                    }

                                    // Second button
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {

                                        // If member two isn't selected yet, display a question mark button
                                        if (secondSelectedMember == null) {
                                            QuestionMarkButton(onClick = { findConnectionButtonClicked = !findConnectionButtonClicked })

                                            // Placeholder for the name display to maintain alignment
                                            Spacer(modifier = Modifier.height(40.dp))
                                        }

                                        // If member two is selected, display his name and a person button
                                        else {

                                            if (secondSelectedMember!!.getGender()) {

                                                // Display a man's button
                                                JewishManButton(onClick = { findConnectionButtonClicked = !findConnectionButtonClicked })
                                            }

                                            else {

                                                // Display a woman's button
                                                JewishWomanButton(onClick = { findConnectionButtonClicked = !findConnectionButtonClicked })
                                            }

                                            CustomizedTextHomeScreenTwoLinesDisplay(secondSelectedMember!!.getFullName())
                                        }
                                    }
                                }
                            }
                        } else {
                            BigRoundButton(
                                onClick = { findConnectionButtonClicked = true },
                                text = HebrewText.FIND_CONNECTION_BETWEEN_TWO_MEMBERS
                            )
                        }
                    }

                    // Middle 2/4 of ths screen

                    Column(modifier = Modifier.fillMaxWidth().weight(2f)) {

                        // Headline text
                        PageHeadLine(HebrewText.FAMILY_TREE_MEMBERS)

                        // Search Bar
                        MembersSearchBar(
                            members = members,
                            onSearchResults = { filteredMembers = it }
                        )

                        // If the "Find Connection" button was clicked, display members as blocks
                        if (findConnectionButtonClicked) {
                            BoxWithConstraints(
                                modifier = Modifier.fillMaxSize()
                            ) {
                                val containerHeight = maxHeight
                                val cubeLength = containerHeight / 5 // Dividing the height into 5 parts (4 members + spacing)


//                                // This is when i tried to split the cubes by machzor
//                                val groupedMembers =
//                                    filteredMembers.groupBy { it.getMachzor() } // Group members by machzor
//
//                                LazyRow(
//                                    modifier = Modifier.fillMaxWidth(),
//                                    contentPadding = PaddingValues(horizontal = 16.dp),
//                                    horizontalArrangement = Arrangement.spacedBy(2.dp)
//                                ) {
//                                    val sortedMachzorKeys = groupedMembers.keys.filterNotNull()
//                                        .sorted() + listOf(null) // Sort machzorim & include null
//                                    val columns = sortedMachzorKeys.flatMap { machzor ->
//                                        groupedMembers[machzor]?.chunked(4)
//                                            ?: emptyList() // Split each machzor group into chunks of 4
//                                    }


                                LazyRow(
                                    modifier = Modifier.fillMaxWidth(),
                                    contentPadding = PaddingValues(horizontal = 16.dp),
                                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                                ) {
                                    val columns = filteredMembers.chunked(4) // Properly chunk the list once

                                    items(columns.size) { columnIndex ->
                                        val columnMembers = columns[columnIndex]

                                        Column(
                                            modifier = Modifier.padding(vertical = 8.dp),
                                            verticalArrangement = Arrangement.spacedBy(2.dp)
                                        ) {
                                            columnMembers.forEach { member ->
                                                val isSelected = member == firstSelectedMember || member == secondSelectedMember
                                                FamilyMemberCube(
                                                    member = member,
                                                    isSelected = isSelected,
                                                    length = cubeLength,
                                                    width = 80.dp,
                                                    onClick = { onClickCube(member) }
                                                )
                                            }
                                        }
                                    }
                                }
                            }


                        }

                        // If the "Find Connection" button wasn't clicked, display members as a list
                        else {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp)
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
                                            style = MaterialTheme.typography.bodyLarge
                                        )

                                        if (displayMembersInfo) {
                                            InfoOnMemberDialog(
                                                member = clickedMemberOnList!!,
                                                onDismiss = { displayMembersInfo = false }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Bottom 1/4 of ths screen

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        WideBlueButton(onClick = { navController.navigate("familyTreeGraphPage") }, text = HebrewText.SHOW_FAMILY_TREE_GRAPH)
                        WideBlueButton(onClick = { navController.navigate("adminPage") }, text = HebrewText.GO_INTO_ADMIN_MODE)

//                        YbmLogo()
                    }
                }
            }
        }
    }
}