package com.github.monosoul.jooq

import org.jooq.Binding
import org.jooq.BindingGetResultSetContext
import org.jooq.BindingGetSQLInputContext
import org.jooq.BindingGetStatementContext
import org.jooq.BindingRegisterContext
import org.jooq.BindingSQLContext
import org.jooq.BindingSetSQLOutputContext
import org.jooq.BindingSetStatementContext
import org.jooq.Converter
import org.jooq.conf.ParamType.INLINED
import org.jooq.impl.DSL
import java.sql.SQLFeatureNotSupportedException
import java.sql.Timestamp
import java.sql.Types.TIMESTAMP_WITH_TIMEZONE
import java.time.OffsetDateTime
import java.time.ZoneOffset.UTC

class OffsetDateTimeBinding : Binding<OffsetDateTime, OffsetDateTime> {
    override fun register(ctx: BindingRegisterContext<OffsetDateTime>) {
        ctx.statement().registerOutParameter(ctx.index(), TIMESTAMP_WITH_TIMEZONE)
    }

    override fun sql(ctx: BindingSQLContext<OffsetDateTime>) {
        if (ctx.render().paramType() == INLINED) {
            ctx.render().visit(DSL.inline(ctx.value())).sql("::timestamp with time zone")
        } else {
            ctx.render().sql("?")
        }
    }

    override fun converter(): Converter<OffsetDateTime, OffsetDateTime> = NoOpConverter

    override fun get(ctx: BindingGetResultSetContext<OffsetDateTime>) {
        ctx.value(ctx.resultSet().getTimestamp(ctx.index())?.toLocalDateTime()?.atOffset(UTC))
    }

    override fun get(ctx: BindingGetStatementContext<OffsetDateTime>) {
        ctx.value(ctx.statement().getTimestamp(ctx.index())?.toLocalDateTime()?.atOffset(UTC))
    }

    override fun get(ctx: BindingGetSQLInputContext<OffsetDateTime>) {
        throw SQLFeatureNotSupportedException()
    }

    override fun set(ctx: BindingSetStatementContext<OffsetDateTime>) {
        ctx.statement().setTimestamp(ctx.index(), ctx.value()?.toLocalDateTime()?.let(Timestamp::valueOf))
    }

    override fun set(ctx: BindingSetSQLOutputContext<OffsetDateTime>?) {
        throw SQLFeatureNotSupportedException()
    }
}

private object NoOpConverter : Converter<OffsetDateTime, OffsetDateTime> {
    override fun from(databaseObject: OffsetDateTime?) = databaseObject
    override fun to(userObject: OffsetDateTime?) = userObject
    override fun fromType() = OffsetDateTime::class.java
    override fun toType() = OffsetDateTime::class.java
}