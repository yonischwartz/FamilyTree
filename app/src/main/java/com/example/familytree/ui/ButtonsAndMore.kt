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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.familytree.R
import com.example.familytree.data.FamilyMember
import java.io.File
import android.net.Uri
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.offset
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardDoubleArrowLeft
import androidx.compose.material.icons.filled.KeyboardDoubleArrowRight
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextFieldDefaults
import com.davemorrissey.labs.subscaleview.ImageSource
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import com.example.familytree.data.Connection
import com.example.familytree.data.MemberType
import com.example.familytree.data.Relations
import com.example.familytree.data.dataManagement.DatabaseManager

/**
 * A constant color value representing a beige background color.
 */
val ScreenBackgroundColor = Color(0xFFF5F5DC) // Beige

/**
 * A constant color value representing the button color used throughout the app.
 */
val buttonColor = Color(0xFF00668B) // Custom Blue

/**
 * A constant color value representing the background color used in dialogs.
 */
val DialogBackgroundColor = Color(0xFFF2FBFF) // Very light blue

/**
 * Returns the default text style used in the app.
 *
 * This function provides a consistent typography style for text elements
 * using `MaterialTheme.typography.bodyMedium`.
 *
 * @return A [TextStyle] representing the app's default text appearance.
 */
@Composable
fun appTextStyle(): TextStyle {
    return MaterialTheme.typography.bodyMedium
}

/**
 * Returns the default text style with black text color.
 *
 * This function builds on [appTextStyle] and overrides the text color to black,
 * providing a consistent dark text appearance for use on light backgrounds.
 *
 * @return A [TextStyle] based on [appTextStyle] with black color.
 */
@Composable
fun appTextStyleBlack(): TextStyle {
    return appTextStyle().copy(color = Color.Black)
}

/**
 * Returns the default text style with white text color.
 *
 * This function builds on [appTextStyle] and overrides the text color to white,
 * providing a consistent light text appearance for use on dark backgrounds.
 *
 * @return A [TextStyle] based on [appTextStyle] with white color.
 */
@Composable
fun appTextStyleWhite(): TextStyle {
    return appTextStyle().copy(color = Color.White)
}

/**
 * Returns the default bold text style used in the app.
 *
 * This function provides a consistent bold typography style for text elements
 * using `MaterialTheme.typography.bodyMedium` with `FontWeight.Bold`.
 *
 * @return A [TextStyle] representing the app's bold default text appearance.
 */
@Composable
fun appTextStyleBold(): TextStyle {
    return appTextStyle().copy(fontWeight = FontWeight.Bold)
}

/**
 * Returns the default bold text style with black color.
 *
 * This function builds on [appTextStyleBold] and overrides the text color to black,
 * suitable for bold dark text on light backgrounds.
 *
 * @return A [TextStyle] based on [appTextStyleBold] with black color.
 */
@Composable
fun appTextStyleBoldBlack(): TextStyle {
    return appTextStyleBold().copy(color = Color.Black)
}

/**
 * Returns the default bold text style with white color.
 *
 * This function builds on [appTextStyleBold] and overrides the text color to white,
 * suitable for bold light text on dark backgrounds.
 *
 * @return A [TextStyle] based on [appTextStyleBold] with white color.
 */
@Composable
fun appTextStyleBoldWhite(): TextStyle {
    return appTextStyleBold().copy(color = Color.White)
}

/**
 * Returns the large text style used in the app.
 *
 * This function provides a consistent typography style for larger text elements
 * using `MaterialTheme.typography.bodyLarge`.
 *
 * @return A [TextStyle] representing the app's large text appearance.
 */
@Composable
fun appTextStyleLarge(): TextStyle {
    return MaterialTheme.typography.bodyLarge
}

/**
 * Returns the large text style with black text color.
 *
 * This function builds on [appTextStyleLarge] and overrides the text color to black,
 * suitable for prominent text on light backgrounds.
 *
 * @return A [TextStyle] based on [appTextStyleLarge] with black color.
 */
@Composable
fun appTextStyleLargeBlack(): TextStyle {
    return appTextStyleLarge().copy(color = Color.Black)
}

/**
 * Returns the large text style with white text color.
 *
 * This function builds on [appTextStyleLarge] and overrides the text color to white,
 * suitable for prominent text on dark backgrounds.
 *
 * @return A [TextStyle] based on [appTextStyleLarge] with white color.
 */
@Composable
fun appTextStyleLargeWhite(): TextStyle {
    return appTextStyleLarge().copy(color = Color.White)
}

