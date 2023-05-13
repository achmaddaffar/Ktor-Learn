package com.daffa.routes

import com.daffa.data.models.User
import com.daffa.data.requests.CreateAccountRequest
import com.daffa.data.responses.BasicApiResponse
import com.daffa.di.testModule
import com.daffa.plugins.configureSerialization
import com.daffa.repository.user.FakeUserRepository
import com.daffa.util.ApiResponseMessages
import com.google.common.truth.Truth
import com.google.common.truth.Truth.assertThat
import com.google.gson.Gson
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.testing.*
import kotlinx.coroutines.runBlocking
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.inject
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test


internal class CreateUserRoutesKtTest : KoinTest {

    private val userRepository by inject<FakeUserRepository>()

    private val gson = Gson()

    @BeforeTest
    fun setUp() {
        startKoin {
            modules(testModule)
        }
    }

    @AfterTest
    fun tearDown() {
        stopKoin()
    }

    @Suppress("DEPRECATION")
    @Test
    fun `Create user, no body attached, responds with BadRequest`() {
        withTestApplication(
            moduleFunction = {
                install(Routing) {
                    createUserRoutes(userRepository)
                }
            }
        ) {
            val request = handleRequest(
                method = HttpMethod.Post,
                uri = "/api/user/create"
            )

            assertThat(request.response.status()).isEqualTo(HttpStatusCode.BadRequest)
        }
    }

    @Suppress("DEPRECATION")
    @Test
    fun `Create user, user already exists, responds with unsuccessful`() = runBlocking {

        val user = User(
            email = "test@test.com",
            username = "test",
            password = "test",
            profileImageUrl = "",
            bio = "",
            gitHubUrl = "",
            instagramUrl = "",
            linkedInUrl = ""
        )
        userRepository.createUser(user)

        withTestApplication(
            moduleFunction = {
                configureSerialization()
                install(Routing) {
                    createUserRoutes(userRepository)
                }
            }
        ) {
            val request = handleRequest(
                method = HttpMethod.Post,
                uri = "/api/user/create"
            ) {
                addHeader("Content-Type", "application/json")
                val request = CreateAccountRequest(
                    email = "test@test.com",
                    username = "test",
                    password = "test"
                )
                setBody(gson.toJson(request))
            }

            val response = gson.fromJson<BasicApiResponse>(
                request.response.content ?: "",
                BasicApiResponse::class.java
            )
            assertThat(response.successful).isFalse()
            assertThat(response.message).isEqualTo(ApiResponseMessages.USER_ALREADY_EXISTS)
        }
    }

    @Suppress("DEPRECATION")
    @Test
    fun `Create user, email is empty, responds with unsuccessful`() = runBlocking {
        withTestApplication(
            moduleFunction = {
                configureSerialization()
                install(Routing) {
                    createUserRoutes(userRepository)
                }
            }
        ) {
            val request = handleRequest(
                method = HttpMethod.Post,
                uri = "/api/user/create"
            ) {
                addHeader("Content-Type", "application/json")
                val request = CreateAccountRequest(
                    email = "",
                    username = "test",
                    password = ""
                )
                setBody(gson.toJson(request))
            }

            val response = gson.fromJson<BasicApiResponse>(
                request.response.content ?: "",
                BasicApiResponse::class.java
            )
            assertThat(response.successful).isFalse()
            assertThat(response.message).isEqualTo(ApiResponseMessages.FIELDS_BLANK)
        }
    }

    @Suppress("DEPRECATION")
    @Test
    fun `Create user, valid data, responds with successful`() {
        withTestApplication(
            moduleFunction = {
                configureSerialization()
                install(Routing) {
                    createUserRoutes(userRepository)
                }
            }
        ) {
            val request = handleRequest(
                method = HttpMethod.Post,
                uri = "/api/user/create"
            ) {
                addHeader("Content-Type", "application/json")
                val request = CreateAccountRequest(
                    email = "test@test.com",
                    username = "test",
                    password = "test"
                )
                setBody(gson.toJson(request))
            }

            val response = gson.fromJson<BasicApiResponse>(
                request.response.content ?: "",
                BasicApiResponse::class.java
            )
            assertThat(response.successful).isTrue()
            runBlocking {
                val isUserInDb = userRepository.getUserByEmail("test@test.com") != null
                assertThat(isUserInDb).isTrue()
            }
        }
    }
}