package com.github.monosoul

import org.postgresql.ds.PGSimpleDataSource
import org.slf4j.LoggerFactory.getLogger
import org.testcontainers.containers.output.Slf4jLogConsumer
import javax.sql.DataSource
import org.testcontainers.containers.PostgreSQLContainer as PSQLContainer

class PostgreSQLContainer : PSQLContainer<PostgreSQLContainer>("postgres:12") {

    companion object {
        const val JDBC_LOG_PATH = "logs/pgjdbc.log"
    }

    val loggedJdbcUrl: String
        get() = "jdbc:postgresql://$host:${getMappedPort(POSTGRESQL_PORT)}/$databaseName" +
                "?loggerLevel=TRACE&loggerFile=$JDBC_LOG_PATH"

    fun datasourceWithUrl(urlSupplier: PostgreSQLContainer.() -> String): DataSource = PGSimpleDataSource().also {
        it.user = username
        it.password = password
        it.setUrl(this.urlSupplier())
    }

    fun withLogging() = apply {
        withLogConsumer(Slf4jLogConsumer(getLogger("DOCKER")))
    }
}