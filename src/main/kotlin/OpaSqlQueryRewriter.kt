import net.sf.jsqlparser.expression.StringValue
import net.sf.jsqlparser.expression.operators.conditional.AndExpression
import net.sf.jsqlparser.expression.operators.relational.EqualsTo
import net.sf.jsqlparser.parser.CCJSqlParserUtil
import net.sf.jsqlparser.schema.Column
import net.sf.jsqlparser.schema.Table
import net.sf.jsqlparser.statement.Block
import net.sf.jsqlparser.statement.Commit
import net.sf.jsqlparser.statement.CreateFunctionalStatement
import net.sf.jsqlparser.statement.DeclareStatement
import net.sf.jsqlparser.statement.DescribeStatement
import net.sf.jsqlparser.statement.ExplainStatement
import net.sf.jsqlparser.statement.IfElseStatement
import net.sf.jsqlparser.statement.PurgeStatement
import net.sf.jsqlparser.statement.ResetStatement
import net.sf.jsqlparser.statement.RollbackStatement
import net.sf.jsqlparser.statement.SavepointStatement
import net.sf.jsqlparser.statement.SetStatement
import net.sf.jsqlparser.statement.ShowColumnsStatement
import net.sf.jsqlparser.statement.ShowStatement
import net.sf.jsqlparser.statement.StatementVisitor
import net.sf.jsqlparser.statement.Statements
import net.sf.jsqlparser.statement.UseStatement
import net.sf.jsqlparser.statement.alter.Alter
import net.sf.jsqlparser.statement.alter.AlterSession
import net.sf.jsqlparser.statement.alter.AlterSystemStatement
import net.sf.jsqlparser.statement.alter.RenameTableStatement
import net.sf.jsqlparser.statement.alter.sequence.AlterSequence
import net.sf.jsqlparser.statement.comment.Comment
import net.sf.jsqlparser.statement.create.index.CreateIndex
import net.sf.jsqlparser.statement.create.schema.CreateSchema
import net.sf.jsqlparser.statement.create.sequence.CreateSequence
import net.sf.jsqlparser.statement.create.synonym.CreateSynonym
import net.sf.jsqlparser.statement.create.table.CreateTable
import net.sf.jsqlparser.statement.create.view.AlterView
import net.sf.jsqlparser.statement.create.view.CreateView
import net.sf.jsqlparser.statement.delete.Delete
import net.sf.jsqlparser.statement.drop.Drop
import net.sf.jsqlparser.statement.execute.Execute
import net.sf.jsqlparser.statement.grant.Grant
import net.sf.jsqlparser.statement.insert.Insert
import net.sf.jsqlparser.statement.merge.Merge
import net.sf.jsqlparser.statement.replace.Replace
import net.sf.jsqlparser.statement.select.PlainSelect
import net.sf.jsqlparser.statement.select.Select
import net.sf.jsqlparser.statement.select.SelectVisitor
import net.sf.jsqlparser.statement.select.SetOperationList
import net.sf.jsqlparser.statement.select.WithItem
import net.sf.jsqlparser.statement.show.ShowTablesStatement
import net.sf.jsqlparser.statement.truncate.Truncate
import net.sf.jsqlparser.statement.update.Update
import net.sf.jsqlparser.statement.upsert.Upsert
import net.sf.jsqlparser.statement.values.ValuesStatement

class OpaSqlQueryRewriter {
    fun rewrite(sql: String, tableToWhereClause: Map<String, Pair<String, String>>): String {
        val queryAst = CCJSqlParserUtil.parse(sql)
        queryAst.accept(object : NoopStatementVisitor {
            override fun visit(select: Select?) {
                select?.selectBody?.accept(object : SelectVisitor {
                    override fun visit(plainSelect: PlainSelect?) {
                        val table = plainSelect?.fromItem
                        val (column, value) = tableToWhereClause.getValue((table as Table).name)
                        val policyExpression = EqualsTo(Column(table, column), StringValue(value))
                        val where = if (plainSelect.where == null) {
                            policyExpression
                        } else {
                            AndExpression(plainSelect.where, policyExpression)
                        }
                        plainSelect.withWhere(where)
                        Unit
                    }

                    override fun visit(setOpList: SetOperationList?) = Unit
                    override fun visit(withItem: WithItem?) = Unit
                    override fun visit(aThis: ValuesStatement?) = Unit
                })
                return Unit
            }
        })
        return queryAst.toString()
    }
}

interface NoopStatementVisitor : StatementVisitor {
    override fun visit(savepointStatement: SavepointStatement?) = Unit

    override fun visit(rollbackStatement: RollbackStatement?) = Unit

    override fun visit(comment: Comment?) = Unit

    override fun visit(commit: Commit?) = Unit

    override fun visit(delete: Delete?) = Unit

    override fun visit(update: Update?) = Unit

    override fun visit(insert: Insert?) = Unit

    override fun visit(replace: Replace?) = Unit

    override fun visit(drop: Drop?) = Unit

    override fun visit(truncate: Truncate?) = Unit

    override fun visit(createIndex: CreateIndex?) = Unit

    override fun visit(aThis: CreateSchema?) = Unit

    override fun visit(createTable: CreateTable?) = Unit

    override fun visit(createView: CreateView?) = Unit

    override fun visit(alterView: AlterView?) = Unit

    override fun visit(alter: Alter?) = Unit

    override fun visit(stmts: Statements?) {
        return Unit
    }

    override fun visit(execute: Execute?) {
        return Unit
    }

    override fun visit(set: SetStatement?) {
        return Unit
    }

    override fun visit(reset: ResetStatement?) {
        return Unit
    }

    override fun visit(set: ShowColumnsStatement?) {
        return Unit
    }

    override fun visit(showTables: ShowTablesStatement?) {
        return Unit
    }

    override fun visit(merge: Merge?) {
        return Unit
    }

    override fun visit(select: Select?) {
        return Unit
    }

    override fun visit(upsert: Upsert?) {
        return Unit
    }

    override fun visit(use: UseStatement?) {
        return Unit
    }

    override fun visit(block: Block?) {
        return Unit
    }

    override fun visit(values: ValuesStatement?) {
        return Unit
    }

    override fun visit(describe: DescribeStatement?) {
        return Unit
    }

    override fun visit(aThis: ExplainStatement?) {
        return Unit
    }

    override fun visit(aThis: ShowStatement?) {
        return Unit
    }

    override fun visit(aThis: DeclareStatement?) {
        return Unit
    }

    override fun visit(grant: Grant?) {
        return Unit
    }

    override fun visit(createSequence: CreateSequence?) {
        return Unit
    }

    override fun visit(alterSequence: AlterSequence?) {
        return Unit
    }

    override fun visit(createFunctionalStatement: CreateFunctionalStatement?) {
        return Unit
    }

    override fun visit(createSynonym: CreateSynonym?) {
        return Unit
    }

    override fun visit(alterSession: AlterSession?) {
        return Unit
    }

    override fun visit(aThis: IfElseStatement?) {
        return Unit
    }

    override fun visit(renameTableStatement: RenameTableStatement?) {
        return Unit
    }

    override fun visit(purgeStatement: PurgeStatement?) {
        return Unit
    }

    override fun visit(alterSystemStatement: AlterSystemStatement?) {
        return Unit
    }
}
