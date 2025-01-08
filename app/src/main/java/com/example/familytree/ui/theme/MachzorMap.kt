package com.example.familytree.ui.theme

import com.kosherjava.zmanim.hebrewcalendar.JewishDate
import java.util.*

// Define the starting machzor year
private const val STARTING_MACHZOR_YEAR = 5737

// Function to get the current Hebrew year
fun getCurrentHebrewYear(): Int {
    val jewishDate = JewishDate(Date())
    return jewishDate.jewishYear
}

// Generate all machzorim up to the current year
val allMachzorim: List<String> by lazy {
    val currentYear = getCurrentHebrewYear()
    val machzorCount = currentYear - STARTING_MACHZOR_YEAR + 1
    val baseMachzorim = listOf("ללא מחזור") + listOf(
        "א", "ב", "ג", "ד", "ה", "ו", "ז", "ח", "ט", "י",
        "יא", "יב", "יג", "יד", "טו", "טז", "יז", "יח", "יט", "כ",
        "כא", "כב", "כג", "כד", "כה", "כו", "כז", "כח", "ל",
        "לא", "לב", "לג", "לד", "לה", "לו", "לז", "לח", "מ",
        "מא", "מב", "מג", "מד", "מה", "מו", "מז", "מח"
    ).take(machzorCount)
    baseMachzorim
}

val machzorToInt: Map<String, Int> by lazy {
    allMachzorim.mapIndexed { index, machzor -> machzor to index }.toMap()
}

val intToMachzor: Map<Int, String> by lazy {
    machzorToInt.entries.associate { (key, value) -> value to key }
}
