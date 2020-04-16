package com.example.dsls

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder
import org.springframework.cloud.gateway.route.builder.filters
import org.springframework.cloud.gateway.route.builder.routes
import org.springframework.context.support.beans
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.body
import org.springframework.web.reactive.function.server.router

@SpringBootApplication
class DslsApplication

fun main(args: Array<String>) {

	runApplication<DslsApplication>(*args) {

		val config = beans {
			bean {
				router {
					val customers = ref<CustomerRepository>()
					GET("/customers") {
						ServerResponse.ok().body(customers.findAll())
					}
				}
			}
			bean {
				ref<RouteLocatorBuilder>().routes {
					route {
						host ("*.spring.io") and path("/proxy")
						filters {
							setPath("/guides")
						}
						uri ("https://spring.io")
					}
				}
			}
		}
		addInitializers(config)
	}
}

data class Customer(val id: String, val name: String)

interface CustomerRepository : ReactiveCrudRepository<Customer, String>