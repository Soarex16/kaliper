package com.soarex16.kaliper

import org.jetbrains.kotlin.js.descriptorUtils.getJetTypeFqName
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.isNullable

val primitiveTypesSizes = mapOf(
    "kotlin.Boolean" to 1,
    "kotlin.Char" to 1,
    "kotlin.Byte" to 1,
    "kotlin.Short" to 2,
    "kotlin.Int" to 4,
    "kotlin.Long" to 8,
    "kotlin.Float" to 4,
    "kotlin.Double" to 8,
)

const val REFERENCE_TYPE_SIZE = 8

fun getSizeType(type: KotlinType?): Int = when {
    // val types = cls.value.getProperties().map { it.asmType() }
    type == null -> throw NullPointerException()
    type.isNullable() -> REFERENCE_TYPE_SIZE
    else -> primitiveTypesSizes.getOrDefault(type.getJetTypeFqName(false), REFERENCE_TYPE_SIZE)
}

