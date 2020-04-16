package com.example.koroutines

import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.coroutines.reactive.flow.asFlow
import kotlinx.coroutines.runBlocking
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.boot.runApplication
import org.springframework.context.ApplicationListener
import org.springframework.context.support.beans
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.bodyFlowAndAwait
import org.springframework.web.reactive.function.server.coRouter
import java.util.*

@SpringBootApplication
class KoroutinesApplication

@FlowPreview
fun main(args: Array<String>) {
	runApplication<KoroutinesApplication>(*args) {

		val config = beans {
			bean {
				coRouter {
					val customers = ref<CustomerRepository>()
					GET("/customers"){
						ServerResponse.ok().bodyFlowAndAwait(customers.findAll().asFlow())
					}
				}
			}
			bean {
				ApplicationListener<ApplicationReadyEvent> {
					val customers = ref<CustomerRepository>()
					runBlocking {
						listOf("Gabriel", "Tammie", "Kimly", "Madhura", "Eddù", "Zhen", "Dave", "Stephane", "George")
								.map { Customer(UUID.randomUUID().toString(), it) }
								.map { customers.save(it).awaitFirst() }
								.forEach { println(it) }
					}
				}
			}
		}
		addInitializers(config)
	}
}


interface CustomerRepository : ReactiveCrudRepository<Customer, String>

data class Customer(val id: String, val name: String)