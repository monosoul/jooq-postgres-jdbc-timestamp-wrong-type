package com.github.monosoul.extensions

import org.jooq.Explain
import strikt.api.DescribeableBuilder
import strikt.assertions.hasSize
import java.util.regex.MatchResult
import kotlin.streams.toList

private val SCAN_PATTERN = ".*Index Scan using partitioned_table_.*_pkey on partitioned_table_.*".toPattern()

fun DescribeableBuilder<Explain>.`has only one partition index scan`() = apply {
    get { plan().indexScans }.hasSize(1)
}

private val String.indexScans: List<String>
    get() = SCAN_PATTERN.matcher(this)
            .results()
            .map(MatchResult::group)
            .toList()