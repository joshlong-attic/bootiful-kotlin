package com.example.basics


import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.boot.runApplication
import org.springframework.context.ApplicationListener
import org.springframework.context.annotation.Bean
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.nativex.hint.TypeHint
import org.springframework.stereotype.Service
import java.util.*

/*
import org.jetbrains.exposed.sql.ExpressionWithColumnType
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.selectAll
import org.springframework.context.annotation.Profile
import org.springframework.nativex.hint.ProxyHint
import org.springframework.nativex.hint.TypeHint
import org.springframework.transaction.annotation.Transactional
import java.util.*


object Customers : Table("customer") {
    val id = integer("id").autoIncrement()
    val name = varchar("name", 255)
}

@Service
@Transactional
class ExposedCustomerService : CustomerService {

    override fun all() =
        Customers.selectAll()
            .map { Customer(it[Customers.id], it[Customers.name]) }
}
*/

@TypeHint (types = [
    Collections::class
])
/*@TypeHint(
    types = [
        Table::class,
        kotlin.jvm.functions.Function0::class,
        kotlin.jvm.functions.Function1::class,
        kotlin.jvm.functions.Function2::class,
        kotlin.jvm.functions.Function3::class,
        kotlin.jvm.functions.Function4::class,
        ExpressionWithColumnType::class,
        Collections::class,
        org.jetbrains.exposed.sql.ForeignKeyConstraint::class,
        org.jetbrains.exposed.sql.IColumnType::class,
        org.jetbrains.exposed.sql.DdlAware::class,
        org.jetbrains.exposed.sql.Expression::class,
        org.jetbrains.exposed.sql.Column::class,
    ]
)*/
@SpringBootApplication
class BasicsApplication {

    @Bean
    fun runner(cs: CustomerService) =
        ApplicationListener<ApplicationReadyEvent> {
            cs.all().forEach { println(it) }
        }
}

fun main(args: Array<String>) {
    runApplication<BasicsApplication>(*args)
}


@Service
class JdbcCustomerService(val jdbcTemplate: JdbcTemplate) : CustomerService {

	override fun all() =
		this.jdbcTemplate.query("select * from CUSTOMER") { rs, index ->
			Customer(rs.getInt("id"), rs.getString("name"))
		}
}


data class Customer(val id: Int, val name: String)

interface CustomerService {

    fun all(): Collection<Customer>
}
