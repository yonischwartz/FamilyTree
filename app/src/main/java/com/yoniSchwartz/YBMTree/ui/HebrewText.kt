package com.yoniSchwartz.YBMTree.ui

object HebrewText {

    // Errors
    const val ERROR_SAVING_MEMBERS_IN_FIREBASE = "שגיאה בשמירת עץ המשפחה ב-firebase"
    const val ERROR_LOADING_MEMBER_MAP = "שגיאה בטעינת בני משפחה"
    const val ERROR_ADDING_MEMBER = "שגיאה בהוספת בן משפחה"
    const val ERROR_REMOVING_MEMBER = "שגיאה בהסרת בן משפחה"
    const val NO_MEMBERS_FOUND = "לא נמצאו בני משפחה"
    const val WITH_STRING = "עם המחרוזת"
    const val ERROR_CONNECTING_TO_FIREBASE = "שגיאה בגישה ל-firebase, אולי כדאי לבדוק חיבור לאינטרנט"

    // Successes
    const val SUCCESS_SAVING_MEMBERS_IN_FIREBASE = "עץ המשפחה נשמר בהצלחה ב-firebase"
    const val SUCCESS_LOADING_MEMBER_MAP = "בני משפחה נמשכו בהצלחה מה-firebase"
    const val SUCCESS_ADDING_MEMBER = "בן משפחה נוסף בהצלחה"
    const val SUCCESS_ADDING_CONNECTION = "קשר משפחתי נוסף בהצלחה"

    // Home screen buttons
    const val ADD_NEW_FAMILY_MEMBER = "הוסף בן משפחה חדש"
    const val ADD_CONNECTION_BETWEEN_TWO_EXISTING_MEMBERS = "קשר בין שני בני משפחה קיימים"
    const val FIND_CONNECTION_BETWEEN_TWO_MEMBERS = "מצא קשר בן שני בני משפחה"
    const val SHOW_ALL_FAMILY_MEMBERS = "הצג את כל בני המשפחה"
    const val LOAD_MEMBERS_FROM_FIREBASE = "משוך נתונים מה-firebase"
    const val SAVE_AND_UPDATE_MEMBERS_TO_FIREBASE = "שמור ועדכן שינויים ב-firebase"
    const val GO_INTO_ADMIN_MODE = "היכנס למצב מנהל"
    const val SHOW_FAMILY_TREE_GRAPH = "הצג את עץ המשפחה בגרף"
    const val CLICK_TO_FIND_CONNECTION = "לחץ למציאת קשר"

    // Other buttons
    const val YES = "כן"
    const val NO = "לא"
    const val SEARCH = "חפש"
    const val REMOVE_MEMBER = "הסר מועמד"
    const val SAVE = "שמור"
    const val EDIT = "ערוך"
    const val REMOVE = "הסר"
    const val NEXT = "המשך"
    const val PREVIOUS = "הקודם"
    const val CLOSE = "סגור"
    const val CANCEL = "בטל"
    const val OK = "אישור"
    const val FIND_CONNECTION = "מצא קשר"
    const val SHOW_REVERSE_CONNECTION = "הצג את הקשר הפוך"
    const val GOT_IT = "הבנתי"
    const val DISPLAY_CONNECTION_GRAPHICALLY = "הצג את הקשר בצורה גרפית"
    const val ADD_NEW_MEMBER_THAT_IS_RELATED_TO = "הוסף בן משפחה חדש שקשור ל"
    const val ADD_CONNECTION_BETWEEN_EXISTING_MEMBER_AND = "הוסף קשר בין בן משפחה קיים לבין "
    const val FIND_CONNECTION_BETWEEN = "מצא קשר בין"
    const val AND_ANOTHER_MEMBER = "לבין בן משפחה נוסף"
    const val RESET_SELECTION = "אפס בחירה"


