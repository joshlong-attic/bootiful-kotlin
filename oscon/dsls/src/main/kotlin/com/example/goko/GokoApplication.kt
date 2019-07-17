package com.example.goko

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder
import org.springframework.cloud.gateway.route.builder.routes
import org.springframework.context.support.beans
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.body
import org.springframework.web.reactive.function.server.router
import reactor.core.publisher.Flux

@SpringBootApplication
class GokuApplication

fun main(args: Array<String>) {
	runApplication<GokuApplication>(*args) {
		val conf = beans {
			bean {
				router {
					GET("/hello") {
						val greeting = Flux.just("Hello ${it.pathVariable("name")}!")
						ServerResponse.ok().body(greeting)
					}
				}
			}
			bean {
				ref<RouteLocatorBuilder>().routes {
					route {
						host("*.spring.io") and path("/proxy")
						uri("http://spring.io/guides")
					}
				}
			}
		}
		addInitializers(conf)
	}
}

