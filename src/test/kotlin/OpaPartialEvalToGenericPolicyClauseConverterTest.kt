import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class OpaPartialEvalToGenericPolicyClauseConverterTest {
    val converter = OpaPartialEvalToGenericPolicyClauseConverter()

    @Test
    fun `can parse single clause opa partial responses`() {
        val basicResponse = """
            # Query 1
            pet = data.pets[_];
            pet.owner = "alice"
        """.trimIndent()
        assertEquals(mapOf("pet" to listOf(listOf(("owner" to "alice")))), converter.convert(basicResponse))
    }

    @Test
    fun `can parse AND opa partial responses`() {
        val basicResponse = """
            # Query 1
            pet = data.pets[_];
            pet.owner = "alice";
            pet.name = "fluffy"
        """.trimIndent()
        assertEquals(mapOf("pet" to listOf(listOf(("owner" to "alice"), ("name" to "fluffy")))), converter.convert(basicResponse))
    }

    @Test
    fun `can parse OR opa partial responses`() {
        val basicResponse = """
            # Query 1
            pet = data.pets[_];
            pet.owner = "alice"
            
            # Query 2
            pet = data.pets[_];
            pet.veterinarian = "alice"
        """.trimIndent()
        assertEquals(mapOf("pet" to listOf(listOf("owner" to "alice"), listOf("veterinarian" to "alice"))), converter.convert(basicResponse))
    }
}
