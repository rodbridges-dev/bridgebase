package com.bridgebase.bridgebase.domain

/**
 * Aggregated goal metrics used to populate the home dashboard.
 *
 * This model surfaces a lightweight summary of the user's goals,
 * allowing the UI to display progress at a glance without loading
 * full goal records. It keeps the home screen fast, responsive,
 * and independent of backend or database specifics.
 *
 * Fields:
 * @param inProgress   Number of goals the user is actively working on.
 * @param completed    Number of goals fully completed.
 * @param upcoming     Number of goals scheduled or planned for the near future.
 *
 * Why a summary model?
 *  - Reduces the amount of data fetched for the home screen.
 *  - Keeps ViewModel state compact and easy to reason about.
 *  - Allows fake/demo implementations to plug in simple values.
 *  - Real implementations (Firestore/Room/REST) can update this reactively.
 *
 * Typically populated by:
 *  - Firestore query snapshot aggregation
 *  - Local Room cached aggregates
 *  - Fake repositories for offline/demo behavior
 */
data class GoalsSummary(
    val inProgress: Int = 0,
    val completed: Int = 0,
    val upcoming: Int = 0
)