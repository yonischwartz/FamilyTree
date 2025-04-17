package com.example.familytree.ui

import android.graphics.PointF
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Clear
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Popup
import com.example.familytree.R
import com.example.familytree.data.FamilyMember
import com.example.familytree.data.dataManagement.DatabaseManager
import com.example.familytree.ui.dialogs.InfoOnMemberDialog
import java.io.File
import android.net.Uri
import androidx.compose.foundation.layout.offset
import androidx.compose.material.icons.filled.KeyboardDoubleArrowLeft
import androidx.compose.material.icons.filled.KeyboardDoubleArrowRight
import androidx.compose.material3.CircularProgressIndicator
import com.davemorrissey.labs.subscaleview.ImageSource
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView

/**
 * A constant color value representing a beige background color.
 */
val backgroundColor = Color(0xFFF5F5DC) // Beige

/**
 * Returns the default text style used in the app.
 *
 * This function provides a consistent typography style for text elements
 * by using `MaterialTheme.typography.bodyMedium`. It ensures that the text
 * follows the app's theme settings.
 *
 * @return A [TextStyle] representing the app's default text appearance.
 */
@Composable
fun appTextStyle(): TextStyle {
    return MaterialTheme.typography.bodyMedium
}

/**
 * A Composable function that displays the YBM logo image.
 */
@Composable
fun YbmLogo() {
    Image(
        painter = painterResource(id = R.drawable.ybm),
        contentDescription = "Family Tree Image",
        modifier = Modifier
            .fillMaxWidth()
            .height(400.dp)
    )
}

/**
 * A customized text composable that displays text with certain style.
 *
 * @param text The string to be displayed.
 */
@Composable
fun CustomizedText(text: String) {
    Text(
        text = text,
        style = appTextStyle()
    )
}

/**
 * A customized text composable that displays a title and text in the same line.
 * The title is bold and followed by a colon and the text.
 *
 * @param title The bold title string.
 * @param text The normal text string.
 */
@Composable
fun CustomizedTitleText(title: String, text: String) {
    Row {
        Text(
            text = "$title:",
            style = appTextStyle().copy(fontWeight = FontWeight.Bold, fontSize = 20.sp),
            modifier = Modifier.alignByBaseline()
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = text,
            style = appTextStyle(),
            modifier = Modifier.alignByBaseline()
        )
    }
}


/**
 * A customized text composable that displays a name with a fixed width.
 * If the name is too long, it wraps into two lines instead of shifting UI elements.
 *
 * @param text The name to be displayed.
 */
@Composable
fun CustomizedTextHomeScreenTwoLinesDisplay(text: String) {
    val words = text.split(" ") // Split the name into words

    val firstLine = words.dropLast(1).joinToString(" ") // All words except the last
    val secondLine = words.last() // The last word

    Text(
        text = "$firstLine\n$secondLine",
        style = MaterialTheme.typography.bodyMedium,
        textAlign = TextAlign.Center,
        modifier = Modifier.width(100.dp)
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
        style = appTextStyle(),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        fontSize = 24.sp,
        textAlign = TextAlign.Center
    )
}

/**
 * Displays a subtitle aligned to the right with consistent styling and an underline.
 *
 * @param text The text to be displayed as the subtitle.
 */
