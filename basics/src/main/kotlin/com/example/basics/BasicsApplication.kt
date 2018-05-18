package com.example.basics

import org.jetbrains.exposed.spring.SpringTransactionManager
import org.jetbrains.exposed.sql.*
import org.springframework.beans.factory.InitializingBean
import org.springframework.boot.ApplicationRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.context.support.beans
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.support.TransactionTemplate

@SpringBootApplication
class BasicsApplication

fun main(args: Array<String>) {

	SpringApplicationBuilder()
			.sources(BasicsApplication::class.java)
			.initializers(beans {
				bean {
					SpringTransactionManager(ref())
				}
				bean {
					ApplicationRunner {

						val cs = ref<CustomerService>()

						arrayOf("Josh", "Christian", "Markus", "Manjari",
								"Billy", "Terri", "Ambili", "Chris")
								.map { Customer(name = it) }
								.forEach { cs.insert(it) }

						cs.all().forEach {
							println(cs.byId(it.id!!))
						}

					}
				}
			})
			.run(*args)
}

object Customers : Table() {
	val id = long("id").autoIncrement().primaryKey()
	val name = varchar("name", 255)
}

@Service
@Transactional
class ExposedCustomerService(private val tt: TransactionTemplate) : CustomerService, InitializingBean {

	override fun afterPropertiesSet() {
		tt.execute {
			SchemaUtils.create(Customers)
		}
	}

	override fun byId(id: Long): Customer? =
			Customers
					.select {
						Customers.id.eq(id)
					}
					.map {
						Customer(id = it[Customers.id], name = it[Customers.name])
					}
					.singleOrNull()

	override fun all(): Collection<Customer> =
			Customers
					.selectAll()
					.map {
						Customer(id = it[Customers.id], name = it[Customers.name])
					}


	override fun insert(c: Customer) {
		Customers.insert {
			it[Customers.name] = c.name!!
		}
	}
}

/*

@Service
class JdbcCustomerService(private val jdbcTemplate: JdbcTemplate) : CustomerService {

	override fun byId(id: Long): Customer? = this.jdbcTemplate.queryForObject(
			"SELECT * FROM CUSTOMERS WHERE ID = ? ", id) { rs, i ->
		Customer(rs.getLong("ID"), rs.getString("NAME"))
	}

	override fun all(): Collection<Customer> =
			this.jdbcTemplate.query("SELECT * FROM CUSTOMERS ") { rs, i ->
				Customer(rs.getLong("ID"), rs.getString("NAME"))
			}

	override fun insert(c: Customer) {
		this.jdbcTemplate.execute("INSERT INTO CUSTOMERS(NAME) VALUES (?)") {
			it.setString(1, c.name)
			it.execute()
		}
	}
}
*/


interface CustomerService {
	fun byId(id: Long): Customer?
	fun all(): Collection<Customer>
	fun insert(c: Customer)
}

data class Customer(val id: Long? = null, val name: String? = null)