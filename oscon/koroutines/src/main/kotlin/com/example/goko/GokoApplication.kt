package com.example.goko

import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactive.awaitSingle
import kotlinx.coroutines.reactive.flow.asFlow
import kotlinx.coroutines.runBlocking
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.boot.runApplication
import org.springframework.context.ApplicationListener
import org.springframework.context.support.beans
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.bodyFlowAndAwait
import org.springframework.web.reactive.function.server.coRouter
import java.util.*


@SpringBootApplication
class GokuApplication

@FlowPreview
fun main(args: Array<String>) {

	runApplication<GokuApplication>(*args) {

		val conf = beans {
			bean {
				ApplicationListener<ApplicationReadyEvent> {
					runBlocking {
						println("application is ready...")
						val customers = ref<CustomerRepository>()
						customers.deleteAll().awaitFirstOrNull()
						arrayOf("Josh", "Bob", "Madhura", "Jane") //
								.map { Customer(UUID.randomUUID().toString(), it) }
								.map { customers.save(it).awaitSingle() } //
								.forEach { println("${Thread.currentThread().name}: $it") }
					}
				}
			}
			bean {
				val customers = ref<CustomerRepository>()
				coRouter {
					GET("/customers") {
						ServerResponse.ok().bodyFlowAndAwait(customers.findAll().asFlow())
					}
				}
			}
		}
		addInitializers(conf)
	}
}


interface CustomerRepository : ReactiveCrudRepository<Customer, String>

@Document
data class Customer(@Id val id: String, val name: String)
