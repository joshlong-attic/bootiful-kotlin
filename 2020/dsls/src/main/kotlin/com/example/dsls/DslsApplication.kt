package com.example.dsls

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder
import org.springframework.cloud.gateway.route.builder.filters
import org.springframework.cloud.gateway.route.builder.routes
import org.springframework.context.annotation.Bean
import org.springframework.context.support.beans
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.http.HttpHeaders
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.body
import org.springframework.web.reactive.function.server.router

@SpringBootApplication
class DslsApplication {

	@Bean
	fun gateway(rlb: RouteLocatorBuilder) = rlb.routes {
		route {
			path("/proxy") and host("*.spring.io")
			filters {
				addResponseHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "*")
				removeRequestHeader( HttpHeaders.HOST)
			}
			uri("https://github.com/bootiful-podcast")
		}
	}

	@Bean
	fun http(rr: ReservationRepository) = router {
		GET("/reservations") {
			ServerResponse.ok().body(rr.findAll())
		}
	}
}

data class Reservation(val id: String, val name: String)

interface ReservationRepository : ReactiveCrudRepository<Reservation, String>

fun main(args: Array<String>) {
	runApplication<DslsApplication>(*args) {
		val context = beans {

		}
		addInitializers(context)
	}
}
