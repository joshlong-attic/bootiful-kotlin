package com.example.koroutines

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.data.annotation.Id
import org.springframework.data.r2dbc.core.DatabaseClient
import org.springframework.data.r2dbc.core.asType
import org.springframework.data.r2dbc.core.awaitOne
import org.springframework.stereotype.Repository
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.bodyValueAndAwait
import org.springframework.web.reactive.function.server.coRouter

@SpringBootApplication
class KoroutinesApplication {

  @Bean()
  fun routes(cr: CustomerRepository) =
      coRouter {
        GET("/customers/{id}") {
          val customer: Customer = cr.findOne(it.pathVariable("id"))
          ServerResponse.ok().bodyValueAndAwait(customer)
        }
      }
}

fun main(args: Array<String>) {
  runApplication<KoroutinesApplication>(*args)
}

data class Customer(@Id val id: Int, val name: String)

@Repository
class CustomerRepository(private val dbc: DatabaseClient) {


  suspend fun findOne(name: String): Customer =
      this.dbc
          .execute("select * from CUSTOMER where NAME = :name")
          .bind("name", name)
          .asType<Customer>()
          .fetch()
          .awaitOne()


}