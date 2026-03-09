package com.bridgebase.bridgebase.common.enums

/**
 * Enum class defining standardized authentication error types.
 *
 * Each constant maps to a descriptive [status] message displayed to the user.
 * This helps maintain consistency across UI components that report login or
 * signup issues.
 */
enum class ErrorType(val status: String) {

    /** Account not found for provided credentials */
    NO_ACCOUNT("Account doesn't exist"),

    /** Invalid password error */
    INVALID_PASSWORD("Invalid password"),

    /** Generic login error */
    LOGIN_ERROR("Unable to login"),

    /** Weak password error */
    WEAK_PASSWORD("Password is too weak"),

    /** Invalid email format error */
    INVALID_EMAIL("Invalid email"),

    /** Account already exists */
    ACCOUNT_EXISTS("Account already exists."),

    /** Generic signup error */
    SIGNUP_ERROR("Unable to signup"),

    /** Rate limit exceeded or unusual activity detected */
    TOO_MANY_REQUESTS(
        "We have blocked all requests from this device due to unusual activity"
    ),

    /** Backend service temporarily unavailable */
    SERVICE_DOWN("There was a problem signing in. Please try again later.")
}