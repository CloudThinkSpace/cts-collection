package space.think.cloud.cts.lib.ui.utils

import java.util.regex.Pattern

object KeyboardTypeUtil {

    fun keepDigital(oldString: String, regex:String): String {
        val newString = StringBuffer()
        val matcher = Pattern.compile(regex).matcher(oldString)
        while (matcher.find()) {
            newString.append(matcher.group())
        }
        return newString.toString()
    }
}