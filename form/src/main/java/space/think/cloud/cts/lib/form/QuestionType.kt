package space.think.cloud.cts.lib.form

/**
 * ClassName: QuestionType
 * Description:
 * @date: 2022/10/16 23:13
 * @author: tanghy
 */
enum class QuestionType(val type: String) {
    TextType("TextType"),
    UserType("UserType"),
    IntegerType("IntegerType"),
    NumberType("NumberType"),
    LongitudeType("LongitudeType"),
    AddressType("AddressType"),
    LatitudeType("LatitudeType"),
    PasswordType("PasswordType"),
    SingleChoiceType("SingleChoiceType"),
    MoreChoiceType("MoreChoiceType"),
    CheckType("CheckType"),
    RadioType("RadioType"),
    SectionType("SectionType"),
    DateType("DateType"),
    EmailType("EmailType"),
    ImageType("ImageType"),
}