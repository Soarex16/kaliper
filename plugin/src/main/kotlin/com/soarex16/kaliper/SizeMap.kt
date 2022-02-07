package com.soarex16.kaliper

import org.jetbrains.kotlin.js.descriptorUtils.getJetTypeFqName
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.isNullable

val primitiveTypesSizes = mapOf(
    "kotlin.Boolean" to 1,
    "kotlin.Char" to Char.SIZE_BYTES,
    "kotlin.Byte" to Byte.SIZE_BYTES,
    "kotlin.Short" to Short.SIZE_BYTES,
    "kotlin.Int" to Int.SIZE_BYTES,
    "kotlin.Long" to Long.SIZE_BYTES,
    "kotlin.UByte" to UByte.SIZE_BYTES,
    "kotlin.UShort" to UShort.SIZE_BYTES,
    "kotlin.UInt" to UInt.SIZE_BYTES,
    "kotlin.ULong" to ULong.SIZE_BYTES,
    "kotlin.Float" to Float.SIZE_BYTES,
    "kotlin.Double" to Double.SIZE_BYTES,
)

const val REFERENCE_TYPE_SIZE = 8

fun getSizeType(type: KotlinType?): Int = when {
    // val types = cls.value.getProperties().map { it.asmType() }
    type == null -> throw NullPointerException()
    type.isNullable() -> REFERENCE_TYPE_SIZE
    else -> primitiveTypesSizes.getOrDefault(type.getJetTypeFqName(false), REFERENCE_TYPE_SIZE)
}

