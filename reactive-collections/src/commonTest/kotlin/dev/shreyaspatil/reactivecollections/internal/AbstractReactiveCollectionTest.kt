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
package dev.shreyaspatil.reactivecollections.internal

import dev.shreyaspatil.reactivecollections.util.testFlow
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * Tests for [AbstractReactiveCollection] focusing on batch operations.
 * Uses ReactiveListImpl as a concrete implementation to test the abstract functionality.
 */
class AbstractReactiveCollectionTest {
    @Test
    fun `batchNotify - should perform multiple operations in single emission`() {
        val list = ReactiveListImpl(mutableListOf("A", "B", "C"))

        testFlow(list.asStateFlow()) {
            list.batchNotify {
                add("D")
                add("E")
                removeAt(0) // Remove "A"
                set(0, "Modified B") // Modify "B" to "Modified B"
            }
        }.emitsExactly(
            listOf("A", "B", "C"), // Initial state
            listOf("Modified B", "C", "D", "E"), // Final state after batch
        )
    }

    @Test
    fun `batchNotify - should emit only once for empty batch`() {
        val list = ReactiveListImpl(mutableListOf("A", "B", "C"))

        testFlow(list.asStateFlow()) {
            list.batchNotify {
                // Empty batch - no operations
            }
        }.emitsExactly(listOf("A", "B", "C"))
    }

    @Test
    fun `batchNotify - should handle exceptions and still emit`() {
        val list = ReactiveListImpl(mutableListOf("A", "B", "C"))

        testFlow(list.asStateFlow()) {
            runCatching {
                list.batchNotify {
                    add("D")
                    removeAt(10) // This will throw IndexOutOfBoundsException
                }
            }.exceptionOrNull()!!.let { it is IndexOutOfBoundsException }
        }.emitsExactly(
            listOf("A", "B", "C"), // Initial state
            listOf("A", "B", "C", "D"), // State after successful operations before exception
        )
    }

    @Test
    fun `batchNotifyAsync - should perform multiple operations in single emission`() = runTest {
        val list = ReactiveListImpl(mutableListOf("A", "B", "C"))

        testFlow(list.asStateFlow()) {
            list.batchNotifyAsync {
                add("D")
                add("E")
                delay(1000)
                removeAt(0) // Remove "A"
                set(0, "Modified B") // Modify "B" to "Modified B"
            }
        }.emitsExactly(
            listOf("A", "B", "C"), // Initial state
            listOf("Modified B", "C", "D", "E"), // Final state after batch
        )
    }

    @Test
    fun `batchNotifyAsync - should emit only once for empty batch`() = runTest {
        val list = ReactiveListImpl(mutableListOf("A", "B", "C"))

        testFlow(list.asStateFlow()) {
            list.batchNotifyAsync {
                delay(1000)
                // Empty batch - no operations
            }
        }.emitsExactly(listOf("A", "B", "C"))
    }

    @Test
    fun `batchNotifyAsync - should handle exceptions and still emit`() = runTest {
        val list = ReactiveListImpl(mutableListOf("A", "B", "C"))

        testFlow(list.asStateFlow()) {
            runCatching {
                list.batchNotifyAsync {
                    add("D")
                    removeAt(10) // This will throw IndexOutOfBoundsException
                }
            }.exceptionOrNull()!!.let { assertTrue { it is IndexOutOfBoundsException } }
        }.emitsExactly(
            listOf("A", "B", "C"), // Initial state
            listOf("A", "B", "C", "D"), // State after successful operations before exception
        )
    }

    @Test
    fun `nested batchNotify - should work correctly`() {
        val list = ReactiveListImpl(mutableListOf("A"))

        testFlow(list.asStateFlow()) {
            list.batchNotify {
                add("B")
                list.batchNotify {
                    add("C")
                    add("D")
                }
                add("E")
            }
        }.emitsExactly(
            listOf("A"), // Initial
            listOf("A", "B", "C", "D"), // After nested batch
            listOf("A", "B", "C", "D", "E"), // After outer batch
        )
    }
}
