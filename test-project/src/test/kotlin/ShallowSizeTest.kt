import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.nio.file.Files
import java.nio.file.Paths

data class TestClass(val a: Double, val b: Int)

class ShallowSizeTest {
    @Test
    fun `source files are generated`() {
        val generatedDirPath = Paths.get(System.getProperty("user.dir"), "build", "generated")
        assertTrue(Files.exists(Paths.get(generatedDirPath.toString(), "source", "kapt", "main", "TestClass_shallowSize.kt")))
    }

    @Test
    fun `shallowSize method exists`() {
        val t = TestClass(1.0, 1)
        t.shallowSize()
    }
}