package com.example.familytree.ui.dialogs

import androidx.compose.runtime.Composable
import com.example.familytree.ui.HebrewText


/**
 * A dialog shown when the user enters the demo admin mode.
 *
 * This dialog informs the user that demo mode is functionally similar to regular admin mode,
 * but changes made during this session will not be saved. It includes a single confirmation button
 * to acknowledge and dismiss the message.
 *
 * @param onDismiss A callback invoked when the user acknowledges the dialog or dismisses it.
 */
@Composable
fun DemoAdminInfoDialog(
    onDismiss: () -> Unit
) {
    DialogWithButtons(
        title = "demoAdmin mode",
        text = HebrewText.DEMO_ADMIN_MODE_DESCRIPTION,
        onRightButtonClick = onDismiss,
        textForRightButton = HebrewText.GOT_IT,
        enabledForLeftButton = true,
        onDismiss = onDismiss
    )
}
