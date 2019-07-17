package com.example.goko

import org.jetbrains.exposed.spring.SpringTransactionManager
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.selectAll
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Profile
import org.springframework.context.event.EventListener
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.support.TransactionTemplate
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import javax.sql.DataSource

fun main(args: Array<String>) {
	runApplication<GokoApplication>(*args)
}

@SpringBootApplication
class GokoApplication {

	@Bean
	fun exposedTransactionManager(ds: DataSource) = SpringTransactionManager(ds)

	@Bean
	fun transactionTemplate(txm: PlatformTransactionManager) = TransactionTemplate(txm)
}


object Customers : Table() {
	val id = long("id").autoIncrement().primaryKey()
	val name = varchar("name", 255)
}

@Profile("exposed")
@Service
@Transactional
class ExposedCustomerService : CustomerService {

	override fun all(): Collection<Customer> = Customers
			.selectAll()
			.map { Customer(id = it[Customers.id], name = it[Customers.name]) }
}


@RestController
class CustomerRestController(private val customerService: CustomerService) {

	@GetMapping("/customers")
	fun get() = this.customerService.all()

}

@Profile("jdbc")
@Service
class JdbcCustomerService(private val jdbcTemplate: JdbcTemplate) : CustomerService {

	override fun all(): Collection<Customer> = this.jdbcTemplate.query("select * from CUSTOMERS") { rs, _ ->
		Customer(id = rs.getLong("ID"), name = rs.getString("NAME"))
	}
}


interface CustomerService {
	fun all(): Collection<Customer>
}

data class Customer(val id: Long? = null, val name: String)