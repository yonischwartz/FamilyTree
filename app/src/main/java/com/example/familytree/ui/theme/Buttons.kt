package com.example.familytree.ui.theme

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign

/**
 * A Composable function that represents an home screen button.
 *
 * @param onClick A lambda function to handle the action when the button is clicked.
 * @param text A text to display on the button.
 */
@Composable
internal fun WideBlueButton(onClick: () -> Unit, text: String) {
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
 */
@Composable
fun SmallDialogButton(text: String, onClick: () -> Unit) {
    Button(onClick = onClick) {
        Text(text)
    }
}
