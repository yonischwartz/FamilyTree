package com.yoniSchwartz.YBMTree.ui.dialogs

import androidx.compose.runtime.Composable
import com.yoniSchwartz.YBMTree.ui.HebrewText


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
        title = HebrewText.DEMO_ADMIN_MODE,
        text = HebrewText.DEMO_ADMIN_MODE_DESCRIPTION,
        onLeftButtonClick = onDismiss,
        textForLeftButton = HebrewText.GOT_IT,
        onDismiss = onDismiss
    )
}
