package com.example.familytree.ui.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.familytree.data.dataManagement.DatabaseManager
import com.example.familytree.ui.HebrewText
import com.example.familytree.ui.appTextStyleBlack
import com.example.familytree.ui.buttonColor

@Composable
fun AdminPasswordDialog(
    onDismiss: () -> Unit,
    onPasswordCorrect: () -> Unit,
    onDemoPasswordCorrect: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var error by remember { mutableStateOf(false) }
    var loading by remember { mutableStateOf(false) }
    var isDemoMode by remember { mutableStateOf(false) }

    DialogWithButtons(
        title = HebrewText.ENTER_ADMIN_PASSWORD,
        textForLeftButton = HebrewText.OK,
        textForRightButton = if (isDemoMode) "realAdmin" else "demoAdmin",
        onRightButtonClick = {
            isDemoMode = !isDemoMode
            error = false
            password = ""
        },
        enabledForLeftButton = password.isNotBlank() && (!loading && (isDemoMode || email.isNotBlank())),
        onLeftButtonClick = {
            loading = true
            if (isDemoMode) {
                loading = false
                if (password == "demo") {
                    onDemoPasswordCorrect()
                } else {
                    error = true
                }
            } else {
                DatabaseManager.signIn(email, password) { success ->
                    loading = false
                    if (success) {
                        onPasswordCorrect()
                    } else {
                        error = true
                    }
                }
            }
        },
        contentOfDialog = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                if (!isDemoMode) {
                    OutlinedTextField(
                        value = email,
                        onValueChange = {
                            email = it
                            error = false
                        },
                        label = { Text(HebrewText.EMAIL) },
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

                OutlinedTextField(
                    value = password,
                    onValueChange = {
                        password = it
                        error = false
                    },
                    label = { Text(HebrewText.PASSWORD) },
                    isError = error,
                    visualTransformation = PasswordVisualTransformation(),
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

                if (error) {
                    Text(
                        text = HebrewText.WRONG_MAIL_OR_PASSWORD,
                        color = Color.Red,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        onDismiss = onDismiss
    )
}
