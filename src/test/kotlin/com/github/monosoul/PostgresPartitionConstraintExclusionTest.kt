package com.github.monosoul

import com.github.monosoul.extensions.`has only one partition index scan`
import com.github.monosoul.extensions.`should not contain timestamp passed as string`
import com.github.monosoul.extensions.andPrintIt
import com.github.monosoul.extensions.offsetDateTime
import com.github.monosoul.extensions.uuid
import com.github.monosoul.jooq.tables.PartitionedTable.PARTITIONED_TABLE
import org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric
import org.jooq.DSLContext
import org.jooq.SQLDialect.POSTGRES
import org.jooq.impl.DefaultDSLContext
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import strikt.api.expectThat

class PostgresPartitionConstraintExclusionTest : TestBase() {

    private lateinit var jooq: DSLContext

    @BeforeEach
    internal fun setUp() {
        jooq = DefaultDSLContext(loggedDatasource, POSTGRES)
    }

    @Test
    internal fun `update query with constraint exclusion should cause only 1 partition index scan`() {
        val explanation = jooq.explain(
                jooq.update(PARTITIONED_TABLE)
                        .set(PARTITIONED_TABLE.SOME_DATA, randomAlphanumeric(10))
                        .where(
                                PARTITIONED_TABLE.ID.eq("8b47dd65-edcd-4ed4-971e-375419261403".uuid)
                        )
                        .and(
                                PARTITIONED_TABLE.TIMESTAMP.between(
                                        "2020-10-05T00:00:00+00:00".offsetDateTime,
                                        "2020-10-28T23:59:59+00:00".offsetDateTime
                                )
                        )
        )

        val jdbcLogs = readJdbcLog().andPrintIt()

        expectThat(explanation).`has only one partition index scan`()
        expectThat(jdbcLogs).`should not contain timestamp passed as string`()
    }
}