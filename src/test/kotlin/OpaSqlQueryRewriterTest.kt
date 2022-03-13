import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class OpaSqlQueryRewriterTest {
    private val rewriter = OpaSqlQueryRewriter()

    @Test
    fun `rewrite can insert sql fragments`() {
        val policyFragments = mapOf("users" to ("account" to "123"))

        assertEquals(
            "SELECT * FROM users WHERE users.account = '123'",
            rewriter.rewrite("select * from users", policyFragments)
        )
        assertEquals(
            "SELECT * FROM users AS u WHERE u.username = 'Will' AND u.account = '123'",
            rewriter.rewrite("select * from users as u where u.username = 'Will'", policyFragments)
        )
    }

    @Test
    fun `rewrite can handle nested queries`() {
        // inner select should be modified but isn't
        val nestedSelect = """
                    select a.impact_type, sum(a.rating)
                    from admin_report_impact_projection a
                    inner join(
                       select user_id, max(date_created)
                       from admin_report_impact_projection
                       where date_created < '2022-07-11'
                       group by user_id
                    ) b on a.user_id = b.user_id and a.date_created = b.max
                    group by a.impact_type
                    order by a.impact_type;
                    """
        assertEquals(
            "SELECT a.impact_type, sum(a.rating) FROM admin_report_impact_projection a INNER JOIN (SELECT user_id, max(date_created) FROM admin_report_impact_projection WHERE date_created < '2022-07-11' AND admin_report_impact_projection.account = '456' GROUP BY user_id) b ON a.user_id = b.user_id AND a.date_created = b.max WHERE a.account = '456' GROUP BY a.impact_type ORDER BY a.impact_type",
            rewriter.rewrite(nestedSelect, mapOf("admin_report_impact_projection" to ("account" to "456")))
        )
    }
}
