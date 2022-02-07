package com.soarex16.kaliper

import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

const val TEST_PACKAGE = "test"
const val TEST_CLASS_NAME = "TestClass"
const val SHALLOW_SIZE_METHOD_NAME = "shallowSize"

class ShallowSizeTest {
    // if ctorParameters == null we don't check size by method invocation
    @ParameterizedTest
    @MethodSource("testData")
    fun testClassMeasure(className: String, source: SourceFile, expectedSize: Int, ctorParameters: List<Any>? = listOf(), ) {
        val result = KotlinCompilation().apply {
            sources = listOf(source)
            compilerPlugins = listOf(KaliperMetaPlugin())
            inheritClassPath = true
            messageOutputStream = System.out // see diagnostics in real time
        }.compile()

        assertEquals(KotlinCompilation.ExitCode.OK, result.exitCode)

        if (ctorParameters == null)
            return

        val fqKlassName = "${TEST_PACKAGE}.${className}"
        val fqExtensionKlassName = "${TEST_PACKAGE}.${className}_${SHALLOW_SIZE_METHOD_NAME}Kt"

        val klass = result.classLoader.loadClass(fqKlassName)
        val extensionKlass = result.classLoader.loadClass(fqExtensionKlassName)

        val klassInstance = klass
            .constructors
            .first() // Because we process only data classes, we can suppose that we have only one ctor
            .newInstance(*ctorParameters.toTypedArray())

        val shallowSizeMethod = extensionKlass.methods.find { it.name == SHALLOW_SIZE_METHOD_NAME }

        val actualSize = shallowSizeMethod?.invoke(null, klassInstance)
        assertEquals(expectedSize, actualSize)
    }

    companion object {
        @JvmStatic
        fun testData() = listOf(
            Arguments.of(
                "TestClass1",
                SourceFile.kotlin("$TEST_CLASS_NAME.kt", """
                    package $TEST_PACKAGE

                    data class TestClass1(val x: Int?)
                """.trimIndent()),
                REFERENCE_TYPE_SIZE,
                listOf(12)
            ),
            Arguments.of(
                "TestClass2",
                SourceFile.kotlin("$TEST_CLASS_NAME.kt", """
                    package $TEST_PACKAGE

                    data class TestClass2(val x: Boolean?)
                """.trimIndent()),
                REFERENCE_TYPE_SIZE,
                listOf(false)
            ),
            Arguments.of(
                "TestClass3",
                SourceFile.kotlin("$TEST_CLASS_NAME.kt", """
                    package $TEST_PACKAGE

                    class UserType                    
    
                    data class TestClass3(val t: UserType)
                """.trimIndent()),
                REFERENCE_TYPE_SIZE,
                null
            ),
            Arguments.of(
                "TestClass4",
                SourceFile.kotlin("$TEST_CLASS_NAME.kt", """
                    package $TEST_PACKAGE
    
                    data class TestClass4(val s: Set<Int>)
                """.trimIndent()),
                REFERENCE_TYPE_SIZE,
                null
            ),
            Arguments.of(
                "TestClass5",
                SourceFile.kotlin("$TEST_CLASS_NAME.kt", """
                    package $TEST_PACKAGE
                    
                    data class TestClass5(val a: Int, val b: Float)
                """.trimIndent()),
                Int.SIZE_BYTES + Float.SIZE_BYTES,
                listOf<Any>(1, 1.toFloat())
            ),
            Arguments.of(
                "TestClass6",
                SourceFile.kotlin("$TEST_CLASS_NAME.kt", """
                    package $TEST_PACKAGE
                    
                    data class TestClass6(val a: String, val b: Int)
                """.trimIndent()),
                REFERENCE_TYPE_SIZE + Int.SIZE_BYTES,
                listOf<Any>("abc", 1)
            ),
            Arguments.of(
                "TestClass7",
                SourceFile.kotlin("$TEST_CLASS_NAME.kt", """
                    package $TEST_PACKAGE
                    
                    data class TestClass7(val name: String) {
                        var age: Int = 0
                    }
                """.trimIndent()),
                REFERENCE_TYPE_SIZE + Int.SIZE_BYTES,
                listOf<Any>("abc")
            ),
            Arguments.of(
                "TestClass8",
                SourceFile.kotlin("$TEST_CLASS_NAME.kt", """
                    package $TEST_PACKAGE
                    
                    data class TestClass8(val x: Int) {
                            var mutableVar = 0
                            val immutableVar: Byte = 0
                            var withoutInitializer: Double = 0.0
                    }
                """.trimIndent()),
                Int.SIZE_BYTES + Int.SIZE_BYTES + Byte.SIZE_BYTES + Double.SIZE_BYTES,
                listOf<Any>(12)
            ),
            Arguments.of(
                "TestClass9",
                SourceFile.kotlin("$TEST_CLASS_NAME.kt", """
                    package $TEST_PACKAGE
                    
                    data class TestClass9(val x: Int) {
                            var customGetter = 0
                                get() {
                                    return (field * 2)
                                }
                    }
                """.trimIndent()),
                Int.SIZE_BYTES + Int.SIZE_BYTES,
                listOf<Any>(12)
            ),
            Arguments.of(
                "TestClass10",
                SourceFile.kotlin("$TEST_CLASS_NAME.kt", """
                    package $TEST_PACKAGE
                    
                    data class TestClass10(val x: Int) {
                            var customSetter = 0
                                set(value) {
                                    if (value >= 0)
                                        field = value
                                }
                    }
                """.trimIndent()),
                Int.SIZE_BYTES + Int.SIZE_BYTES,
                listOf<Any>(12)
            ),
            Arguments.of(
                "TestClass11",
                SourceFile.kotlin("$TEST_CLASS_NAME.kt", """
                    package $TEST_PACKAGE
                    
                    data class TestClass11(val x: Int) {
                            val customGetter2
                                get() = 42
                    }
                """.trimIndent()),
                Int.SIZE_BYTES,
                listOf<Any>(12)
            ),
            Arguments.of(
                "TestClass12",
                SourceFile.kotlin("$TEST_CLASS_NAME.kt", """
                    package $TEST_PACKAGE
                    
                    data class TestClass12(val x: Int) {
                            var mutableVar = 0

                            var virtualProp
                                get() = mutableVar * 2
                                set(value) {
                                    mutableVar = value
                                }
                    }
                """.trimIndent()),
                Int.SIZE_BYTES + Int.SIZE_BYTES,
                listOf<Any>(12)
            ),
            Arguments.of(
                "TestClass13",
                SourceFile.kotlin("$TEST_CLASS_NAME.kt", """
                    package $TEST_PACKAGE

                    data class TestClass13(val x: UByte)
                """.trimIndent()),
                UByte.SIZE_BYTES,
                listOf(12.toUByte())
            ),
        )
    }
}