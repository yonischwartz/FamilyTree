package com.yoniSchwartz.YBMTree.ui.dialogs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.yoniSchwartz.YBMTree.ui.CustomizedText
import com.yoniSchwartz.YBMTree.ui.DialogBackgroundColor
import com.yoniSchwartz.YBMTree.ui.DialogTitle
import com.yoniSchwartz.YBMTree.ui.HebrewText
import com.yoniSchwartz.YBMTree.ui.DialogButton

/**
 * Displays a customizable dialog with up to two buttons (left and right).
 *
 * @param title The title of the dialog, displayed at the top center.
 * @param text The default text to display if no custom content is provided.
 * @param textForLeftButton The optional text for the left-side button.
 * @param onLeftButtonClick The optional action for the left-side button.
 * @param enabledForLeftButton Whether the left button is enabled (default is true).
 * @param textForRightButton The optional text for the right-side button.
 * @param onRightButtonClick The optional action for the right-side button.
 * @param enabledForRightButton Whether the right button is enabled (default is true).
 * @param contentOfDialog Optional custom composable content.
 * @param onDismiss Optional dialog dismiss handler. Defaults to right or left button action.
 */
@Composable
fun DialogWithButtons(
    title: String,
    text: String = "",
    textForLeftButton: String? = HebrewText.OK,
    onLeftButtonClick: (() -> Unit)? = null,
    enabledForLeftButton: Boolean = true,
    textForRightButton: String? = null,
    onRightButtonClick: (() -> Unit)? = null,
    enabledForRightButton: Boolean = true,
    onDismiss: (() -> Unit)? = null,
    contentOfDialog: @Composable (() -> Unit)? = null
) {
    val dismissAction = onDismiss ?: onLeftButtonClick ?: onRightButtonClick ?: {}

    Dialog(onDismissRequest = dismissAction) {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    tonalElevation = 4.dp,
                    color = DialogBackgroundColor
                ) {
                    BoxWithConstraints {
                        val maxHeight = this.maxHeight

                        Column(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth()
                                .heightIn(max = maxHeight * 0.85f),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f, fill = false)) {
                                Spacer(modifier = Modifier.height(16.dp))

                                DialogTitle(
                                    text = title,
                                    modifier = Modifier.align(Alignment.CenterHorizontally)
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                if (contentOfDialog != null) {
                                    Box(modifier = Modifier.fillMaxWidth()) {
                                        contentOfDialog()
                                    }
                                } else {
                                    CustomizedText(
                                        text = text,
                                        centered = true,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }

                                Spacer(modifier = Modifier.height(24.dp))
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                if (textForRightButton != null && onRightButtonClick != null) {
                                    DialogButton(
                                        text = textForRightButton,
                                        onClick = onRightButtonClick,
                                        enabled = enabledForRightButton
                                    )
                                } else {
                                    Spacer(modifier = Modifier.width(1.dp))
                                }

                                if (textForLeftButton != null && onLeftButtonClick != null) {
                                    DialogButton(
                                        text = textForLeftButton,
                                        onClick = onLeftButtonClick,
                                        enabled = enabledForLeftButton
                                    )
                                } else {
                                    Spacer(modifier = Modifier.width(1.dp))
                                }
                            }
                        }
                    }
                }

                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    tint = Color.Black,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .clickable { dismissAction() }
                        .padding(8.dp)
                        .size(24.dp)
                )
            }
        }
    }
}