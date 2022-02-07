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
        assertEquals(actualSize, expectedSize)
    }

    companion object {
        @JvmStatic
        fun testData() = listOf(
            Arguments.of(
                "TestClass1",
                SourceFile.kotlin("TestClass1.kt", """
                    package $TEST_PACKAGE

                    data class TestClass1(val x: Int?)
                """.trimIndent()),
                8,
                listOf(12)
            ),
            Arguments.of(
                "TestClass2",
                SourceFile.kotlin("$TEST_CLASS_NAME.kt", """
                    package $TEST_PACKAGE

                    data class TestClass2(val x: Boolean?)
                """.trimIndent()),
                8,
                listOf(false)
            ),
            Arguments.of(
                "TestClass3",
                SourceFile.kotlin("$TEST_CLASS_NAME.kt", """
                    package $TEST_PACKAGE

                    class UserType                    
    
                    data class TestClass3(val t: UserType)
                """.trimIndent()),
                8,
                null
            ),
            Arguments.of(
                "TestClass4",
                SourceFile.kotlin("$TEST_CLASS_NAME.kt", """
                    package $TEST_PACKAGE
    
                    data class TestClass4(val s: Set<Int>)
                """.trimIndent()),
                    8,
                    null
            ),
            Arguments.of(
                "TestClass5",
                SourceFile.kotlin("$TEST_CLASS_NAME.kt", """
                    package $TEST_PACKAGE
                    
                    data class TestClass5(val x: Int) {
                            var mutableVar = 0
                            val immutableVar: Byte = 0
                            var withoutInitializer: Double = 0.0
                        
                            var customGetter = 0
                                get() {
                                    return (field * 2)
                                }
                        
                            var customSetter = 0
                                set(value) {
                                    if (value >= 0)
                                        field = value
                                }
                        
                            val customGetter2
                                get() = 42

                            var virtualProp
                                get() = mutableVar * 2
                                set(value) {
                                    mutableVar = value
                                }
                    }
                """.trimIndent()),
                4 + 4 + 1 + 8 + 4 + 4,
                listOf<Any>(12)
            ),
            Arguments.of(
                "TestClass6",
                SourceFile.kotlin("$TEST_CLASS_NAME.kt", """
                    package $TEST_PACKAGE
                    
                    data class TestClass6(val a: Int, val b: Float)
                """.trimIndent()),
                4 + 4,
                listOf<Any>(1, 1.toFloat())
            ),
            Arguments.of(
                "TestClass7",
                SourceFile.kotlin("$TEST_CLASS_NAME.kt", """
                    package $TEST_PACKAGE
                    
                    data class TestClass7(val a: String, val b: Int)
                """.trimIndent()),
                8 + 4,
                listOf<Any>("abc", 1)
            ),
            Arguments.of(
                "TestClass8",
                SourceFile.kotlin("$TEST_CLASS_NAME.kt", """
                    package $TEST_PACKAGE
                    
                    data class TestClass8(val name: String) {
                        var age: Int = 0
                    }
                """.trimIndent()),
                8 + 4,
                listOf<Any>("abc")
            ),
        )
    }
}