package com.example.familytree.ui.pages.homeScreenPage

import android.net.ConnectivityManager
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
import com.example.familytree.data.FamilyMember
import com.example.familytree.data.dataManagement.DatabaseManager
import com.example.familytree.ui.HebrewText
import com.example.familytree.ui.pages.homeScreenPage.functionForButtons.AddFamilyMember
import android.content.Context
import androidx.compose.foundation.background
import androidx.navigation.NavController
import com.example.familytree.data.dataManagement.DatabaseManager.loadMembersFromFirebaseIntoLocalMap
import com.example.familytree.data.dataManagement.DatabaseManager.saveLocalMapToFirebase
import com.example.familytree.ui.FamilyTreeTopBar
import com.example.familytree.ui.pages.homeScreenPage.functionForButtons.ConnectTwoMembers
import com.example.familytree.ui.WideBlueButton
import com.example.familytree.ui.pages.homeScreenPage.functionForButtons.FindConnectionsBetweenTwoMembers
import com.example.familytree.ui.MembersSearchBar
import com.example.familytree.ui.backgroundColor
import com.example.familytree.ui.dialogs.errorAndSuccessDialogs.NoMembersFoundDialog
import com.example.familytree.ui.theme.dialogs.MemberListDialog

/**
 * Composable function that displays the main screen for the family tree application.
 * It provides search functionality, buttons for adding and showing family members,
 * and handles dialogs for member input and member lists.
 *
 * @param modifier Modifier for customizing the layout.
 */
@RequiresApi(Build.VERSION_CODES.N)
@Composable
fun FamilyTreeScreen(
    modifier: Modifier = Modifier,
    navController: NavController
) {

    // State variables for UI components
    var searchQuery by remember { mutableStateOf("") }
    var searchResults by remember { mutableStateOf<List<FamilyMember>>(emptyList()) }
    var showAddMemberDialog by remember { mutableStateOf(false) }
    var showAddConnectionDialog by remember { mutableStateOf(false) }
    var showFindConnectionDialog by remember { mutableStateOf(false) }
    var showMemberListDialog by remember { mutableStateOf(false) }

    var testing by remember { mutableStateOf(false) }

    // Check for network connectivity
    val context = LocalContext.current
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val networkCapabilities = connectivityManager.activeNetwork ?: return
    val activeNetwork = connectivityManager.getNetworkCapabilities(networkCapabilities)

    LaunchedEffect(Unit) {
        loadMembersFromFirebaseIntoLocalMap { success ->
            if (!success) {
                Toast.makeText(context, HebrewText.ERROR_LOADING_MEMBER_MAP,Toast.LENGTH_LONG).show()
            }
        }
    }

    Scaffold(
        topBar = { FamilyTreeTopBar(HebrewText.FAMILY_TREE) }
    ) { innerPadding ->
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            Box(
                modifier = modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .background(backgroundColor)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {

                    MembersSearchBar()

                    // Show a dialog if no members are found
                    if (searchResults.isEmpty() && searchQuery.isNotEmpty()) {
                        NoMembersFoundDialog(
                            searchQuery = searchQuery,
                            onDismiss = {
                                searchQuery = ""
                                searchResults = emptyList()
                            }
                        )
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.SpaceBetween,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        // Test button
                        WideBlueButton(
                            onClick = { testing = !testing },
                            "test"
                        )

                        if (testing) {
                            DatabaseManager.removeMemberFromLocalMemberMap("6e61404d-464f-4a55-b695-61e2834a2b19")
//                            ClickableShapesCanvas()
                        }

                        // Button for shifting to memberListPage
                        WideBlueButton(
                            onClick = { navController.navigate("memberListPage") },
                            HebrewText.SHOW_ALL_FAMILY_MEMBERS
                        )

                        // Button to add a new family member
                        WideBlueButton(
                            onClick = { showAddMemberDialog = true },
                            HebrewText.ADD_NEW_FAMILY_MEMBER
                        )

                        // Button to add a new connection
                        WideBlueButton(
                            onClick = { showAddConnectionDialog = true },
                            HebrewText.ADD_CONNECTION_BETWEEN_TWO_EXISTING_MEMBERS
                        )

                        // Find connection button
                        WideBlueButton(
                            onClick = { showFindConnectionDialog = true },
                            text = HebrewText.FIND_CONNECTION_BETWEEN_TWO_MEMBERS
                        )

                        // Display the logo
                        YbmLogo()

                        // Button to show all family members
                        WideBlueButton(
                            onClick = { showMemberListDialog = true },
                            HebrewText.SHOW_ALL_FAMILY_MEMBERS
                        )

                        // Button to save and update data to firebase
                        WideBlueButton(
                            onClick = {
                                saveLocalMapToFirebase { success ->
                                    if (success){
                                        Toast.makeText(
                                            context,
                                            HebrewText.SUCCESS_SAVING_MEMBERS_IN_FIREBASE,
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                    else {
                                        Toast.makeText(
                                            context,
                                            HebrewText.ERROR_SAVING_MEMBERS_IN_FIREBASE,
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                }
                            },
                            text = HebrewText.SAVE_AND_UPDATE_MEMBERS_TO_FIREBASE
                        )

                        // Button to load data from firebase to local DB
                        WideBlueButton(
                            onClick = {
                                loadMembersFromFirebaseIntoLocalMap { success ->
                                    if (success){
                                        Toast.makeText(
                                            context,
                                            HebrewText.SUCCESS_LOADING_MEMBER_MAP,
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                    else {
                                        Toast.makeText(
                                            context,
                                            HebrewText.ERROR_LOADING_MEMBER_MAP,
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                }
                            },
                            text = HebrewText.LOAD_MEMBERS_FROM_FIREBASE
                        )
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
}
