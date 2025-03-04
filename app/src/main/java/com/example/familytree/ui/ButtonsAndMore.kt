package com.example.familytree.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import com.example.familytree.data.FamilyMember
import com.example.familytree.data.dataManagement.DatabaseManager
import com.example.familytree.ui.dialogs.InfoOnMemberDialog


/**
 * A constant color value representing a beige background color.
 */
val backgroundColor = Color(0xFFF5F5DC) // Beige

/**
 * A customized text composable that displays text with certain style.
 *
 * @param text The string to be displayed.
 */
@Composable
fun CustomizedText(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium
    )
}

/**
 * Displays a prominent page headline with centered text and padding.
 *
 * @param headline The text to be displayed as the page headline.
 */
@Composable
fun PageHeadLine(headline: String) {
    Text(
        text = headline,
        style = MaterialTheme.typography.headlineMedium,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        fontSize = 24.sp,
        textAlign = TextAlign.Center
    )
}


/**
 * Displays a subtitle aligned to the right with consistent styling and an underline.
 *
 * @param subtitle The text to be displayed as the subtitle.
 */
@Composable
fun RightSubTitle(subtitle: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.Start
    ) {
        Text(
            text = subtitle,
            style = MaterialTheme.typography.titleMedium.copy(
                textDecoration = TextDecoration.Underline
            ),
            fontSize = 20.sp,
            textAlign = TextAlign.End,
            modifier = Modifier.padding(end = 8.dp)
        )
    }
}


/**
 * A Composable function that represents an home screen button.
 *
 * @param onClick A lambda function to handle the action when the button is clicked.
 * @param text A text to display on the button.
 */
@Composable
fun WideBlueButton(onClick: () -> Unit, text: String) {
    Button(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge.copy(textAlign = TextAlign.Center)
        )
    }
}

/**
 * A generic small button for dialogs.
 *
 * @param text The text to display on the button.
 * @param onClick The action to perform when the button is clicked.
 * @param enabled Whether the button is enabled or disabled (default is true).
 */
@Composable
fun DialogButton(
    text: String,
    onClick: () -> Unit,
    enabled: Boolean = true
) {
    Button(onClick = onClick, enabled = enabled) {
        Text(text)
    }
}

/**
 * A composable function that displays a centered title within a dialog.
 *
 * @param title The text to be displayed as the title.
 */
@Composable
fun DialogTitle(title: String) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall
        )
    }
}

/**
 * A reusable wide text input field for free-text entry.
 *
 * @param text The label describing the text field.
 * @param modifier Modifier to text field.
 * @param value The current text value entered by the user.
 * @param onValueChange Callback triggered when the text value changes.
 */
@Composable
fun TextField(
    text: String,
    modifier: Modifier = Modifier.fillMaxWidth(),
    value: String,
    onValueChange: (String) -> Unit
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(text) },
        modifier = modifier
    )
}

/**
 * A composable function that renders a boolean selection using radio buttons.
 *
 * This component allows users to choose between two options, typically "Yes" and "No".
 *
 * @param label A descriptive text displayed above the radio buttons.
 * @param optionOne The text label for the `true` selection (default: HebrewText.YES).
 * @param optionTwo The text label for the `false` selection (default: HebrewText.NO).
 * @param selected The currently selected boolean value (default: `true`).
 * @param onChange A callback function triggered when the selection changes.
 */
@Composable
fun BooleanSelection(
    label: String,
    optionOne: String = HebrewText.YES,
    optionTwo: String = HebrewText.NO,
    selected: Boolean = true,
    onChange: (Boolean) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
        Text(label, modifier = Modifier.padding(bottom = 8.dp))
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Start) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f).padding(end = 8.dp)
            ) {
                RadioButton(
                    selected = selected,
                    onClick = { onChange(true) }
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(optionOne)
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                RadioButton(
                    selected = !selected,
                    onClick = { onChange(false) }
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(optionTwo)
            }
        }
    }
}

/**
 * Reusable dropdown menu composable.
 *
 * @param label The label to be displayed on the dropdown.
 * @param modifier Modifier to text field.
 * @param options The list of options to display in the dropdown menu.
 * @param selectedOption The currently selected option as a string.
 * @param onOptionSelected Callback triggered when an option is selected.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextFieldWithDropdownMenu(
    label: String,
    modifier: Modifier = Modifier.fillMaxWidth(),
    options: List<String>,
    selectedOption: String?,
    onOptionSelected: (String?) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var selected by remember { mutableStateOf(selectedOption) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        TextField(
            value = selected ?: "",
            onValueChange = {},
            label = { Text(label) },
            readOnly = true,
            trailingIcon = {
                Icon(
                    imageVector = if (expanded) Icons.Filled.ArrowDropUp else Icons.Filled.ArrowDropDown,
                    contentDescription = null
                )
            },
            modifier = modifier
                .menuAnchor()
                .clickable { expanded = true },
            singleLine = false
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = option,
                            modifier = Modifier.fillMaxWidth(),
                            overflow = TextOverflow.Ellipsis,
                            style = LocalTextStyle.current.copy(textDirection = TextDirection.Rtl)
                        )
                    },
                    onClick = {
                        selected = option
                        onOptionSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

/**
 * A row of two customizable confirmation buttons with optional enabled/disabled states.
 *
 * @param textForLeftButton The text displayed on the left button.
 * @param onClickForLeftButton The action to perform when the left button is clicked.
 * @param enabledForLeftButton Whether the left button is enabled (default is true).
 * @param textForRightButton The text displayed on the right button.
 * @param onClickForRightButton The action to perform when the right button is clicked.
 * @param enabledForRightButton Whether the right button is enabled (default is true).
 */
