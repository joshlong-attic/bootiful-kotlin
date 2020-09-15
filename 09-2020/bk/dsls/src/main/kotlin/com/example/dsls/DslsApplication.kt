package com.example.dsls

import kotlinx.coroutines.reactive.awaitFirst
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder
import org.springframework.cloud.gateway.route.builder.filters
import org.springframework.cloud.gateway.route.builder.routes
import org.springframework.context.support.beans
import org.springframework.data.annotation.Id
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.body
import org.springframework.web.reactive.function.server.bodyValueAndAwait
import org.springframework.web.reactive.function.server.coRouter

@SpringBootApplication
class DslsApplication

fun main(args: Array<String>) {
  runApplication<DslsApplication>(*args) {

    addInitializers(beans {
      bean {
        val rlb = ref<RouteLocatorBuilder>()
        rlb
            .routes {
              route {
                path("/guides-proxy")
                filters {
                  setPath("/guides")
                }
                uri("https://spring.io")
              }
            }
      }
      bean {
        val cr = ref<CustomerRepository>()
        coRouter {

          GET("/customers/{id}") {
            val customer: Customer = cr
                .findById(Integer.parseInt(it.pathVariable("id")))
                .awaitFirst()
            ServerResponse.ok().bodyValueAndAwait( customer)
          }

        }
      }
    })
  }
}


data class Customer(@Id val id: Int, val name: String)

interface CustomerRepository : ReactiveCrudRepository<Customer, Int>