package com.example.familytree.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import com.example.familytree.data.FamilyMember
import com.example.familytree.ui.theme.dialogs.DetailsForNonYeshivaMemberDialog
import com.example.familytree.ui.theme.dialogs.DetailsForYeshivaMemberDialog

/**
 * Composable function that displays the appropriate detail dialog
 * based on whether the given family member is associated with a Yeshiva.
 *
 * - If the member has a Machzor value, a Yeshiva member detail dialog is shown.
 * - Otherwise, a Non-Yeshiva member detail dialog is displayed.
 *
 * @param member The family member whose details will be displayed.
 * @param onDismiss The action to perform when the dialog is dismissed.
 */
@Composable
fun ShowDetailsForMember(member: FamilyMember, onDismiss: () -> Unit) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        if (member.getMachzor() != null) {
            DetailsForYeshivaMemberDialog(member = member, onDismiss = onDismiss)
        } else {
            DetailsForNonYeshivaMemberDialog(member = member, onDismiss = onDismiss)
        }
    }
}
