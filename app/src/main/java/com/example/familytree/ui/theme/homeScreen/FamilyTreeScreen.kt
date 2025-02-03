package com.example.familytree.ui.theme.homeScreen

import android.net.ConnectivityManager
import android.net.NetworkCapabilities
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
import com.example.familytree.ui.theme.HebrewText
import com.example.familytree.ui.theme.dialogs.MemberListDialog
import com.example.familytree.ui.theme.dialogs.AddFamilyMemberDialog
import android.content.Context
import com.example.familytree.data.dataManagement.DatabaseManager.loadMembersFromFirebaseIntoLocalMap

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

    // State variables for UI components
    var searchQuery by remember { mutableStateOf("") }
    var searchResults by remember { mutableStateOf<List<FamilyMember>>(emptyList()) }
    var showAddMemberDialog by remember { mutableStateOf(false) }
    var showMemberListDialog by remember { mutableStateOf(false) }
    var isFirestoreAccessible by remember { mutableStateOf(false) }
    var isNetworkAvailable by remember { mutableStateOf(false) }

    // Check for network connectivity
    val context = LocalContext.current
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val networkCapabilities = connectivityManager.activeNetwork ?: return
    val activeNetwork = connectivityManager.getNetworkCapabilities(networkCapabilities)
    isNetworkAvailable = activeNetwork?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true

    LaunchedEffect(Unit) {
        loadMembersFromFirebaseIntoLocalMap { success ->
            if (!success) {
                Toast.makeText(context, HebrewText.ERROR_LOADING_MEMBER_MAP,Toast.LENGTH_LONG).show()
            }
        }
    }

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
                            searchResults = DatabaseManager.searchForMemberInLocalMap(searchQuery)
                        }
                    )

                    // Show search results in a dialog if not empty
                    if (searchResults.isNotEmpty()) {
                        MemberListDialog(
                            existingMembers = searchResults,
                            onDismiss = { searchResults = emptyList() }
                        )
                    }
                    else {
                        // TODO: add message "didn't find family members"
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.SpaceBetween,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        // Button to add a new family member
                        HomeScreenButton(
                            onClick = { showAddMemberDialog = true },
                            HebrewText.ADD_NEW_FAMILY_MEMBER
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Display the logo
                        YbmLogo()

                        Spacer(modifier = Modifier.height(16.dp))

                        // Button to show all family members
                        HomeScreenButton(
                            onClick = { showMemberListDialog = true },
                            HebrewText.SHOW_ALL_FAMILY_MEMBERS
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        HomeScreenButton(
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

                        AddFamilyMemberDialog(
                            existingMembers = DatabaseManager.getAllMembers(),
                            onDismiss = { showAddMemberDialog = false }
                        )
                    }

                    // Dialog to display the list of all family members
                    if (showMemberListDialog) {

                        MemberListDialog(
                            existingMembers = DatabaseManager.getAllMembers(),
                            onDismiss = { showMemberListDialog = false }
                        )
                    }
                }
            }
        }
    }
}