/**
 * Returns the small headline text style used in the app.
 *
 * This function provides a consistent headline style using `MaterialTheme.typography.headlineSmall`,
 * typically for section titles or highlighted text.
 *
 * @return A [TextStyle] representing the app's small headline text appearance.
 */
@Composable
fun appHeadlineStyle(): TextStyle {
    return MaterialTheme.typography.headlineSmall
}

/**
 * Returns the small headline text style with black text color.
 *
 * This function builds on [appHeadlineStyle] and overrides the text color to black,
 * for use on light backgrounds.
 *
 * @return A [TextStyle] based on [appHeadlineStyle] with black color.
 */
@Composable
fun appHeadlineStyleBlack(): TextStyle {
    return appHeadlineStyle().copy(color = Color.Black)
}

/**
 * Returns the small headline text style with white text color.
 *
 * This function builds on [appHeadlineStyle] and overrides the text color to white,
 * for use on dark backgrounds.
 *
 * @return A [TextStyle] based on [appHeadlineStyle] with white color.
 */
@Composable
fun appHeadlineStyleWhite(): TextStyle {
    return appHeadlineStyle().copy(color = Color.White)
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
 * @param centered Whether the text should be centered horizontally.
 */
@Composable
fun CustomizedText(
    text: String,
    modifier: Modifier = Modifier,
    centered: Boolean = false
) {
    Text(
        text = text,
        style = appTextStyleBlack(),
        modifier = if (centered) modifier.fillMaxWidth() else modifier,
        textAlign = if (centered) TextAlign.Center else TextAlign.Start
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
            style = appTextStyleBlack().copy(fontWeight = FontWeight.Bold, fontSize = 20.sp),
            modifier = Modifier.alignByBaseline()
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = text,
            style = appTextStyleBlack(),
            modifier = Modifier.alignByBaseline()
        )
    }
}

/**
 * Displays a section listing family connections in a vertical layout.
 *
 * Each line consists of a Hebrew-labeled relationship (underlined and followed by a colon)
 * and the corresponding member's full name. The names are clickable, allowing users to
 * navigate to the selected member's information dialog.
 *
 * The list is sorted based on a predefined logical order (spouse, children, parents, etc.)
 * defined in [Relations.relationPriority].
 *
 * @param connections A list of [Connection] objects representing relationships for a specific family member.
 * @param onMemberClick A callback triggered when the user clicks a member's name.
 *                      This is typically used to display the clicked member's info in a new or updated dialog.
 */
@Composable
fun DisplayConnectionsForMembersInfoDialog(
    connections: List<Connection>,
    onMemberClick: (FamilyMember) -> Unit
) {
    val sortedConnections = connections.sortedWith(
        compareBy { Relations.relationPriority[it.relationship] ?: Int.MAX_VALUE }
    )

    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = "${HebrewText.FAMILY_CONNECTIONS}:",
            style = appTextStyleBlack().copy(fontSize = 20.sp, fontWeight = FontWeight.SemiBold),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        sortedConnections.forEach { connection ->
            val member = DatabaseManager.getMemberById(connection.memberId)!!
            val relationInHebrew = connection.relationship.toHebrew(member.getGender())
            val memberName = member.getFullName()

            Row(modifier = Modifier.padding(start = 32.dp)) {
                Text(
                    text = "$relationInHebrew:",
                    style = appTextStyleBlack().copy(
                        textDecoration = TextDecoration.Underline,
                    ),
                    modifier = Modifier.alignByBaseline()
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = memberName,
                    style = appTextStyleBlack().copy(
                        color = buttonColor
                    ),
                    modifier = Modifier
                        .alignByBaseline()
                        .clickable { onMemberClick(member) }
                )
            }
        }
    }
}

/**
 * A customized text composable that displays a relation and a name on the same line.
 * The relation is underlined and followed by a colon, the name follows.
 *
 * @param relation The underlined relation string (e.g. "Father").
 * @param name The name string (e.g. "Rabbi Levi").
 */