    // Relations as a subjective
    const val WIFE_OF = "אשתו של"
    const val HUSBAND_OF = "בעלה של"
    const val FATHER_OF = "אבא של"
    const val MOTHER_OF = "אמא של"
    const val SON_OF = "בן של"
    const val DAUGHTER_OF = "בת של"
    const val GRANDMOTHER_OF = "סבתא של"
    const val GRANDFATHER_OF = "סבא של"
    const val GRANDSON_OF = "נכד של"
    const val GRANDDAUGHTER_OF = "נכדה של"
    const val MALE_COUSIN_OF = "בן דוד של"
    const val FEMALE_COUSIN_OF = "בת דודה של"
    const val BROTHER_OF = "אח של"
    const val SISTER_OF = "אחות של"
    const val GREAT_GRANDMOTHER_OF = "סבתא רבתא של"
    const val GREAT_GRANDFATHER_OF = "סבא רבא של"
    const val GREAT_GRANDSON_OF = "נין של"
    const val GREAT_GRANDDAUGHTER_OF = "נינה של"
    const val UNCLE_OF = "דוד של"
    const val AUNT_OF = "דודה של"
    const val NEPHEW_OF = "אחיין של"
    const val NIECE_OF = "אחיינית של"
    const val HALF_BROTHER_OF = "חצי אח של"
    const val HALF_SISTER_OF = "חצי אחות של"

    // Relations
    const val A_WIFE = "אישה"
    const val A_HUSBAND = "בעל"
    const val WIFE = "אשתו"
    const val SECOND_WIFE = "אשתו השנייה"
    const val THIRD_WIFE = "אשתו השלישית"
    const val FORTH_WIFE = "אשתו הרביעית"
    const val HUSBAND = "בעלה"
    const val SECOND_HUSBAND = "בעלה השני"
    const val THIRD_HUSBAND = "בעלה השלישי"
    const val FORTH_HUSBAND = "בעלה הרביעי"
    const val MALE_COUSIN = "בן דוד"
    const val FEMALE_COUSIN = "בת דודה"
    const val BROTHER = "אח"
    const val SISTER = "אחות"
    const val HALF_BROTHER = "חצי אח"
    const val HALF_SISTER = "חצי אחות"
    const val FATHER = "אבא"
    const val MOTHER = "אמא"
    const val SON = "בן"
    const val DAUGHTER = "בת"
    const val GRANDMOTHER = "סבתא"
    const val GRANDFATHER = "סבא"
    const val GRANDSON = "נכד"
    const val GRANDDAUGHTER = "נכדה"
    const val GREAT_GRANDMOTHER = "סבתא רבתא"
    const val GREAT_GRANDFATHER = "סבא רבא"
    const val GREAT_GRANDSON = "נין"
    const val GREAT_GRANDDAUGHTER = "נינה"
    const val UNCLE = "דוד"
    const val AUNT = "דודה"
    const val NEPHEW = "אחיין"
    const val NIECE = "אחיינית"

    // FamilyMember details
    const val FIRST_NAME = "שם פרטי"
    const val LAST_NAME = "שם משפחה"
    const val SEX = "מין"
    const val MALE = "זכר"
    const val FEMALE = "נקבה"
    const val MACHZOR = "מחזור"

    // Question words
    const val HOW = "כיצד"
    const val DOES = "האם"

    // One letter words
    const val THAT = "ש"
    const val TO = "ל"
    const val AS = "כ"
    const val AND = "ו"

