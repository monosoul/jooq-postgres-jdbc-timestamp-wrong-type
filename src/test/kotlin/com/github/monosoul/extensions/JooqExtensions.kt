package com.github.monosoul.extensions

import org.jooq.DSLContext
import org.jooq.Explain
import org.jooq.Query

fun Query.print() = also {
    println("Parameterized query:\n${it.sql}\n")
    println("Inline query:\n$it\n")
}

fun Explain.print() = also {
    print(it.plan())
}

fun DSLContext.explainAndPrint(query: Query) = explain(
        query.print()
).print()