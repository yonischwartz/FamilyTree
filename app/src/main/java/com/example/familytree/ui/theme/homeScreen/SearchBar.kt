package com.example.familytree.ui.theme.homeScreen

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment

/**
 * A composable function that represents a search bar with an input field and a search button.
 *
 * @param searchQuery The current text input in the search field.
 * @param onQueryChange A lambda function to update the search query when the input changes.
 * @param onSearch A lambda function to trigger the search action when the search button is clicked.
 *
 * The search bar consists of:
 * - An [OutlinedTextField] that allows the user to enter a search query, with the text aligned to the right and text direction set to right-to-left (for Hebrew).
 * - A [Button] labeled "חפש" (Search) that triggers the [onSearch] function when clicked.
 */
@Composable
fun SearchBar(
    searchQuery: String,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp), // Adds horizontal padding for spacing.
        verticalAlignment = Alignment.CenterVertically // Centers the children vertically in the row.
    ) {
        // TextField for user input
        OutlinedTextField(
            value = searchQuery, // Displays the current search query.
            onValueChange = onQueryChange, // Updates the search query when the text changes.
            label = { Text("חפש לפי שם") }, // Hebrew label for "Search by name".
            modifier = Modifier.weight(1f), // Makes the text field take up most of the available space.
            textStyle = MaterialTheme.typography.bodyMedium.copy(
                textAlign = TextAlign.Right, // Aligns text to the right.
                textDirection = TextDirection.Rtl // Sets the text direction to right-to-left for Hebrew.
            )
        )
        Spacer(modifier = Modifier.width(8.dp)) // Adds a horizontal space between the text field and the button.

        // Search button
        Button(
            onClick = onSearch, // Executes the search function when clicked.
            contentPadding = PaddingValues(12.dp) // Adds padding inside the button for better spacing.
        ) {
            Text(text = "חפש", style = MaterialTheme.typography.bodyMedium) // Hebrew text for "Search".
        }
    }
}
