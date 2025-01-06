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
import com.example.familytree.ui.theme.AddMemberTypeDialog  // Updated import

@Composable
fun FamilyTreeScreen(modifier: Modifier = Modifier) {
    val familyTreeData = try {
        FamilyTreeData()
    } catch (e: Exception) {
        Log.e("FamilyTreeScreen", "Error initializing FamilyTreeData", e)
        null
    }

    LaunchedEffect(Unit) {
        familyTreeData?.loadDataFromFirebase()
    }

    if (familyTreeData == null) {
        Log.e("FamilyTreeScreen", "FamilyTreeData initialization failed")
    }

    var searchQuery by remember { mutableStateOf("") }
    var searchResults by remember { mutableStateOf<List<FamilyMember>>(emptyList()) }
    var showDialog by remember { mutableStateOf(false) }
    var showMemberList by remember { mutableStateOf(false) }

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

                    SearchBar(
                        searchQuery,
                        onQueryChange = { searchQuery = it },
                        onSearch = {
                            familyTreeData?.let {
                                searchResults = it.searchForMember(searchQuery)
                            }
                        }
                    )

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
                        AddMemberButton(onAddMember = { showDialog = true })
                        Spacer(modifier = Modifier.height(16.dp))
                        YbmLogo()
                        Spacer(modifier = Modifier.height(16.dp))
                        ShowMembersButton(onShowMembers = { showMemberList = true })
                    }

                    // Show the member type selection dialog (Yeshiva or Non-Yeshiva)
                    if (showDialog) {
                        familyTreeData?.let { fm ->
                            AddMemberTypeDialog(
                                onDismiss = { showDialog = false },
                                onAddMember = { member ->
                                    // After adding the member, dismiss the dialog
                                    fm.addNewFamilyMemberToTree(member) // Call to add the member to your family tree
                                    showDialog = false
                                }
                            )
                        }
                    }

                    // Show member list dialog
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
