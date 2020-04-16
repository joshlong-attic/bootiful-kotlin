package com.example.basics

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Profile
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.queryForObject
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

//
// https://github.com/JetBrains/Exposed/tree/master/exposed-spring-boot-starter
//
@SpringBootApplication
class BasicsApplication

fun main(args: Array<String>) {
	runApplication<BasicsApplication>(*args)
}


@Component
class Runner(private val customerService: CustomerService) : ApplicationRunner {

	override fun run(args: ApplicationArguments) {
		this.customerService
				.findAll()
				.forEach { println(this.customerService.findById(it.id)) }
	}
}

@Service
//@Profile("exposed")
@Transactional
class ExposedCustomerService : CustomerService {

	override fun findById(id: Long): Customer? =
			Customers
					.select { Customers.id.eq(id) }
					.map { Customer(it[Customers.id], it[Customers.name]) }
					.firstOrNull()

	override fun findAll(): Collection<Customer> =
			Customers
					.selectAll()
					.map { Customer(it[Customers.id], it[Customers.name]) }
}

object Customers : Table() {

	val id = long("id").autoIncrement()
	val name = varchar("name", 255)

	override val primaryKey = PrimaryKey(id)
}

@Service
@Profile("jdbc")
class JdbcCustomerService(private val template: JdbcTemplate) : CustomerService {

	override fun findById(id: Long): Customer? =
			this.template
					.queryForObject("select * from customers where id = ?", id) { resultSet, _ ->
						Customer(resultSet.getLong("id"), resultSet.getString("name"))
					}

	override fun findAll(): Collection<Customer> =
			this.template
					.query("select * from customers") { resultSet, _ ->
						Customer(resultSet.getLong("id"), resultSet.getString("name"))
					}
}


interface CustomerService {
	fun findById(id: Long): Customer?
	fun findAll(): Collection<Customer>
}

data class Customer(val id: Long, val name: String)