@Composable
fun DisplayRelationAndName(relation: String, name: String) {
    Row {
        Text(
            text = "$relation:",
            style = appTextStyleBlack().copy(
                textDecoration = TextDecoration.Underline
            ),
            modifier = Modifier.alignByBaseline()
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = name,
            style = appTextStyleBlack(),
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
        style = appTextStyleBlack(),
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
        style = appTextStyleBlack(),
        modifier = Modifier
            .fillMaxWidth(),
        fontSize = 24.sp,
        textAlign = TextAlign.Center,
        fontWeight = FontWeight.Bold
    )
}

/**
 * Displays a prominent page sub headline with centered text and padding.
 *
 * @param headline The text to be displayed as the page headline.
 */
@Composable
fun PageSubHeadLine(headline: String) {
    Text(
        text = headline,
        style = appTextStyleBlack(),
        modifier = Modifier
            .fillMaxWidth(),
        fontSize = 16.sp,
        textAlign = TextAlign.Center,
        fontWeight = FontWeight.Bold
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
            style = appTextStyleBlack().copy(
                textDecoration = TextDecoration.Underline
            ),
            fontSize = 20.sp,
            textAlign = TextAlign.End,
            modifier = Modifier.padding(end = 8.dp)
        )
    }
}

/**
 * A Composable function that represents a page button.
 *
 * @param onClick A lambda function to handle the action when the button is clicked.
 * @param text A text to display on the button.
 */
@Composable
fun ButtonForPage(
    onClick: () -> Unit,
    text: String,
    modifier: Modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp)
) {
    Button(
        modifier = modifier,
        onClick = onClick,
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = buttonColor
        )
    ) {
        Text(
            text = text,
            style = appTextStyleLargeWhite().copy(textAlign = TextAlign.Center)
        )
    }
}

/**
 * A generic small button for dialogs.
 *
 * @param text The text to display on the button.
 * @param onClick The action to perform when the button is clicked.
 * @param enabled Whether the button is enabled or disabled (default is true).
 * @param modifier The modifier to apply to the button (default is Modifier).
 */
@Composable
fun DialogButton(
    text: String,
    onClick: () -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = buttonColor,
            contentColor = Color.White,
            disabledContainerColor = Color.LightGray,
            disabledContentColor = Color.Gray
        )
    ) {
        Text(
            text = text,
            style = appTextStyle()
        )
    }
}

/**
 * A composable function that displays a centered title within a dialog.
 *
 * @param text The text to be displayed as the title.
 */
@Composable
fun DialogTitle(
    text: String,
    modifier: Modifier = Modifier.fillMaxWidth()
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = appHeadlineStyleBlack(),
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
    }
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
 * A checkbox input row with a label next to it (checkbox appears after the text).
 *
 * @param label The label shown next to the checkbox.
 * @param checked Whether the checkbox is currently checked.
 * @param onCheckedChange Callback invoked when the checkbox is toggled.
 */
@Composable
fun BooleanCheckboxInput(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = label,
            style = appTextStyleBlack()
        )
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = CheckboxDefaults.colors(
                checkedColor = buttonColor,
                uncheckedColor = buttonColor,
                checkmarkColor = Color.White
            )
        )
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
 * @param textStyle Optional styling for the displayed text. Defaults to black.
 */
@Composable
fun InlineDropdown(
    options: List<String>,
    selectedOption: String?,
    onOptionSelected: (String?) -> Unit,
    textStyle: TextStyle = LocalTextStyle.current.copy(color = Color.Black) // 🖤 selected text color
) {
    var expanded by remember { mutableStateOf(false) }
    var selected by remember { mutableStateOf(selectedOption) }

    Box {
        Text(
            text = selected ?: HebrewText.CHOOSE,
            modifier = Modifier
                .clickable { expanded = true }
                .background(DialogBackgroundColor, shape = RoundedCornerShape(4.dp))
                .border(
                    width = 1.dp,
                    color = buttonColor,
                    shape = RoundedCornerShape(4.dp)
                )
                .padding(horizontal = 6.dp, vertical = 2.dp),
            style = textStyle,
            textAlign = TextAlign.Center
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(DialogBackgroundColor)
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = option,
                            color = Color.Black
                        )
                    },
                    onClick = {
                        selected = option
                        onOptionSelected(option)
                        expanded = false
                    },
                    modifier = Modifier.background(DialogBackgroundColor)
                )
            }
        }
    }
}

/**
 * A reusable styled text field used for editing member details.
 *
 * This composable wraps an [OutlinedTextField] with consistent styling used across the app,
 * including a white background and custom outline color.
 *
 * @param text The label to display above the text field.
 * @param value The current value of the text field.
 * @param onValueChange Callback invoked when the user changes the input text.
 */
