package com.example.familytree.ui.pages

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.familytree.data.FamilyMember
import com.example.familytree.data.PositionedMember
import com.example.familytree.data.dataManagement.DatabaseManager
import com.example.familytree.ui.CenteredLoadingIndicator
import com.example.familytree.ui.FamilyTreeTopBar
import com.example.familytree.ui.FamilyTreeViewModel
import com.example.familytree.ui.HebrewText
import com.example.familytree.ui.ZoomableFamilyTreeImage
import com.example.familytree.ui.graphicTreeDisplay.FamilyTreeGraphicDisplay
import java.io.File


@RequiresApi(Build.VERSION_CODES.N)
@Composable
fun FamilyTreeGraphPage(navController: NavController, viewModel: FamilyTreeViewModel) {

    var isBackButtonEnabled by remember { mutableStateOf(true) }



    val members = DatabaseManager.getAllMembers()
    val rootId = members.firstOrNull()?.getId() ?: return // choose appropriate root member

    // Layout the family tree
    val positionedMembers = remember(members) {
        layoutFamilyTree(
            members = members,
            rootId = rootId,
            horizontalSpacing = 300f,
            verticalSpacing = 200f
        )
    }

//    // Display the family tree graphically
//    FamilyTreeGraphicDisplay(positionedMembers)

    // Get the image file from ViewModel
    val localImageFile = viewModel.imageFile.value

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White) // Force white background
    ) {

        FamilyTreeTopBar(
            text = HebrewText.FAMILY_TREE,
            onClickBack = {
                if (isBackButtonEnabled) {
                    isBackButtonEnabled = false
                    navController.popBackStack()
                }
            }
        )

        when {
            localImageFile != null -> {

                ZoomableFamilyTreeImage(localImageFile)
            }

            else -> {

                CenteredLoadingIndicator()
            }
        }
    }
}


/**
 * Lays out the family tree members in 2D space using a breadth-first traversal.
 *
 * This function assigns an (x, y) position to each family member based on their
 * distance from a root member and their order in traversal. The spacing between
 * nodes can be configured via [horizontalSpacing] and [verticalSpacing].
 *
 * @param members The list of all [FamilyMember] objects in the tree.
 * @param rootId The ID of the root member (e.g., the top ancestor).
 * @param horizontalSpacing The horizontal distance between nodes in the same level.
 * @param verticalSpacing The vertical distance between generations.
 * @return A list of [PositionedMember] representing the graphical layout.
 */
fun layoutFamilyTree(
    members: List<FamilyMember>,
    rootId: String,
    horizontalSpacing: Float = 300f,
    verticalSpacing: Float = 200f
): List<PositionedMember> {
    val memberMap = members.associateBy { it.getId() }
    val result = mutableListOf<PositionedMember>()
    val visited = mutableSetOf<String>()
    val queue = ArrayDeque<Triple<String, Int, Int>>() // (id, depth, positionIndex)

    queue.add(Triple(rootId, 0, 0))

    while (queue.isNotEmpty()) {
        val (id, depth, posIndex) = queue.removeFirst()
        if (id in visited) continue
        visited.add(id)

        val member = memberMap[id] ?: continue
        val x = posIndex * horizontalSpacing
        val y = depth * verticalSpacing
        result.add(PositionedMember(member, x, y))

        var childIndex = 0
        for (conn in member.getConnections()) {
            val targetId = conn.memberId
            if (targetId !in visited) {
                queue.add(Triple(targetId, depth + 1, posIndex + childIndex))
                childIndex++
            }
        }
    }

    return result
}
