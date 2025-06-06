package com.yoniSchwartz.YBMTree.ui

import com.kosherjava.zmanim.hebrewcalendar.JewishDate
import java.util.*


private const val STARTING_MACHZOR_YEAR = 5737

/**
 * Returns the current Hebrew year using the JewishDate class.
 *
 * @return the current Hebrew year as an integer.
 */
fun getCurrentHebrewYear(): Int {
    val jewishDate = JewishDate(Date())  // Get current Jewish date
    return jewishDate.jewishYear  // Return the Hebrew year
}

/**
 * Generates a list of all machzorim (groups of individuals) up to the current Hebrew year.
 * The machzorim are represented by strings, with the first one being a special identifier for
 * rabbis or staff who did not learn in yeshiva, followed by a list of Hebrew letters and their
 * associated numbers (e.g., "א", "ב", "ג", ...).
 *
 * @return a list of strings representing machzorim up to the current Hebrew year.
 */
val allMachzorim: List<String> by lazy {
    val currentYear = getCurrentHebrewYear()  // Get current Hebrew year
    val machzorCount = currentYear - STARTING_MACHZOR_YEAR  // Calculate the number of machzorim
    val baseMachzorim = listOf(HebrewText.RABBIS_OR_STAFF_THAT_DID_NOT_LEARN_IN_YESHIVA) + listOf(
        "א", "ב", "ג", "ד", "ה", "ו", "ז", "ח", "ט", "י",
        "יא", "יב", "יג", "יד", "טו", "טז", "יז", "יח", "יט", "כ",
        "כא", "כב", "כג", "כד", "כה", "כו", "כז", "כח", "כט", "ל",
        "לא", "לב", "לג", "לד", "לה", "לו", "לז", "לח", "לט", "מ",
        "מא", "מב", "מג", "מד", "מה", "מו", "מז", "מח", "מט", "נ",
        "נא", "נב", "נג", "נד", "נה", "נו", "נז", "נח", "נט", "ס",
        "סא", "סב", "סג", "סד", "סה", "סו", "סז", "סח", "סט", "ע",
        "עא", "עב", "עג", "עד", "עה", "עו", "עז", "עח", "עט", "פ",
        "פא", "פב", "פג", "פד", "פה", "פו", "פז", "פח", "פט", "צ",
        "צא", "צב", "צג", "צד", "צה", "צו", "צז", "צח", "צט", "ק",
        "קא", "קב", "קג", "קד", "קה", "קו", "קז", "קח", "קט", "קי",
        "קיא", "קיב", "קיג", "קיד", "קטו", "קטז", "קיז", "קיח", "קיט", "קכ",
        "קכא", "קכב", "קכג", "קכד", "קכה", "קכו", "קכז", "קכח", "קכט", "קל",
        "קלא", "קלב", "קלג", "קלד", "קלה", "קלו", "קלז", "קלח", "קלט", "קמ",
        "קמא", "קמב", "קמג", "קמד", "קמה", "קמו", "קמז", "קמח", "קמט", "קנ",
        "קנא", "קנב", "קנג", "קנד", "קנה", "קנו", "קנז", "קנח", "קנט", "קס",
        "קסא", "קסב", "קסג", "קסד", "קסה", "קסו", "קסז", "קסח", "קסט", "קע",
        "קעא", "קעב", "קעג", "קעד", "קעה", "קעו", "קעז", "קעח", "קעט", "קפ",
        "קפא", "קפב", "קפג", "קפד", "קפה", "קפו", "קפז", "קפח", "קפט", "קצ",
        "קצא", "קצב", "קצג", "קצד", "קצה", "קצו", "קצז", "קצח", "קצט", "ר",
        "רא", "רב", "רג", "רד", "רה", "רו", "רז", "רח", "רט", "רי",
        "ריא", "ריב", "ריג", "ריד", "רטו", "רטז", "ריז", "ריח", "ריט", "רכ",
        "רכא", "רכב", "רכג", "רכד", "רכה", "רכו", "רכז", "רכח", "רכט", "רל",
        "רלא", "רלב", "רלג", "רלד", "רלה", "רלו", "רלז", "רלח", "רלט", "רמ",
        "רמא", "רמב", "רמג", "רמד", "רמה", "רמו", "רמז", "רמח", "רמט", "רנ",
        "רנא", "רנב", "רנג", "רנד", "רנה", "רנו", "רנז", "רנח", "רנט", "רס",
        "רסא", "רסב", "רסג", "רסד", "רסה", "רסו", "רסז", "רסח", "רסט", "רע",
        "רעא", "רעב", "רעג", "רעד", "רעה", "רעו", "רעז", "רעח", "רעט", "רפ",
        "רפא", "רפב", "רפג", "רפד", "רפה", "רפו", "רפז", "רפח", "רפט", "רצ",
        "רצא", "רצב", "רצג", "רצד", "רצה", "רצו", "רצז", "רצח", "רצט", "ש"
    ).take(machzorCount)  // Limit the list to the number of machzorim
    baseMachzorim  // Return the final list of machzorim
}

/**
 * Maps each machzor string to a unique integer index.
 * This map is generated based on the list of machzorim up to the current year.
 *
 * @return a map where the key is a machzor string and the value is its index.
 */
val machzorToInt: Map<String, Int> by lazy {
    allMachzorim.mapIndexed { index, machzor -> machzor to index }.toMap()  // Map each machzor to its index
}

/**
 * Maps each integer index to the corresponding machzor string.
 * This map is the inverse of the machzorToInt map.
 *
 * @return a map where the key is an integer index and the value is the corresponding machzor string.
 */
val intToMachzor: Map<Int, String> by lazy {
    machzorToInt.entries.associate { (key, value) -> value to key }  // Invert the machzorToInt map
}
