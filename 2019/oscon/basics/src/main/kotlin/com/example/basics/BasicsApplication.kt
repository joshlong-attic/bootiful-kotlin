package com.example.basics

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
import org.springframework.transaction.annotation.Transactional
import javax.sql.DataSource

@SpringBootApplication
class BasicsApplication {

	@Bean
	fun txm(ds: DataSource) = SpringTransactionManager(ds)

}

fun main(args: Array<String>) {
	runApplication<BasicsApplication>(*args)
}

@Service
@Profile("jdbc")
class JdbcCustomerService(private val jdbcTemplate: JdbcTemplate) : CustomerService {

	override fun all(): Collection<Customer> =
			jdbcTemplate.query("select * from customers") { rs, i ->
				Customer(rs.getLong("id"), rs.getString("name"))
			}
}


object Customers : Table() {
	val id = long("id").nullable()
	val name = varchar("name", 255)
}

@Service
@Transactional
class ExposedCustomerService : CustomerService {

	override fun all(): Collection<Customer> =
			Customers.selectAll().map { Customer(it[Customers.id]!!, it[Customers.name]) }
}

interface CustomerService {
	fun all(): Collection<Customer>
}


data class Customer(val id: Long, val name: String)

@Component
class Runner(private val customerService: CustomerService) {

	@EventListener(ApplicationReadyEvent::class)
	fun ready() {
		this.customerService
				.all()
				.forEach { println(it) }
	}

}