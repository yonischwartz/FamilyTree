package com.example.familytree.ui.theme.homeScreen

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalLayoutDirection
import com.example.familytree.data.FamilyMember
import com.example.familytree.data.dataManagement.*
import com.example.familytree.ui.theme.MemberListDialog
import com.example.familytree.ui.theme.AddFamilyMemberDialog  // Updated import

/**
 * Composable function that displays the main screen for the family tree application.
 * It provides search functionality, buttons for adding and showing family members,
 * and handles dialogs for member input and member lists.
 *
 * @param modifier Modifier for customizing the layout.
 */
@Composable
fun FamilyTreeScreen(modifier: Modifier = Modifier) {
    // Initialize FamilyTreeData with error handling
    val familyTreeData = try {
        FamilyTreeData()
    } catch (e: Exception) {
        Log.e("FamilyTreeScreen", "Error initializing FamilyTreeData", e)
        null
    }

    // Load family tree data from Firebase when the screen is first composed
    LaunchedEffect(Unit) {
        familyTreeData?.loadDataFromFirebase()
    }

    if (familyTreeData == null) {
        Log.e("FamilyTreeScreen", "FamilyTreeData initialization failed")
    }

    // State variables for UI components
    var searchQuery by remember { mutableStateOf("") }
    var searchResults by remember { mutableStateOf<List<FamilyMember>>(emptyList()) }
    var showDialog by remember { mutableStateOf(false) }
    var showMemberList by remember { mutableStateOf(false) }

    // Scaffold provides a consistent visual structure with a top bar
    Scaffold(
        topBar = { FamilyTreeTopBar() }
    ) { innerPadding ->
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            Box(
                modifier = modifier
                    .padding(innerPadding)
                    .fillMaxSize()
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Spacer(modifier = Modifier.height(16.dp))

                    // Search bar for finding family members
                    SearchBar(
                        searchQuery,
                        onQueryChange = { searchQuery = it },
                        onSearch = {
                            familyTreeData?.let {
                                searchResults = it.searchForMember(searchQuery)
                            }
                        }
                    )

                    // Show search results in a dialog if not empty
                    if (searchResults.isNotEmpty()) {
                        MemberListDialog(
                            familyMembers = searchResults,
                            onDismiss = { searchResults = emptyList() }
                        )
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.SpaceBetween,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Button to add a new family member
                        AddMemberButton(onAddMember = { showDialog = true })
                        Spacer(modifier = Modifier.height(16.dp))
                        YbmLogo() // Display the logo
                        Spacer(modifier = Modifier.height(16.dp))
                        // Button to show all family members
                        ShowMembersButton(onShowMembers = { showMemberList = true })
                    }

                    // Dialog for adding a new family member (Yeshiva or Non-Yeshiva)
                    if (showDialog) {
                        familyTreeData?.let { fm ->
                            AddFamilyMemberDialog(
                                onDismiss = { showDialog = false },
                                onAddMember = { member ->
                                    // Add the new member to the family tree
                                    fm.addNewFamilyMemberToTree(member)
                                    showDialog = false
                                }
                            )
                        }
                    }

                    // Dialog to display the list of all family members
                    if (showMemberList) {
                        familyTreeData?.let { fm ->
                            MemberListDialog(
                                familyMembers = fm.getAllMembers(),
                                onDismiss = { showMemberList = false }
                            )
                        }
                    }
                }
            }
        }
    }
}
