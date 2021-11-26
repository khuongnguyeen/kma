package io.kma.results.readercccd.extension

import android.provider.ContactsContract
import dev.turingcomplete.kotlinonetimepassword.HmacAlgorithm
import org.apache.commons.codec.binary.Base32
import java.util.*


fun String.toCountryEmoji(): String {
    if (this.length != 2) {
        return ""
    }

    val countryCodeCaps = toUpperCase(Locale.US)
    val firstLetter = Character.codePointAt(countryCodeCaps, 0) - 0x41 + 0x1F1E6
    val secondLetter = Character.codePointAt(countryCodeCaps, 1) - 0x41 + 0x1F1E6

    if (!countryCodeCaps[0].isLetter() || !countryCodeCaps[1].isLetter()) {
        return this
    }

    return String(Character.toChars(firstLetter)) + String(Character.toChars(secondLetter))
}


