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
import androidx.compose.foundation.lazy.LazyRow
import androidx.navigation.NavController
import com.example.familytree.ui.ArrowButton
import com.example.familytree.ui.BigRoundButton
import com.example.familytree.ui.CustomizedText
import com.example.familytree.ui.FamilyMemberCube
import com.example.familytree.ui.JewishManButton
import com.example.familytree.ui.JewishWomanButton
import com.example.familytree.ui.WideBlueButton
import com.example.familytree.ui.MembersSearchBar
import com.example.familytree.ui.QuestionMarkButton
import com.example.familytree.ui.YbmLogo
import com.example.familytree.ui.backgroundColor
import com.example.familytree.ui.dialogs.DisplayConnectionBetweenTwoMembersDialog
import com.example.familytree.ui.graphicTreeDisplay.ClickableShapesCanvas

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

    // Get all family members from the database
    val members = DatabaseManager.getAllMembers()

    // Boolean to determine if the "Find Connection" button was clicked
    var findConnectionButtonClicked by remember { mutableStateOf(false) }

    // Family members that were selected by the user
    var firstSelectedMember by remember { mutableStateOf<FamilyMember?>(null) }
    var secondSelectedMember by remember { mutableStateOf<FamilyMember?>(null) }

    // Function to handle clicking on a family member cube
    val onClickCube: (FamilyMember) -> Unit

    // Small buttons to display when the "Find Connection" button is clicked
    var firstSmallButton by remember { mutableStateOf<@Composable () -> Unit> (
        { QuestionMarkButton(onClick = { findConnectionButtonClicked = !findConnectionButtonClicked }) } )
    }
    var secondSmallButton by remember { mutableStateOf<@Composable () -> Unit> (
        { QuestionMarkButton(onClick = { findConnectionButtonClicked = !findConnectionButtonClicked }) } )
    }

    // Boolean to determine if the connection dialog should be displayed
    var showConnectionDialog: Boolean by remember { mutableStateOf(false) }


    var testing by remember { mutableStateOf(false) }

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
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        if (findConnectionButtonClicked) {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    CustomizedText(HebrewText.CHOOSE_TWO_FAMILY_MEMBERS)
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
                                        }

                                        // If member one is selected, display his name and a person button
                                        else {

                                            CustomizedText(firstSelectedMember!!.getFullName())

                                            if (firstSelectedMember!!.getGender()) {

                                                // Display a man's button
                                                JewishManButton(onClick = { findConnectionButtonClicked = !findConnectionButtonClicked })
                                            }

                                            else {

                                                // Display a woman's button
                                                JewishWomanButton(onClick = { findConnectionButtonClicked = !findConnectionButtonClicked })
                                            }
                                        }
                                    }

                                    if (firstSelectedMember != null && secondSelectedMember != null) {
                                        ArrowButton(
                                            onClick = { showConnectionDialog = true }
                                        )
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
                                        }

                                        // If member two is selected, display his name and a person button
                                        else {

                                            CustomizedText(secondSelectedMember!!.getFullName())

                                            if (secondSelectedMember!!.getGender()) {

                                                // Display a man's button
                                                JewishManButton(onClick = { findConnectionButtonClicked = !findConnectionButtonClicked })
                                            }

                                            else {

                                                // Display a woman's button
                                                JewishWomanButton(onClick = { findConnectionButtonClicked = !findConnectionButtonClicked })
                                            }
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

                    Column(modifier = Modifier
                        .fillMaxWidth()
                        .weight(2f)) {

                        MembersSearchBar()

                        BoxWithConstraints(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            val containerHeight = maxHeight
                            val cubeLength = containerHeight / 5  // Dividing the height into 5 parts (4 members + spacing)

                            LazyRow(
                                modifier = Modifier.fillMaxWidth(),
                                contentPadding = PaddingValues(horizontal = 16.dp), // Ensures first & last column are away from edges
                                horizontalArrangement = Arrangement.spacedBy(2.dp) // Ensures spacing between columns
                            ) {
                                val columns = members.chunked(4)

                                items(columns.size) { columnIndex -> // Iterate over chunk indices
                                    val columnMembers = columns[columnIndex] // Get the chunk at index
                                    Column(
                                        modifier = Modifier.padding(vertical = 8.dp), // Keeps columns away from top & bottom edges
                                        verticalArrangement = Arrangement.spacedBy(2.dp) // Ensures spacing between cubes
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

                    // Bottom 1/4 of ths screen

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        WideBlueButton(onClick = { testing = !testing }, text = "test")
                        WideBlueButton(onClick = { navController.navigate("memberListPage") }, text = HebrewText.SHOW_ALL_FAMILY_MEMBERS)
                        WideBlueButton(onClick = { navController.navigate("adminPage") }, text = HebrewText.GO_INTO_ADMIN_MODE)

                        YbmLogo()
                    }

                    if (testing) {
                        DatabaseManager.removeMemberFromLocalMemberMap("6e61404d-464f-4a55-b695-61e2834a2b19")
                        ClickableShapesCanvas()
                    }
                }
            }
        }
    }
}