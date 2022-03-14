import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class OpaPartialEvalToGenericPolicyClauseConverterTest {
    val converter = OpaPartialEvalToGenericPolicyClauseConverter()

    @Test
    fun `can parse basic opa partial responses`() {
        val basicResponse = """
            # Query 1
            pet = data.pets[_];
            pet.owner = "alice"
        """.trimIndent()
        assertEquals(mapOf("pet" to ("owner" to "alice")), converter.convert(basicResponse))
    }
}
