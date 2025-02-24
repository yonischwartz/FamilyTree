package com.example.familytree.ui.theme

object HebrewText {

    // Errors
    const val ERROR_SAVING_MEMBERS_IN_FIREBASE = "שגיאה בשמירת עץ המשפחה ב-firebase"
    const val ERROR_LOADING_MEMBER_MAP = "שגיאה בטעינת בני משפחה"
    const val ERROR_ADDING_MEMBER = "שגיאה בהוספת בן משפחה"
    const val ERROR_REMOVING_MEMBER = "שגיאה בהסרת בן משפחה"
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

    // Other buttons
    const val YES = "כן"
    const val NO = "לא"
    const val REMOVE = "הסר"
    const val NEXT = "המשך"
    const val PREVIOUS = "הקודם"
    const val CLOSE = "סגור"
    const val CANCEL = "בטל"
    const val OK = "אישור"

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

    // Relations
    const val WIFE = "אשתו"
    const val HUSBAND = "בעלה"
    const val MALE_COUSIN = "בן דוד"
    const val FEMALE_COUSIN = "בת דודה"
    const val BROTHER = "אח"
    const val SISTER = "אחות"
    const val MARRIAGE = "נישואין"
    const val FATHER = "אבא"
    const val MOTHER = "אמא"
    const val SON = "בן"
    const val DAUGHTER = "בת"
    const val GRANDMOTHER = "סבתא"
    const val GRANDFATHER = "סבא"
    const val GRANDSON = "נכד"
    const val GRANDDAUGHTER = "נכדה"
    const val COUSIN = "בן דוד / בת דודה"
    const val SIBLING = "אח / אחות"

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

    const val BLANK = "______"
    const val CHOOSE = "בחר"
    const val HOW_IS_OTHER_MEMBER_RELATED_TO = "כיצד בן המשפחה הנוסף קשור ל"
    const val RABBIS_OR_STAFF_THAT_DID_NOT_LEARN_IN_YESHIVA = "רבנים או צוות הישיבה שלא למדו בישיבה"
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
    const val TO = "ל"
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
    const val AS = "כ"
    const val HE = "הוא"
    const val SHE = "היא"
    const val CHOOSE_FAMILY_MEMBER_TYPE = "בחר סוג בן משפחה"
    const val FAMILY_MEMBER_TYPE = "סוג בן משפחה"
    const val YESHIVA_FAMILY_MEMBER = "בן משפחה מהישיבה"
    const val NON_YESHIVA_FAMILY_MEMBER = "בן משפחה שאינו מהישיבה"
    const val ADD_FAMILY_MEMBER = "הוסף בן משפחה"
    const val IS_THIS_FAMILY_MEMBER_A_RABBI = "האם בן משפחה זה הוא רב?"
    const val NEW_FAMILY_MEMBERS_MUST_BE_RELATED_TO_AN_EXISTING_MEMBER = "בני משפחה חדשים נדרשים להיות קשורים לבן משפחה קיים בעץ. עליך לבחור בן משפחה שאליו קשור בן המשפחה שאתה רוצה להוסיף"


}