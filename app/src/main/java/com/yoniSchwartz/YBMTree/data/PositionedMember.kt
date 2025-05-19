package com.yoniSchwartz.YBMTree.data

/**
 * Represents a family member along with their position on the graphical family tree layout.
 *
 * This data class is used to store the layout coordinates for each family member node
 * after computing their positions in the visual graph. The `x` and `y` values represent
 * the pixel location where the member's box should be drawn on the screen.
 *
 * Being a data class, it provides useful functionality like `toString()`, `equals()`,
 * `hashCode()`, `copy()`, and destructuring support.
 *
 * @property member The [FamilyMember] associated with this node.
 * @property x The horizontal position (in pixels) of the member on the canvas.
 * @property y The vertical position (in pixels) of the member on the canvas.
 */
data class PositionedMember(
    val member: FamilyMember,
    val x: Float,
    val y: Float
)
