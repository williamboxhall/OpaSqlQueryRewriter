import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class OpaSqlQueryRewriterTest {
    @Test
    fun `rewrite can insert sql fragments`() {
        val policyFragments = mapOf("users" to ("account" to "123"))
        val rewriter = OpaSqlQueryRewriter()
        assertEquals(
            "SELECT * FROM users WHERE users.account = '123'",
            rewriter.rewrite("select * from users", policyFragments)
        )
        assertEquals(
            "SELECT * FROM users AS u WHERE u.username = 'Will' AND u.account = '123'",
            rewriter.rewrite("select * from users as u where u.username = 'Will'", policyFragments)
        )
    }
}
