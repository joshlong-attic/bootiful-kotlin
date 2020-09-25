package com.example.basics

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.selectAll
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.boot.runApplication
import org.springframework.context.ApplicationListener
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Profile
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@SpringBootApplication
class BasicsApplication {

  @Bean
  fun runner(cs: CustomerService) = ApplicationListener<ApplicationReadyEvent> {
    cs.all().forEach { println(it) }
  }
}

fun main(args: Array<String>) {
  runApplication<BasicsApplication>(*args)
}

object Customers : Table() {
  val id = integer("id").autoIncrement()
  val name = varchar("name", 255)
}

@Service
@Transactional
class ExposedCustomerService : CustomerService {
  
  override fun all() =
    Customers.selectAll().map { Customer(it[Customers.id], it[Customers.name]) }
}

@Service
@Profile("jdbc")
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