@Composable
fun TwoConfirmButtons(
    textForLeftButton: String,
    onClickForLeftButton: () -> Unit,
    enabledForLeftButton: Boolean = true,
    textForRightButton: String,
    onClickForRightButton: () -> Unit,
    enabledForRightButton: Boolean = true,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Right button
        DialogButton(
            text = textForRightButton,
            onClick = onClickForRightButton,
            enabled = enabledForRightButton
        )

        // Left button
        DialogButton(
            text = textForLeftButton,
            onClick = onClickForLeftButton,
            enabled = enabledForLeftButton
        )
    }
}

/**
 * A composable function that displays a top app bar with a centered title.
 * Optionally displays a back arrow icon in the top right corner if [onClickBack] is provided.
 *
 * @param text The title text to display in the top bar.
 * @param onClickBack Optional click handler for the back arrow icon.
 */
@Composable
fun FamilyTreeTopBar(
    text: String,
    onClickBack: (() -> Unit)? = null
) {
    // Force RTL layout direction for this composable
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .background(MaterialTheme.colorScheme.primary)
        ) {
            // Centered title text
            Text(
                text = text,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onPrimary,
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.Center)
            )

            // Optional back arrow icon (always on the right in RTL layout)
            onClickBack?.let {
                IconButton(
                    onClick = it,
                    modifier = Modifier
                        .align(Alignment.TopStart) // Always on the right side for RTL
                        .padding(start = 8.dp) // Adds padding from the left edge in RTL
                ) {
                    // Always points left (correct for RTL)
                    Icon(
                        imageVector = Icons.Filled.ArrowForward,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }
    }
}

/**
 * A search bar composable that allows users to search for family members by name and display their information.
 * It provides a dropdown menu with search results and shows a detailed information dialog when a member is selected.
 */
@RequiresApi(Build.VERSION_CODES.N)
@Composable
fun MembersSearchBar() {
    var searchQuery by remember { mutableStateOf("") }
    var searchResults by remember { mutableStateOf<List<FamilyMember>>(emptyList()) }
    var chosenMemberToShowIsInfo by remember { mutableStateOf<FamilyMember?>(null) }
    var isDropdownExpanded by remember { mutableStateOf(false) }
    val searchBarPosition = remember { mutableStateOf(Offset.Zero) }
    val searchBarHeight = with(LocalDensity.current) { 56.dp.toPx() } // Approximate TextField height

    Box(modifier = Modifier.fillMaxWidth()) {
        // The search bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { query ->
                searchQuery = query
                searchResults = if (query.isEmpty()) {
                    emptyList()
                } else {
                    DatabaseManager.searchForMemberInLocalMap(query)
                }
                isDropdownExpanded = searchResults.isNotEmpty()
            },
            label = { CustomizedText(HebrewText.SEARCH_BY_NAME) },
            textStyle = MaterialTheme.typography.bodyMedium.copy(
                textAlign = TextAlign.Right,
                textDirection = TextDirection.Rtl
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .onGloballyPositioned { coordinates ->
                    searchBarPosition.value = coordinates.positionInWindow()
                }
        )

        // Floating dropdown menu under the search bar
        if (isDropdownExpanded) {
            Popup(
                alignment = Alignment.TopStart,
                offset = IntOffset(
                    x = searchBarPosition.value.x.toInt(),
                    y = (searchBarPosition.value.y + searchBarHeight - with(LocalDensity.current) { 64.dp.toPx() }).toInt()
                )
            ) {
                Card(
                    elevation = CardDefaults.cardElevation(8.dp),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .heightIn(max = 400.dp) // Limit max height
                ) {
                    Box(
                        modifier = Modifier
                            .background(Color.White)
                            .padding(8.dp)
                    ) {
                        LazyColumn(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(searchResults) { member ->
                                DropdownMenuItem(
                                    text = { Text(text = member.getFullName()) },
                                    onClick = {
                                        chosenMemberToShowIsInfo = member
                                        isDropdownExpanded = false
                                    }
                                )
                            }
                        }

                        // Close button in the bottom-right corner
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.BottomEnd
                        ) {
                            Button(
                                onClick = { isDropdownExpanded = false },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Blue),
                                contentPadding = PaddingValues(4.dp),
                                modifier = Modifier.padding(8.dp)
                            ) {
                                Text(HebrewText.CLOSE, fontSize = 12.sp, color = Color.White)
                            }
                        }
                    }
                }
            }
        }
    }

    // Display the member information dialog
    chosenMemberToShowIsInfo?.let { selectedMember ->
        InfoOnMemberDialog(
            member = selectedMember,
            onDismiss = { chosenMemberToShowIsInfo = null }
        )
    }
}
