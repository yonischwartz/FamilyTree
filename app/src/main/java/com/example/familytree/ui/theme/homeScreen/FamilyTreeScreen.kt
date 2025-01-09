package com.example.familytree.ui.theme.homeScreen

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
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
import com.example.familytree.data.dataManagement.FamilyTreeData
import com.example.familytree.ui.theme.MemberListDialog
import com.example.familytree.ui.theme.AddFamilyMemberDialog

/**
 * Composable function that displays the main screen for the family tree application.
 * It provides search functionality, buttons for adding and showing family members,
 * and handles dialogs for member input and member lists.
 *
 * @param modifier Modifier for customizing the layout.
 */
@RequiresApi(Build.VERSION_CODES.N)
@Composable
fun FamilyTreeScreen(modifier: Modifier = Modifier) {
    // Load family tree data from Firebase when the screen is first composed
    LaunchedEffect(Unit) {
        try {
            FamilyTreeData.loadDataFromFirebase()
        } catch (e: Exception) {
            Log.e("FamilyTreeScreen", "Error loading data from Firebase", e)
        }
    }

    // State variables for UI components
    var searchQuery by remember { mutableStateOf("") }
    var searchResults by remember { mutableStateOf<List<FamilyMember>>(emptyList()) }
    var showAddMemberDialog by remember { mutableStateOf(false) }
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
                            searchResults = FamilyTreeData.searchForMember(searchQuery)
                        }
                    )

                    // Show search results in a dialog if not empty
                    if (searchResults.isNotEmpty()) {
                        MemberListDialog(
                            initialFamilyMembers = searchResults,
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
                        AddMemberButton(onAddMember = { showAddMemberDialog = true })
                        Spacer(modifier = Modifier.height(16.dp))
                        YbmLogo() // Display the logo
                        Spacer(modifier = Modifier.height(16.dp))
                        // Button to show all family members
                        ShowMembersButton(onShowMembers = { showMemberList = true })
                    }

                    // Dialog for adding a new family member
                    if (showAddMemberDialog) {
                        AddFamilyMemberDialog(
                            onDismiss = { showAddMemberDialog = false },
                            onAddMember = { member ->
                                FamilyTreeData.addNewFamilyMemberToTree(member)
                                showAddMemberDialog = false
                            },
                            existingMembers = FamilyTreeData.getAllMembers()
                        )
                    }

                    // Dialog to display the list of all family members
                    if (showMemberList) {
                        MemberListDialog(
                            initialFamilyMembers = FamilyTreeData.getAllMembers(),
                            onDismiss = { showMemberList = false }
                        )
                    }
                }
            }
        }
    }
}
