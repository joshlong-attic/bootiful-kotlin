package com.example.dsls

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.runBlocking
import org.springframework.boot.ApplicationRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder
import org.springframework.cloud.gateway.route.builder.filters
import org.springframework.cloud.gateway.route.builder.routes
import org.springframework.context.support.beans
import org.springframework.data.mongodb.core.ReactiveFluentMongoOperations
import org.springframework.data.mongodb.core.insert
import org.springframework.data.mongodb.core.oneAndAwait
import org.springframework.data.mongodb.core.query
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Repository
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.bodyAndAwait
import org.springframework.web.reactive.function.server.coRouter
import java.util.*

@SpringBootApplication
class DslsApplication

fun main(args: Array<String>) {

	runApplication<DslsApplication>(*args) {
		val beans = beans {
			bean {
				val rlb = ref<RouteLocatorBuilder>()
				rlb.routes {
					route {
						path("/proxy")
						filters {
							setPath("/guides")
							addResponseHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "*")
						}
						uri("https://spring.io")
					}
				}
			}
			bean {
				val repository = ref<ReservationRepository>()
				ApplicationRunner {
					runBlocking {
						val insertedRecord = repository.insert(Reservation(UUID.randomUUID().toString(), "Jane"))
						println(insertedRecord.name)
						println(insertedRecord.id)
					}
				}
			}
			bean {
				val repo = ref<ReservationRepository>()
				coRouter {
					GET("/reservations") {
						ServerResponse.ok().bodyAndAwait(repo.all())
					}
				}
			}
		}
		addInitializers(beans)
	}
}


data class Reservation(val id: String, val name: String)

@Repository
class ReactiveReservationRepository(private val ops: ReactiveFluentMongoOperations) : ReservationRepository {

	override suspend fun insert(r: Reservation) = ops.insert<Reservation>().oneAndAwait(r)

	override suspend fun all(): Flow<Reservation> = this.ops.query<Reservation>().all().asFlow()
}

interface ReservationRepository {

	suspend fun insert(r: Reservation): Reservation

	suspend fun all(): Flow<Reservation>
}

