package com.bridgebase.bridgebase.data.user

import com.bridgebase.bridgebase.R
import com.bridgebase.bridgebase.domain.User
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import com.bridgebase.bridgebase.common.FirebaseResult

/**
 * Fake implementation of [UserRepository] used for template previews,
 * offline demos, and unit testing.
 *
 * Unlike the real repository (which may read from Firebase, REST APIs,
 * or local persistence), this fake version provides predictable, static
 * data wrapped in coroutine flows.
 *
 * This makes the UI fully functional without requiring network
 * configuration, backend services, or authentication.
 *
 * Why Fake Repositories?
 *  - Ensures the template runs out-of-the-box for buyers.
 *  - Enables rapid iteration and UI building.
 *  - Allows the real data layer to be added later without touching UI code.
 *  - Perfect for Upwork clients evaluating the template before wiring Firebase.
 *
 * Annotated with [javax.inject.Inject] so Hilt can provide this implementation wherever
 * [UserRepository] is requested during the initial template configuration.
 */
class FakeUserRepository @Inject constructor() : UserRepository {

    /**
     * Emits a static [com.bridgebase.bridgebase.domain.User] object after a short artificial delay,
     * simulating a lightweight network request.
     *
     * @return A cold [kotlinx.coroutines.flow.Flow] that emits a single [com.bridgebase.bridgebase.domain.User] value.
     */
    override fun getUser(): Flow<FirebaseResult<User>> = flow {
        emit(FirebaseResult.Loading)

        delay(500)  // simulate network latency

        try {
            val user = User(
                name = "",
                avatar = R.drawable.avatar,
                tipOfDay = "✨ Take one mindful breath before starting."
            )
            emit(FirebaseResult.Success(user))
        } catch (e: Exception) {
            emit(FirebaseResult.Error("Failed to load fake goals", e))
        }
    }
}