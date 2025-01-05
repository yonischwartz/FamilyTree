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
import com.example.familytree.data.dataManagement.*
import androidx.compose.runtime.LaunchedEffect

@Composable
fun FamilyTreeScreen(modifier: Modifier = Modifier) {
    // Try-catch for initialization errors
    val familyTreeData = try {
        FamilyTreeData()
    } catch (e: Exception) {
        Log.e("FamilyTreeScreen", "Error initializing FamilyTreeData", e)
        null
    }

    // Call loadDataFromFirebase when the screen is opened
    LaunchedEffect(Unit) {
        familyTreeData?.loadDataFromFirebase()
    }

    if (familyTreeData == null) {
        // Handle error or display an error message
        Log.e("FamilyTreeScreen", "FamilyTreeData initialization failed")
    }

    var searchQuery by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }
    var showMemberList by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "עץ משפחה",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onPrimary,
                    textAlign = TextAlign.Center
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .padding(innerPadding)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("חפש לפי שם") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                textStyle = MaterialTheme.typography.bodyMedium.copy(textAlign = TextAlign.Right)
            )

            // LazyColumn with placeholder content
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                item {
                    Text(
                        text = "אין בני משפחה להצגה.",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground,
                        textAlign = TextAlign.Right
                    )
                }
            }

            // Button to open the dialog for adding a new member
            Button(
                onClick = { showDialog = true },
                modifier = Modifier.padding(16.dp)
            ) {
                Text("הוסף בן משפחה חדש")
            }

            Button(
                onClick = { showMemberList = true },
                modifier = Modifier.padding(16.dp)
            ) {
                Text("הצג את כל בני המשפחה")
            }

            // Show the dialog if showDialog is true
            if (showDialog) {
                familyTreeData?.let { fm ->
                    NewMemberDialog(
                        familyTreeData = fm,
                        onDismiss = { showDialog = false }
                    )
                }
            }

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
