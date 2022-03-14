class OpaPartialEvalToGenericPolicyClauseConverter {
    val clauseRegex = Regex("""\w+\.\w+ = "\w+"""") // pet.owner = "alice"
    fun convert(opaPartialEvalResponse: String): Map<String, List<List<Pair<String, String>>>> {
        val fragments = opaPartialEvalResponse.split("\n\n").map { it.split("\n").map { it.filter { it != ';' } } }
        val andClauses = fragments.map {
            it.filter { clauseRegex.matches(it) }
                .map { it.split(".", " = ") }
                .map { it[0] to (it[1] to it[2].filter { it != '\"' }) }
                .groupBy { it.first }.mapValues { it.value.map { it.second } }
        }
        val orClauses = andClauses
            .flatMap { it.entries }
            .groupBy { it.key }
            .mapValues { entry -> entry.value.map { it.value } }
        return orClauses
    }
}
