package com.github.trevorwhitney.prometheus

import io.micrometer.core.instrument.Meter
import io.micrometer.core.instrument.config.NamingConvention
import io.micrometer.core.lang.Nullable
import java.util.regex.Pattern

open class MetricNamingConvention() : NamingConvention {

    /**
     * Names are snake-cased. They contain a base unit suffix when applicable.
     *
     *
     * Names may contain ASCII letters and digits, as well as underscores and colons. They must match the regex
     * [a-zA-Z_:][a-zA-Z0-9_:]*
     */
    override fun name(name: String, type: Meter.Type, @Nullable baseUnit: String?): String {
        var conventionName = NamingConvention.snakeCase.name(name, type, baseUnit)

        when (type) {
            Meter.Type.COUNTER, Meter.Type.DISTRIBUTION_SUMMARY, Meter.Type.GAUGE -> if (baseUnit != null && !conventionName.endsWith("_$baseUnit"))
                conventionName += "_$baseUnit"
        }

        var sanitized = nameChars.matcher(conventionName).replaceAll("_")
        if (!Character.isLetter(sanitized[0])) {
            sanitized = "m_$sanitized"
        }
        return sanitized
    }

    /**
     * Label names may contain ASCII letters, numbers, as well as underscores. They must match the regex
     * [a-zA-Z_][a-zA-Z0-9_]*. Label names beginning with __ are reserved for internal use.
     */
    override fun tagKey(key: String): String {
        val conventionKey = NamingConvention.snakeCase.tagKey(key)

        var sanitized = tagKeyChars.matcher(conventionKey).replaceAll("_")
        if (!Character.isLetter(sanitized[0])) {
            sanitized = "m_$sanitized"
        }
        return sanitized
    }

    companion object {
        private val nameChars = Pattern.compile("[^a-zA-Z0-9_:]")
        private val tagKeyChars = Pattern.compile("[^a-zA-Z0-9_]")
    }
}
