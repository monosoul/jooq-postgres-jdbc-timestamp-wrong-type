package com.github.monosoul

import com.github.monosoul.PostgreSQLContainer.Companion.JDBC_LOG_PATH
import org.flywaydb.core.Flyway
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import java.io.File
import javax.sql.DataSource

abstract class TestBase {

    private lateinit var postgres: PostgreSQLContainer
    lateinit var datasource: DataSource
    lateinit var loggedDatasource: DataSource

    @BeforeEach
    internal fun baseSetUp() {
        File(JDBC_LOG_PATH).delete()
        postgres = PostgreSQLContainer().withLogging().also {
            it.start()
        }

        datasource = postgres.datasourceWithUrl { jdbcUrl }
        loggedDatasource = postgres.datasourceWithUrl { loggedJdbcUrl }

        runFlywayMigration()
    }

    @AfterEach
    internal fun tearDown() {
        postgres.stop()
    }

    private fun runFlywayMigration() {
        Flyway.configure()
                .dataSource(datasource)
                .locations("classpath:db/migration", "classpath:db/data")
                .load()
                .migrate()
    }

    fun readJdbcLog(): JdbcLogs = JdbcLogs(
            File(JDBC_LOG_PATH).readText()
    )
}