    const val DEMO_ADMIN_MODE = "מצב מנהל דמה"
    const val DEMO_ADMIN_MODE_DESCRIPTION = "מצב זה זהה למצב המנהל הרגיל, פרט לכך ששינויים שתבצע לא יישמרו"
    const val TOTAL_AMOUNT_OF_YESHIVA_MEMBERS = "סה\"כ בני משפחה מהישיבה"
    const val FAMILY_CONNECTIONS = "קשרי משפחה"
    const val EMAIL = "מייל"
    const val WRONG_PASSWORD = "סיסמא שגויה"
    const val WRONG_MAIL_OR_PASSWORD = "מייל או סיסמא שגויים"
    const val PASSWORD = "סיסמא"
    const val ENTER_ADMIN_PASSWORD = "הזן סיסמא לכניסה למצב מנהל"
    const val EDIT_MEMBER = "ערוך בן משפחה"
    const val HE_IS_NOT_FROM_THE_YESHIVA = "אינו בוגר הישיבה"
    const val SHE_IS_NOT_FROM_THE_YESHIVA = "אינה מן הישיבה"
    const val NAME = "שם"
    const val FAMILY_TREE_MEMBERS = "בני המשפחה"
    const val CHOOSE_TWO_FAMILY_MEMBERS = "בחר שני בני משפחה"
    const val ADMIN_MODE = "מצב מנהל"
    const val RABBIS_AND_STAFF = "רבנים וצוות הישיבה"
    const val NON_YESHIVA_FAMILY_MEMBERS = "בני משפחה שאינם מהישיבה"
    const val FAMILY_MEMBERS_LIST = "רשימת בני המשפחה"
    const val SEARCH_BY_NAME = "חפש לפי שם"
    const val THE_CONNECTION_BETWEEN = "הקשר בין"
    const val NOT_ENOUGH_MEMBERS_IN_TREE = "אין מספיק בני משפחה בעץ"
    const val IN_ORDER_TO_FIND_CONNECTION_BETWEEN_MEMBERS_TREE_MUST_HAVE_AT_LEAST_TWO_MEMBERS = "כדי למצוא קשר בין בני משפחה, צריכים להיות בעץ לפחות שני בני משפחה"
    const val CHOOSE_TWO_MEMBERS_WHOM_YOU_WOULD_LIKE_TO_FIND_THEIR_CONNECTION = "בחר שני בני משפחה שתרצה למצוא קשר ביניהם"
    const val BLANK = "______"
    const val CHOOSE = "בחר"
    const val HOW_IS_OTHER_MEMBER_RELATED_TO = "כיצד בן המשפחה הנוסף קשור ל"
    const val RABBIS_OR_STAFF_THAT_DID_NOT_LEARN_IN_YESHIVA = "רבנים או צוות הישיבה שלא למדו בישיבה"
    const val STAFF = "צוות"
    const val ADDING_NEW_MEMBER = "הוספת בן משפחה חדש"
    const val DO_YOU_WANT_TO_ADD_ANYWAY = "האם תרצה להוסיף בן משפחה זה, בכל זאת?"
    const val MEMBER_ALREADY_EXISTS = "קיים כבר בן משפחה עם שם זה בעץ"
    const val WAS_ADDED_SUCCESSFULLY = "נוסף בהצלחה"
    const val THE = "ה"
    const val IS_THIS_RABBI_A_YESHIVA_RABBI = "האם הרב הזה הוא רב בישיבה?"
    const val ENTER_DETAILS_FOR = "מלא פרטים על"
    const val RELATED_TO_THE_OTHER_MEMBER = "קשור לבן המשפחה הנוסף?"
    const val OF = "של"
    const val THE_OTHER_MEMBER_IS = "בן / בת המשפחה הנוסף הוא / היא"
    const val FAMILY_TREE = "עץ משפחה"
    const val CHOOSE_FAMILY_MEMBER = "בחר בן משפחה"
    const val CHOOSE_RELATION = "בחר קשר משפחתי"
    const val FAMILY_MEMBER_LIST = "רשימת בני משפחה"
    const val UNKNOWN = "לא ידוע"
    const val RABBI = "הרב "
    const val RABBI_WIFE = "הרבנית "
    const val FAMILY_MEMBER_DETAILS = "פרטי בן משפחה"
    const val REMOVING_THIS_MEMBER_BRAKES_THE_TREE = "הסרת מועמד זה אינה חוקית, מפני שהיא מחלקת את עץ המשפחה למספר חלקים שונים"
    const val MARRIED_COUPLE_CAN_NOT_BE_OF_SAME_SEX = "זוג נשוי אינם יכולים להיות מאותו המין"
    const val EXISTS_ALREADY_FAMILY_RELATION_OF_TYPE = "קיים כבר קשר משפחתי מסוג "
    const val CAN_NOT_ADD = "לא ניתן להוסיף את "
    const val BECAUSE = "מכיוון ש"
    const val HE = "הוא"
    const val SHE = "היא"
    const val CHOOSE_FAMILY_MEMBER_TYPE = "בחר סוג בן משפחה"
    const val MEMBER_TYPE = "סוג בן משפחה"
    const val YESHIVA_FAMILY_MEMBER = "בן משפחה מהישיבה"
    const val NON_YESHIVA_FAMILY_MEMBER = "בן משפחה שאינו מהישיבה"
    const val ADD_FAMILY_MEMBER = "הוסף בן משפחה"
    const val IS_THIS_FAMILY_MEMBER_A_RABBI = "האם בן משפחה זה הוא רב?"
    const val NEW_FAMILY_MEMBERS_MUST_BE_RELATED_TO_AN_EXISTING_MEMBER = "בני משפחה חדשים נדרשים להיות קשורים לבן משפחה קיים בעץ. עליך לבחור בן משפחה שאליו קשור בן המשפחה שאתה רוצה להוסיף"


}