@Composable
fun RightSubTitle(
    text: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.Start
    ) {
        Text(
            text = text,
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
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
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
 * A lightweight inline dropdown menu for use inside text lines.
 *
 * This composable allows embedding a clickable dropdown selector within a line of text,
 * making it ideal for sentences like: "I like [options] very much".
 *
 * @param options A list of strings to display as dropdown options.
 * @param selectedOption The currently selected option to display. If null, a placeholder will be shown.
 * @param onOptionSelected Callback invoked when the user selects an option from the dropdown.
 * @param textStyle Optional styling for the displayed text. Defaults to [LocalTextStyle] with primary color.
 */
@Composable
fun InlineDropdown(
    options: List<String>,
    selectedOption: String?,
    onOptionSelected: (String?) -> Unit,
    textStyle: TextStyle = LocalTextStyle.current.copy(color = MaterialTheme.colorScheme.primary)
) {
    var expanded by remember { mutableStateOf(false) }
    var selected by remember { mutableStateOf(selectedOption) }

    Box {
        Text(
            text = selected ?: HebrewText.CHOOSE,
            modifier = Modifier
                .clickable { expanded = true }
                .padding(horizontal = 4.dp)
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(4.dp)
                )
                .padding(horizontal = 6.dp, vertical = 2.dp),
            style = textStyle,
            textAlign = TextAlign.Center
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
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
 * Composable function that provides a search bar to filter members by name.
 *
 * @param members The list of FamilyMember objects to search through.
 * @param onSearchResults A callback function that returns the filtered list of members based on the search query.
 */
@RequiresApi(Build.VERSION_CODES.N)
@Composable
fun MembersSearchBar(
    members: List<FamilyMember>,
    onSearchResults: (List<FamilyMember>) -> Unit
) {
    var searchText by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        OutlinedTextField(
            value = searchText,
            onValueChange = {
                searchText = it
                val filteredMembers = if (searchText.isBlank()) {
                    members
                } else {
                    members.filter { member ->
                        member.getFullName().contains(searchText, ignoreCase = true)
                    }
                }
                onSearchResults(filteredMembers)
            },
            label = { CustomizedText(HebrewText.SEARCH_BY_NAME) },
            textStyle = appTextStyle().copy(
                textAlign = TextAlign.Right,
                textDirection = TextDirection.Rtl
            ),
            trailingIcon = {
                if (searchText.isNotEmpty()) {
                    IconButton(onClick = {
                        searchText = ""
                        onSearchResults(members)
                    }) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Clear"
                        )
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 2.dp)
        )
    }
}

/**
 * A search bar composable that allows users to search for family members by name and display their information.
 * It provides a dropdown menu with search results and shows a detailed information dialog when a member is selected.
 */
@RequiresApi(Build.VERSION_CODES.N)
@Composable
fun MembersSearchBar_2() {
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
            textStyle = appTextStyle().copy(
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

/**
 * A large circular button.
 *
 * @param onClick Lambda function to execute when the button is clicked.
 * @param text The label displayed on the button.
 */
@Composable
fun BigRoundButton(onClick: () -> Unit, text: String) {
    Button(
        onClick = onClick,
        shape = CircleShape,
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
        modifier = Modifier
            .size(150.dp)
    ) {
        Text(
            text = text,
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    }
}

/**
 * A circular button displaying an image.
 *
 * @param imageRes The resource ID of the image to be displayed.
 * @param onClick The callback function invoked when the button is clicked.
 */
@Composable
fun CircularImageButton(imageRes: Int, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(100.dp)
            .clip(CircleShape)
            .clickable(onClick = onClick)
    ) {
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = "Button Image",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    }
}

/**
 * A Composable function that displays a bidirectional arrow image as a clickable button.
 *
 * @param onClick The action to perform when the button is clicked.
 * @param modifier Optional [Modifier] to customize the appearance and behavior.
 */
@Composable
fun ArrowButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CustomizedText(HebrewText.FIND_CONNECTION)
        Image(
            painter = painterResource(id = R.drawable.bidirectional_arrow),
            contentDescription = "Bidirectional Arrow Button",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .clickable(onClick = onClick)
                .size(60.dp)
        )
    }
}

/**
 * A circular button displaying a question mark image.
 *
 * @param onClick The callback function invoked when the button is clicked.
 */
@Composable
fun QuestionMarkButton(onClick: () -> Unit) {
    CircularImageButton(imageRes = R.drawable.question_mark_button, onClick = onClick)
}

/**
 * A circular button displaying a jewish man image.
 *
 * @param onClick The callback function invoked when the button is clicked.
 */
@Composable
fun JewishManButton(onClick: () -> Unit) {
    CircularImageButton(imageRes = R.drawable.jewish_man_button_01, onClick = onClick)
}

/**
 * A circular button displaying a jewish woman image.
 *
 * @param onClick The callback function invoked when the button is clicked.
 */
@Composable
fun JewishWomanButton(onClick: () -> Unit) {
    CircularImageButton(imageRes = R.drawable.jewish_woman_button_01, onClick = onClick)
}

/**
 * A composable that displays a clickable rectangle representing a family member.
 *
 * @param member The family member to display.
 * @param isSelected Whether the rectangle is currently selected.
 * @param length The length of the rectangle (height).
 * @param width The width of the rectangle.
 * @param onClick Action to perform when the rectangle is clicked.
 */
@Composable
fun FamilyMemberCube(
    member: FamilyMember,
    isSelected: Boolean,
    length: Dp,
    width: Dp,
    onClick: () -> Unit
) {
    val lightGreen = Color(0xFF90EE90)
    val color = if (isSelected) lightGreen else Color.Gray

    Box(
        modifier = Modifier
            .size(width, length)
            .clip(RoundedCornerShape(8.dp)) // Rounded edges
            .background(color)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        androidx.compose.material.Text(
            text = member.getFullName(),
            textAlign = TextAlign.Center,
        )
    }
}

/**
 * A Composable function that displays a centered circular loading spinner.
 *
 * This is typically used to indicate that a loading process is occurring,
 * such as when downloading data or rendering content.
 *
 * @param modifier Optional [Modifier] for customizing layout behavior.
 */
@Composable
fun CenteredLoadingIndicator(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

/**
 * A Composable function that displays a zoomable image using SubsamplingScaleImageView,
 * with navigation buttons to jump to the left or right edges.
 *
 * @param imageFile The [File] containing the image to be displayed.
 */
@Composable
fun ZoomableFamilyTreeImage(imageFile: File) {
    var imageView by remember { mutableStateOf<SubsamplingScaleImageView?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { context ->
                SubsamplingScaleImageView(context).apply {
                    setImage(ImageSource.uri(Uri.fromFile(imageFile)))
                    setMinimumScaleType(SubsamplingScaleImageView.SCALE_TYPE_CENTER_CROP)
                    setPanLimit(SubsamplingScaleImageView.PAN_LIMIT_INSIDE)

                    setOnImageEventListener(object : SubsamplingScaleImageView.OnImageEventListener {
                        override fun onReady() {
                            val targetScale = height.toFloat() / sHeight
                            setScaleAndCenter(targetScale, PointF(sWidth / 2f, sHeight / 2f))
                            minScale = targetScale * 0.8f
                            maxScale = targetScale * 5f
                        }

                        override fun onImageLoaded() {}
                        override fun onImageLoadError(e: Exception) {}
                        override fun onPreviewLoadError(e: Exception) {}
                        override fun onTileLoadError(e: Exception) {}
                        override fun onPreviewReleased() {}
                    })

                    imageView = this
                }
            }
        )

        // Place each button in its corner with padding
        ScrollToStartButton(
            onClick = { imageView?.scrollToStart() },
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 12.dp, top = 12.dp)
                .offset(y = (-16).dp)
        )

        ScrollToEndButton(
            onClick = { imageView?.scrollToEnd() },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(end = 12.dp, top = 12.dp)
                .offset(y = (-16).dp)
        )

    }
}

/**
 * Scrolls the image to the left edge.
 */
private fun SubsamplingScaleImageView.scrollToStart() {
    if (isReady) {
        val scale = scale
        val centerY = sHeight / 2f
        setScaleAndCenter(scale, PointF(0f, centerY))
    }
}

/**
 * Scrolls the image to the right edge.
 */
private fun SubsamplingScaleImageView.scrollToEnd() {
    if (isReady) {
        val scale = scale
        val centerY = sHeight / 2f
        setScaleAndCenter(scale, PointF(sWidth.toFloat(), centerY))
    }
}

/**
 * A Composable function that displays a scroll-to-start button.
 *
 * This button shows a double left arrow icon and calls [onClick] when pressed.
 * Use it inside a Box and pass an aligned modifier (e.g., Modifier.align(Alignment.TopStart)).
 *
 * @param onClick Lambda to be invoked when the button is clicked.
 * @param modifier Modifier for layout positioning (e.g., alignment and padding).
 */
@Composable
private fun ScrollToStartButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    IconButton(
        onClick = onClick,
        modifier = modifier
    ) {
        Icon(
            imageVector = Icons.Default.KeyboardDoubleArrowLeft,
            contentDescription = "Scroll to start"
        )
    }
}

/**
 * A Composable function that displays a scroll-to-end button.
 *
 * This button shows a double right arrow icon and calls [onClick] when pressed.
 * Use it inside a Box and pass an aligned modifier (e.g., Modifier.align(Alignment.TopEnd)).
 *
 * @param onClick Lambda to be invoked when the button is clicked.
 * @param modifier Modifier for layout positioning (e.g., alignment and padding).
 */
@Composable
private fun ScrollToEndButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    IconButton(
        onClick = onClick,
        modifier = modifier
    ) {
        Icon(
            imageVector = Icons.Default.KeyboardDoubleArrowRight,
            contentDescription = "Scroll to end"
        )
    }
}