@Composable
fun TextFieldForMemberDetails(
    text: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(text) },
        singleLine = true,
        textStyle = appTextStyleBlack(),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = buttonColor,
            unfocusedBorderColor = buttonColor,
            disabledBorderColor = buttonColor,
            focusedLabelColor = buttonColor,
            unfocusedLabelColor = buttonColor,
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            disabledContainerColor = Color.White,
            cursorColor = buttonColor
        )
    )
}

/**
 * A customized styled text field for editing member details.
 *
 * This composable is used for member detail inputs, applying a white background,
 * custom blue outline, label color, and cursor color.
 *
 * @param label The label to display above the text field.
 * @param value The current value of the text field.
 * @param onValueChange Callback invoked when the user changes the text.
 */
@Composable
fun CustomizedTextFieldForEditingMembersDetails(
    label: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        singleLine = true,
        textStyle = appTextStyleBlack(),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = buttonColor,
            unfocusedBorderColor = buttonColor,
            disabledBorderColor = buttonColor,
            focusedLabelColor = buttonColor,
            unfocusedLabelColor = buttonColor,
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            disabledContainerColor = Color.White,
            cursorColor = buttonColor
        )
    )
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
        OutlinedTextField(
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
            singleLine = false,
            textStyle = appTextStyleBlack(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = buttonColor,
                unfocusedBorderColor = buttonColor,
                disabledBorderColor = buttonColor,
                focusedLabelColor = buttonColor,
                unfocusedLabelColor = buttonColor,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                disabledContainerColor = Color.White,
                cursorColor = buttonColor
            )
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
                            style = LocalTextStyle.current.copy(
                                textDirection = TextDirection.Rtl,
                                color = Color.Black
                            )
                        )
                    },
                    onClick = {
                        selected = option
                        onOptionSelected(option)
                        expanded = false
                    },
                    modifier = Modifier.background(DialogBackgroundColor)
                )
            }
        }
    }
}

/**
 * Provides a dropdown menu for selecting the machzor value.
 * The user can pick from a predefined set of options, and the selected option will be passed to the callback.
 *
 * @param machzor The currently selected machzor as a string.
 * @param onMachzorChange Callback to update the machzor selection. The selected machzor will be passed as a string.
 */
@Composable
fun MachzorInput(
    machzor: Int?,
    onMachzorChange: (Int?) -> Unit
) {
    TextFieldWithDropdownMenu(
        label = HebrewText.MACHZOR,
        options = allMachzorim,
        selectedOption = intToMachzor[machzor],
        onOptionSelected = { selectedOption ->
            val selectedMachzor = machzorToInt[selectedOption] ?: 0
            onMachzorChange(selectedMachzor)
        }
    )
}

/**
 * Dropdown input that allows the user to select a [MemberType] from the enum values.
 *
 * @param selectedType The currently selected member type.
 * @param onMemberTypeChange Callback invoked when the selected member type changes.
 */
