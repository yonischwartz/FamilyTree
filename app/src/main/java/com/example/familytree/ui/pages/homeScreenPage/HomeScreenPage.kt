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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import com.example.familytree.data.dataManagement.DatabaseManager.getYeshivaMemberCount
import com.example.familytree.ui.BigRoundButton
import com.example.familytree.ui.CustomizedTextHomeScreenTwoLinesDisplay
import com.example.familytree.ui.Display
import com.example.familytree.ui.FamilyMemberCube
import com.example.familytree.ui.JewishManButton
import com.example.familytree.ui.JewishWomanButton
import com.example.familytree.ui.MembersSearchBar
import com.example.familytree.ui.PageHeadLine
import com.example.familytree.ui.ButtonForPage
import com.example.familytree.ui.FindConnectionButton
import com.example.familytree.ui.PageSubHeadLine
import com.example.familytree.ui.QuestionMarkButton
import com.example.familytree.ui.RightSubTitle
import com.example.familytree.ui.allMachzorim
import com.example.familytree.ui.ScreenBackgroundColor
import com.example.familytree.ui.dialogs.DisplayConnectionBetweenTwoMembersDialog
import com.example.familytree.ui.dialogs.InfoOnMemberDialog
import com.example.familytree.ui.dialogs.AdminPasswordDialog
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

    // Display options for the members
    val displayOption by remember { mutableStateOf(Display.CUBES_IN_COLUMN_SORTED) }

    // State to hold the password dialog visibility
    var showPasswordDialog by remember { mutableStateOf(false) }

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
    var findConnectionButtonClicked by remember { mutableStateOf(true) }

    // Family members that were selected by the user
    var firstSelectedMember by remember { mutableStateOf<FamilyMember?>(null) }
    var secondSelectedMember by remember { mutableStateOf<FamilyMember?>(null) }

    // State to hold the member that was clicked
    var memberToShowHisInfoDialog by remember { mutableStateOf<FamilyMember?>(null) }

    // State to hold whether to display member's info dialog
    var displayMembersInfo by remember { mutableStateOf(false) }

    // Boolean to determine if the connection dialog should be displayed
    var showConnectionDialog: Boolean by remember { mutableStateOf(false) }

    val onClickCube: (FamilyMember) -> Unit

    // Function to handle clicking on a family member cube
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
    } else {
        onClickCube = {}
    }

    Scaffold() {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            Box(
                modifier = modifier
                    .background(ScreenBackgroundColor)
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

                                // Headline text
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    PageHeadLine(HebrewText.CHOOSE_TWO_FAMILY_MEMBERS)
                                }

                                // Display two question marks or the two selected family members
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
                                            QuestionMarkButton(onClick = {  })

                                            // Placeholder for the name display to maintain alignment
                                            Spacer(modifier = Modifier.height(40.dp))
                                        }

                                        // If member one is selected, display his name and a person button
                                        else {

                                            if (firstSelectedMember!!.getGender()) {

                                                // Display a man's button
                                                JewishManButton(onClick = { firstSelectedMember = null })
                                            }

                                            else {

                                                // Display a woman's button
                                                JewishWomanButton(onClick = { firstSelectedMember = null })
                                            }

                                            CustomizedTextHomeScreenTwoLinesDisplay(firstSelectedMember!!.getFullNameThatFitsTheCube())
                                        }
                                    }

                                    // Find connection button and reset selection button
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {

//                                        // Spacer to push the findConnection button down
//                                        Spacer(modifier = Modifier.height(30.dp))

                                        // This box holds the findConnection button
                                        Box(
                                            modifier = Modifier
                                                .height(56.dp)
                                                .wrapContentWidth(),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            FindConnectionButton(
                                                onClick = { showConnectionDialog = true },
                                                enabled = firstSelectedMember != null && secondSelectedMember != null
                                            )
                                        }

//
//                                                ArrowButton(
//                                                    onClick = { showConnectionDialog = true }
//                                                )
//                                            }

                                        if (showConnectionDialog) {
                                            DisplayConnectionBetweenTwoMembersDialog(
                                                memberOne = firstSelectedMember!!,
                                                memberTwo = secondSelectedMember!!,
                                                onDismiss = { showConnectionDialog = false }
                                            )
                                        }

//                                        // Reset selection button
//                                        ButtonForPage(
//                                            onClick = {
//                                                firstSelectedMember = null
//                                                secondSelectedMember = null
//                                            },
//                                            text = HebrewText.RESET_SELECTION,
//                                            modifier = Modifier
//                                                .wrapContentWidth()
//                                                .padding(horizontal = 8.dp)
//                                        )
                                    }

                                    // Second button
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {

                                        // If member two isn't selected yet, display a question mark button
                                        if (secondSelectedMember == null) {
                                            QuestionMarkButton(onClick = { })

                                            // Placeholder for the name display to maintain alignment
                                            Spacer(modifier = Modifier.height(40.dp))
                                        }

                                        // If member two is selected, display his name and a person button
                                        else {

                                            if (secondSelectedMember!!.getGender()) {

                                                // Display a man's button
                                                JewishManButton(onClick = { secondSelectedMember = null })
                                            }

                                            else {

                                                // Display a woman's button
                                                JewishWomanButton(onClick = { secondSelectedMember = null })
                                            }

                                            CustomizedTextHomeScreenTwoLinesDisplay(secondSelectedMember!!.getFullNameThatFitsTheCube())
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

                    // Divider line between top and middle
                    HorizontalDivider(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp),
                        color = Color.Black
                    )

                    // Middle 2/4 of ths screen

                    Column(modifier = Modifier.fillMaxWidth().weight(2.47f)) {

                        // Headline text
                        PageHeadLine(HebrewText.FAMILY_TREE_MEMBERS)

                        // Member counter
                        val memberCount = getYeshivaMemberCount()

                        val memberCountText = "${HebrewText.TOTAL_AMOUNT_OF_YESHIVA_MEMBERS}: $memberCount"

                        PageSubHeadLine(memberCountText)

                        // Search Bar
                        MembersSearchBar(
                            members = members,
                            onSearchResults = { filteredMembers = it },
                            modifier = Modifier.padding(start = 16.dp, end = 16.dp)
                        )

                        if (displayOption == Display.CUBES_IN_COLUMN_SORTED) {
                            BoxWithConstraints(
                                modifier = Modifier.fillMaxSize()
                            ) {
                                val cubesInRow = 4
                                val totalHorizontalPadding = 32.dp
                                val spacingBetweenCubes = 4.dp
                                val totalSpacing = spacingBetweenCubes * (cubesInRow - 1)
                                val availableWidth = maxWidth - totalHorizontalPadding - totalSpacing
                                val cubeWidth = availableWidth / cubesInRow
                                val cubeHeight = 70.dp
                                val showNonYeshiva = remember { mutableStateOf(false) }

                                LazyColumn(
                                    modifier = Modifier.fillMaxSize(),
                                    contentPadding = PaddingValues(vertical = 16.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {

                                    groupedMembers.forEach { (group, members) ->
                                        // Track collapsed state for non-yeshiva group
                                        val isNonYeshivaGroup = group == null
                                        val subTitle: String = when {
                                            isNonYeshivaGroup -> HebrewText.NON_YESHIVA_FAMILY_MEMBERS
                                            group == 0 -> HebrewText.RABBIS_AND_STAFF
                                            else -> "${HebrewText.MACHZOR} ${intToMachzor[group]}"
                                        }

                                        item {
                                            if (isNonYeshivaGroup) {
                                                Row(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(horizontal = 16.dp)
                                                        .clickable { showNonYeshiva.value = !showNonYeshiva.value },
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.SpaceBetween
                                                ) {
                                                    RightSubTitle(
                                                        text = subTitle,
                                                        modifier = Modifier.weight(1f)
                                                    )
                                                    Icon(
                                                        imageVector = if (showNonYeshiva.value) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                                        contentDescription = "Toggle section",
                                                        modifier = Modifier.size(20.dp)
                                                    )
                                                }
                                            } else {
                                                RightSubTitle(
                                                    text = subTitle,
                                                    modifier = Modifier.padding(horizontal = 16.dp)
                                                )
                                            }

                                        }

                                        if (!isNonYeshivaGroup || showNonYeshiva.value) {
                                            val rows = members.sortedBy { it.getFullName() }.chunked(cubesInRow)

                                            items(rows.size) { rowIndex ->
                                                val rowMembers = rows[rowIndex]

                                                Row(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(horizontal = 16.dp),
                                                    horizontalArrangement = Arrangement.spacedBy(spacingBetweenCubes)
                                                ) {
                                                    rowMembers.forEach { member ->
                                                        val isSelected = member == firstSelectedMember || member == secondSelectedMember
                                                        FamilyMemberCube(
                                                            member = member,
                                                            isSelected = isSelected,
                                                            length = cubeHeight,
                                                            width = cubeWidth,
                                                            onClick = { onClickCube(member) },
                                                            onExclamationClick = { memberToShowHisInfoDialog = member }
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        else if (displayOption == Display.CUBES_IN_COLUMN) {
                            BoxWithConstraints(
                                modifier = Modifier.fillMaxSize()
                            ) {
                                val cubesInRow = 4
                                val totalHorizontalPadding = 32.dp
                                val spacingBetweenCubes = 4.dp
                                val totalSpacing = spacingBetweenCubes * (cubesInRow - 1)
                                val availableWidth = maxWidth - totalHorizontalPadding - totalSpacing
                                val cubeWidth = availableWidth / cubesInRow
                                val cubeHeight = 70.dp

                                val sortedMembers = filteredMembers.sortedWith(
                                    compareBy<FamilyMember> { it.getMachzor() == null }
                                        .thenBy { it.getMachzor() }
                                )
                                val rows = sortedMembers.chunked(cubesInRow)

                                LazyColumn(
                                    modifier = Modifier.fillMaxSize(),
                                    contentPadding = PaddingValues(vertical = 16.dp),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    items(rows.size) { rowIndex ->
                                        val rowMembers = rows[rowIndex]

                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 16.dp),
                                            horizontalArrangement = Arrangement.spacedBy(spacingBetweenCubes)
                                        ) {
                                            rowMembers.forEach { member ->
                                                val isSelected =
                                                    member == firstSelectedMember || member == secondSelectedMember
                                                FamilyMemberCube(
                                                    member = member,
                                                    isSelected = isSelected,
                                                    length = cubeHeight,
                                                    width = cubeWidth,
                                                    onClick = { onClickCube(member) },
                                                    onExclamationClick = { memberToShowHisInfoDialog = member }
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        else if (displayOption == Display.CUBES_IN_ROW) {

                            BoxWithConstraints(
                                modifier = Modifier.fillMaxSize()
                            ) {

                                val containerHeight = maxHeight
                                val cubeLength =
                                    containerHeight / 5 // Dividing the height into 5 parts (4 members + spacing)

                                LazyRow(
                                    modifier = Modifier.fillMaxWidth(),
                                    contentPadding = PaddingValues(horizontal = 16.dp),
                                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                                ) {
                                    val columns =
                                        filteredMembers.chunked(4) // Properly chunk the list once

                                    items(columns.size) { columnIndex ->
                                        val columnMembers = columns[columnIndex]

                                        Column(
                                            modifier = Modifier.padding(vertical = 8.dp),
                                            verticalArrangement = Arrangement.spacedBy(2.dp)
                                        ) {
                                            columnMembers.forEach { member ->
                                                val isSelected =
                                                    member == firstSelectedMember || member == secondSelectedMember
                                                FamilyMemberCube(
                                                    member = member,
                                                    isSelected = isSelected,
                                                    length = cubeLength,
                                                    width = 80.dp,
                                                    onClick = { onClickCube(member) },
                                                    onExclamationClick = { memberToShowHisInfoDialog = member }
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        else if (displayOption == Display.LIST) {
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
                                                    memberToShowHisInfoDialog = member
                                                    displayMembersInfo = true
                                                },
                                            style = MaterialTheme.typography.bodyLarge
                                        )
                                    }
                                }
                            }
                        }
                    }

                    memberToShowHisInfoDialog?.let { selectedMember ->
                        InfoOnMemberDialog(
                            member = selectedMember,
                            onDismiss = { memberToShowHisInfoDialog = null }
                        )
                    }

                    // Divider line middle top and bottom
                    HorizontalDivider(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp),
                        color = Color.Black
                    )

                    // Bottom 1/4 of ths screen

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(0.53f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {


                        // part of the beta to see what display is the best
//                        TextFieldWithDropdownMenu(
//                            label = "בחר תצוגה",
//                            options = Display.entries.map { it.name },
//                            selectedOption = displayOption.toString(),
//                            onOptionSelected = { displayOption = Display.valueOf(it!!) }
//                        )

                        ButtonForPage(onClick = { navController.navigate("familyTreeGraphPage") }, text = HebrewText.SHOW_FAMILY_TREE_GRAPH)
                        ButtonForPage(onClick = { showPasswordDialog = true }, text = HebrewText.GO_INTO_ADMIN_MODE)

                        if (showPasswordDialog) {
                            AdminPasswordDialog(
                                onDismiss = { showPasswordDialog = false },
                                onPasswordCorrect = {
                                    showPasswordDialog = false
                                    navController.navigate("adminPage?isRealAdmin=true")
                                },
                                onDemoPasswordCorrect = {
                                    showPasswordDialog = false
                                    navController.navigate("adminPage?isRealAdmin=false")
                                }
                            )
                        }

//                        YbmLogo()
                    }
                }
            }
        }
    }
}