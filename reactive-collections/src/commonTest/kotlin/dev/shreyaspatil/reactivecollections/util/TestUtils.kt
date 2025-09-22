/*
 * Copyright 2025 Shreyas Patil
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
@file:Suppress("ktlint:standard:filename")

package dev.shreyaspatil.reactivecollections.util

import dev.shreyaspatil.reactivecollections.core.MutableReactiveCollection
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.assertEquals

/**
 * API for testing Flow emissions that provides an elegant, readable way to verify
 * that flow operations emit the correct sequence of values.
 *
 * This class supports method chaining to create expressive test assertions in a BDD-style format.
 */
class FlowTester<T>(
    private val actualEmissions: List<T>,
) {
    /**
     * Verifies that the flow emitted exactly the specified sequence of values.
     *
     * @param expectedEmissions The exact sequence of values that should have been emitted
     * @throws AssertionError if the actual emissions don't match the expected sequence
     */
    fun emitsExactly(vararg expectedEmissions: T) = assertEquals(expectedEmissions.toList(), actualEmissions)
}

/**
 * Creates a fluent test builder for any Flow that allows you to perform operations
 * and then verify the exact sequence of emissions.
 *
 * @param flow The flow to test
 * @param operations A lambda that performs operations which may trigger flow emissions
 * @return A FlowTester for assertion chaining
 */
@OptIn(ExperimentalCoroutinesApi::class)
fun <T> testFlow(
    flow: Flow<T>,
    operations: suspend () -> Unit = {},
): FlowTester<T> =
    FlowTester(
        buildList {
            runTest(UnconfinedTestDispatcher()) {
                flow.onEach { add(it) }.launchIn(backgroundScope)
                operations()
            }
        },
    )

/**
 * Creates a fluent test builder for reactive collections that allows you to perform operations
 * and then verify the exact sequence of state emissions.
 */
fun <E, IC, MC : IC> testCollection(
    collection: MutableReactiveCollection<E, IC, MC>,
    operations: suspend MC.() -> Unit,
): FlowTester<IC> = testFlow(collection.asStateFlow()) { operations(collection as MC) }