@Composable
fun MemberTypeInput(
    selectedType: MemberType,
    onMemberTypeChange: (MemberType) -> Unit
) {
    val allTypes = MemberType.entries
    val selectedText = selectedType.name

    TextFieldWithDropdownMenu(
        label = HebrewText.MEMBER_TYPE,
        options = allTypes.map { it.name },
        selectedOption = selectedText,
        onOptionSelected = { name ->
            allTypes.find { it.name == name }?.let { onMemberTypeChange(it) }
        }
    )
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
 * Optionally displays a back arrow icon in the top right corner if [onClickBack] is provided,
 * and a save icon in the top left corner if [showSaveIcon] is true, with a save label underneath.
 *
 * @param text The title text to display in the top bar.
 * @param onClickBack Optional click handler for the back arrow icon.
 * @param showSaveIcon Whether to show the save icon.
 * @param onSave Click handler for the save icon.
 */
@Composable
fun FamilyTreeTopBar(
    text: String,
    onClickBack: (() -> Unit)? = null,
    showSaveIcon: Boolean = false,
    onSave: () -> Unit = {}
) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .background(buttonColor)
        ) {
            // Centered title
            Text(
                text = text,
                style = MaterialTheme.typography.titleLarge,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.Center)
            )

            // Back icon
            onClickBack?.let {
                IconButton(
                    onClick = it,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(start = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.ArrowForward,
                        contentDescription = "Back",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            // Save icon + "שמור"
            if (showSaveIcon) {
                Column(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 4.dp, end = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    IconButton(
                        onClick = onSave,
                        modifier = Modifier.size(36.dp) // smaller hit box
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Save,
                            contentDescription = "Save",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Text(
                        text = HebrewText.SAVE,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White,
                        fontSize = 10.sp,
                        lineHeight = 10.sp
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
    onSearchResults: (List<FamilyMember>) -> Unit,
    modifier: Modifier = Modifier.padding(16.dp)
) {
    var searchText by remember { mutableStateOf("") }

    Column(modifier = modifier) {
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
            textStyle = appTextStyleBlack().copy(
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
                .padding(horizontal = 2.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = buttonColor,
                unfocusedBorderColor = buttonColor,
                disabledBorderColor = buttonColor,
                focusedLabelColor = buttonColor,
                unfocusedLabelColor = buttonColor,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                disabledContainerColor = Color.White,
                cursorColor = buttonColor
            )
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
        colors = ButtonDefaults.buttonColors(containerColor = buttonColor),
        modifier = Modifier.size(150.dp)
    ) {
        Text(
            text = text,
            style = appTextStyleWhite().copy(
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
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
                .size(80.dp)
        )
    }
}

/**
 * A button that, when clicked, triggers the [onClick] action.
 * The button displays the text "מצא קשר" (from [HebrewText.FIND_CONNECTION])
 * on the right side in a single row, and a magnifying glass icon on the left side.
 *
 * @param modifier Modifier to apply to the button.
 * @param onClick Callback invoked when the button is clicked.
 * @param enabled Whether the button is enabled or not.
 */
@Composable
fun FindConnectionButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    enabled: Boolean = true
) {

    Button(
        onClick = onClick,
        modifier = modifier
            .height(50.dp)
            .width(120.dp),
        shape = RoundedCornerShape(8.dp),
        contentPadding = PaddingValues(4.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = buttonColor,
            contentColor = Color.White,
            disabledContainerColor = Color.LightGray,
            disabledContentColor = Color.Gray
        ),
        enabled = enabled
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = HebrewText.FIND_CONNECTION,
                style = MaterialTheme.typography.bodySmall,
                color = Color.White,
                fontSize = 15.sp,
                modifier = Modifier
                    .weight(1.2f)
                    .padding(start = 4.dp)
            )

            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = Color.White,
                modifier = Modifier
                    .weight(0.8f)
                    .size(30.dp)
                    .align(Alignment.CenterVertically)
            )
        }
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
 * If the rectangle is selected, it appears in light green; otherwise, it is gray.
 * A small info icon appears in the top-right corner and is independently clickable.
 *
 * @param member The family member to display.
 * @param isSelected Whether the rectangle is currently selected (affects background color).
 * @param length The height of the rectangle.
 * @param width The width of the rectangle.
 * @param onClick Action to perform when the rectangle itself is clicked.
 * @param onExclamationClick Action to perform when the info icon is clicked (default is no-op).
 */
@Composable
fun FamilyMemberCube(
    member: FamilyMember,
    isSelected: Boolean,
    length: Dp,
    width: Dp,
    onClick: () -> Unit,
    onExclamationClick: () -> Unit = {}
) {
    val lightGreen = Color(0xFF90EE90)
    val color = if (isSelected) lightGreen else Color.Gray

    val nameParts = member.getFullNameThatFitsTheCube().split(" ", limit = 2)
    val firstLine = nameParts.getOrNull(0) ?: ""
    val restOfName = nameParts.getOrNull(1) ?: ""

    Box(
        modifier = Modifier
            .size(width, length)
            .clip(RoundedCornerShape(8.dp))
            .background(color)
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = firstLine,
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    style = appTextStyleBlack()
                )

                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "Info Icon",
                    tint = Color.Black,
                    modifier = Modifier
                        .size(16.dp)
                        .clickable { onExclamationClick() }
                )
            }

            if (restOfName.isNotEmpty()) {
                Text(
                    text = restOfName,
                    maxLines = 2,
                    style = appTextStyleBlack()
                )
            }
        }
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

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
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

            ScrollToStartButton(
                onClick = { imageView?.scrollToStart() },
                modifier = Modifier
                    .align(Alignment.TopStart) // Always top-left due to Ltr
                    .padding(start = 12.dp, top = 12.dp)
                    .offset(y = (-16).dp)
            )

            ScrollToEndButton(
                onClick = { imageView?.scrollToEnd() },
                modifier = Modifier
                    .align(Alignment.TopEnd) // Always top-right due to Ltr
                    .padding(end = 12.dp, top = 12.dp)
                    .offset(y = (-16).dp)
            )
        }
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
            contentDescription = "Scroll to start",
            tint = Color.Black
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
            contentDescription = "Scroll to end",
            tint = Color.Black
        )
    }
}
