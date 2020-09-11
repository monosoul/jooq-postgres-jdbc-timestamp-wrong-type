package com.github.monosoul.extensions

import com.github.monosoul.JdbcLogs
import strikt.api.DescribeableBuilder
import strikt.assertions.isFalse

private val LOG_PATTERN = ".*FINEST:.*Bind\\(.*\\$3.*,type=VARCHAR,\\$4.*,type=VARCHAR.*".toPattern()

fun JdbcLogs.andPrintIt() = also {
    println(it.value)
}

fun DescribeableBuilder<JdbcLogs>.`should not contain timestamp passed as string`() = apply {
    get { value.bindsTimestampAsString }.isFalse()
}

private val String.bindsTimestampAsString: Boolean
    get() = LOG_PATTERN.matcher(this).find()