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
import com.example.familytree.data.*

@Composable
fun FamilyTreeScreen(modifier: Modifier = Modifier) {
    // Try-catch for initialization errors
    val firebaseManager = try {
        FirebaseManager(MemberMapByID.getInstance(), FamilyConnections.getInstance())
    } catch (e: Exception) {
        Log.e("FamilyTreeScreen", "Error initializing FirebaseManager", e)
        null
    }

    if (firebaseManager == null) {
        // Handle error or display an error message
        Log.e("FamilyTreeScreen", "FirebaseManager initialization failed")
    }

    var searchQuery by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }  // State to show/hide dialog

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
                label = { Text("חפש לפי שם") }, // Translated to "Search by Name"
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
                        text = "אין בני משפחה להצגה.", // Translated to "No family members to display."
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
                Text("הוסף בן משפחה חדש") // Translated to "Add New Family Member"
            }

            // Show the dialog if showDialog is true
            if (showDialog) {
                firebaseManager?.let { fm ->
                    NewMemberDialog(
                        firebaseManager = fm,
                        onDismiss = { showDialog = false }
                    )
                }
            }
        }
    }
}
