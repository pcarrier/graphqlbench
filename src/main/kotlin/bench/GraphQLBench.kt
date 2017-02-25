package bench

import graphql.GraphQL
import graphql.Scalars
import graphql.execution.Execution
import graphql.introspection.IntrospectionQuery
import graphql.parser.Parser
import graphql.schema.GraphQLFieldDefinition
import graphql.schema.GraphQLObjectType
import graphql.schema.GraphQLSchema
import graphql.validation.Validator
import org.openjdk.jmh.Main
import org.openjdk.jmh.annotations.Benchmark
import org.openjdk.jmh.annotations.BenchmarkMode
import org.openjdk.jmh.annotations.Fork
import org.openjdk.jmh.annotations.Mode
import org.openjdk.jmh.annotations.OutputTimeUnit
import org.openjdk.jmh.infra.Blackhole
import java.util.concurrent.TimeUnit

@Fork(3)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
open class GraphQLBench {
    @Benchmark fun ifull(bh: Blackhole) = bh.consume(gql.execute(iquery))
    @Benchmark fun i0parse(bh: Blackhole) = bh.consume(parser.parseDocument(iquery))
    @Benchmark fun i1validate(bh: Blackhole) = bh.consume(validator.validateDocument(schema, idoc))
    @Benchmark fun i2exec(bh: Blackhole) = bh.consume(exec.execute(schema, null, idoc, null, emptyMap()))

    @Benchmark fun sfull(bh: Blackhole) = bh.consume(gql.execute(squery))
    @Benchmark fun s0parse(bh: Blackhole) = bh.consume(parser.parseDocument(squery))
    @Benchmark fun s1validate(bh: Blackhole) = bh.consume(validator.validateDocument(schema, sdoc))
    @Benchmark fun s2exec(bh: Blackhole) = bh.consume(exec.execute(schema, null, sdoc, null, emptyMap()))

    companion object {
        val parser = Parser()
        val validator = Validator()
        val exec = Execution(null, null)

        val schema = GraphQLSchema.newSchema()
                .query(
                        GraphQLObjectType.newObject()
                                .name("Root")
                                .field(GraphQLFieldDefinition.newFieldDefinition()
                                        .type(Scalars.GraphQLString)
                                        .name("hello")
                                        .staticValue("world")
                                        .build())
                                .build())
                .build()
        val gql = GraphQL(schema)

        val iquery = IntrospectionQuery.INTROSPECTION_QUERY
        val idoc = parser.parseDocument(iquery)

        val squery = "{hello}"
        val sdoc = parser.parseDocument(squery)
    }
}

fun main(args: Array<String>) = Main.main(args)
