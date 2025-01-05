package com.example.familytree.ui.theme

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.text.style.TextDirection
import com.example.familytree.data.dataManagement.*
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.foundation.lazy.items


/**
 * Composable function that displays the Family Tree Screen.
 * This screen allows users to view and manage family members.
 * It includes features such as search functionality, a list of family members, and options to add new members.
 */
@Composable
fun FamilyTreeScreen(modifier: Modifier = Modifier) {
    // Initialize FamilyTreeData with error handling.
    val familyTreeData = try {
        FamilyTreeData()
    } catch (e: Exception) {
        Log.e("FamilyTreeScreen", "Error initializing FamilyTreeData", e)
        null
    }

    // Load data from Firebase when the screen is opened.
    LaunchedEffect(Unit) {
        familyTreeData?.loadDataFromFirebase()
    }

    if (familyTreeData == null) {
        // Log an error if FamilyTreeData initialization fails.
        Log.e("FamilyTreeScreen", "FamilyTreeData initialization failed")
    }

    // State variables for managing UI elements.
    var searchQuery by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }
    var showMemberList by remember { mutableStateOf(false) }
    var triggerSearch by remember { mutableStateOf(false) }

    // Filter family members based on searchQuery
    val filteredMembers = familyTreeData?.getAllMembers()?.filter {
        it.getFullName().contains(searchQuery, ignoreCase = true)
    } ?: emptyList()

    Scaffold(
        topBar = {
            // Top bar displaying the screen title.
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "עץ משפחה",  // Hebrew text for "Family Tree".
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onPrimary,
                    textAlign = TextAlign.Center
                )
            }
        }
    ) { innerPadding ->
        // Layout direction is set to RTL for Hebrew content.
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            Column(
                modifier = modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                // Search Bar with a search button.
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        label = { Text("חפש לפי שם") }, // Hebrew text for "Search by name".
                        modifier = Modifier.weight(1f),
                        textStyle = MaterialTheme.typography.bodyMedium.copy(
                            textAlign = TextAlign.Right,
                            textDirection = TextDirection.Rtl
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = { triggerSearch = true },
                        contentPadding = PaddingValues(12.dp)
                    ) {
                        Text(
                            text = "חפש", // Hebrew text for "Search".
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }


                // Placeholder content for the family member list.
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    if (filteredMembers.isEmpty()) {
                        item {
                            Text(
                                text = "אין בני משפחה להצגה.", // Hebrew text for "No family members to display".
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    textAlign = TextAlign.Right,
                                    textDirection = TextDirection.Rtl
                                ),
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    } else {
                        items(filteredMembers) { member ->
                            Text(
                                text = member.getFullName(),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    textAlign = TextAlign.Right,
                                    textDirection = TextDirection.Rtl
                                ),
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    }
                }

                // Button to open the dialog for adding a new family member.
                Button(
                    onClick = { showDialog = true },
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "הוסף בן משפחה חדש", // Hebrew text for "Add New Family Member".
                        style = MaterialTheme.typography.bodyMedium.copy(textAlign = TextAlign.Center)
                    )
                }

                // Button to show the list of all family members.
                Button(
                    onClick = { showMemberList = true },
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "הצג את כל בני המשפחה", // Hebrew text for "Show All Family Members".
                        style = MaterialTheme.typography.bodyMedium.copy(textAlign = TextAlign.Center)
                    )
                }

                // Display the "Add New Member" dialog if showDialog is true.
                if (showDialog) {
                    familyTreeData?.let { fm ->
                        NewMemberDialog(
                            familyTreeData = fm,
                            onDismiss = { showDialog = false }
                        )
                    }
                }

                // Display the "Show All Members" dialog if showMemberList is true.
                if (showMemberList) {
                    familyTreeData?.let { fm ->
                        MemberListDialog(
                            familyTreeData = fm,
                            onDismiss = { showMemberList = false }
                        )
                    }
                }
            }
        }
    }
}