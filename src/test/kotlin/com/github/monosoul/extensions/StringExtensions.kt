package com.github.monosoul.extensions

import java.time.OffsetDateTime
import java.util.UUID

val String.uuid: UUID
    get() = UUID.fromString(this)

val String.offsetDateTime: OffsetDateTime
    get() = OffsetDateTime.parse(this)