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

import dev.shreyaspatil.reactivecollections.reactiveSetOf
import dev.shreyaspatil.reactivecollections.util.testCollection
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ReactiveSetImplTest {
    @Test
    fun `add - should emit new set state on each successful addition`() {
        testCollection(reactiveSetOf<Int>()) {
            assertTrue(add(1))
            assertTrue(add(2))
            assertFalse(add(1)) // Duplicate addition should return false
            assertTrue(add(3))
        }.emitsExactly(
            emptySet(),
            setOf(1),
            setOf(1, 2),
            setOf(1, 2, 3),
        )
    }

    @Test
    fun `addAll - should emit new state only for collections with new elements`() {
        testCollection(reactiveSetOf(1, 2)) {
            assertTrue(addAll(listOf(3, 4, 5)))
            assertFalse(addAll(emptyList()))
            assertFalse(addAll(listOf(1, 2))) // All elements already exist
            assertTrue(addAll(listOf(2, 6, 7))) // Only 6 and 7 are new
        }.emitsExactly(
            setOf(1, 2),
            setOf(1, 2, 3, 4, 5),
            setOf(1, 2, 3, 4, 5, 6, 7),
        )
    }

    @Test
    fun `clear - should emit an empty set`() {
        testCollection(reactiveSetOf(1, 2, 3, 4, 5)) {
            clear()
        }.emitsExactly(
            setOf(1, 2, 3, 4, 5),
            emptySet(),
        )
    }

    @Test
    fun `remove - should emit new state on successful removal`() {
        testCollection(reactiveSetOf("A", "B", "C", "D")) {
            assertTrue(remove("B"))
            assertFalse(remove("X")) // Element doesn't exist
            assertTrue(remove("D"))
            assertFalse(remove("B")) // Already removed
        }.emitsExactly(
            setOf("A", "B", "C", "D"),
            setOf("A", "C", "D"),
            setOf("A", "C"),
        )
    }

    @Test
    fun `remove from empty set - should return false and not emit`() {
        testCollection(reactiveSetOf<Int>()) { assertFalse(remove(1)) }
            .emitsExactly(emptySet())
    }

    @Test
    fun `removeAll - should emit new state on successful removals`() {
        testCollection(reactiveSetOf(1, 2, 3, 4, 5, 6)) {
            assertTrue(removeAll(listOf(2, 4, 8))) // 8 doesn't exist
            assertFalse(removeAll(listOf(10, 20))) // None exist
            assertTrue(removeAll(listOf(1, 3, 5)))
        }.emitsExactly(
            setOf(1, 2, 3, 4, 5, 6),
            setOf(1, 3, 5, 6),
            setOf(6),
        )
    }

    @Test
    fun `removeAll with empty collection - should not change set`() {
        testCollection(reactiveSetOf(1, 2, 3)) { assertFalse(removeAll(emptyList())) }
            .emitsExactly(setOf(1, 2, 3))
    }

    @Test
    fun `retainAll - should emit new state when set is changed`() {
        testCollection(reactiveSetOf(1, 2, 3, 4, 5, 6)) {
            assertTrue(retainAll(listOf(2, 4, 6, 8))) // 8 doesn't exist in original set
            assertFalse(retainAll(listOf(2, 4, 6))) // No change needed
            assertTrue(retainAll(listOf(2, 10))) // 10 doesn't exist, only 2 remains
        }.emitsExactly(
            setOf(1, 2, 3, 4, 5, 6),
            setOf(2, 4, 6),
            setOf(2),
        )
    }

    @Test
    fun `retainAll with empty collection - should clear the set`() {
        testCollection(reactiveSetOf(1, 2, 3)) {
            assertTrue(retainAll(emptyList()))
        }.emitsExactly(
            setOf(1, 2, 3),
            emptySet(),
        )
    }

    @Test
    fun `retainAll with superset - should not change set`() {
        testCollection(reactiveSetOf("A", "B")) {
            assertFalse(retainAll(listOf("A", "B", "C", "D")))
        }.emitsExactly(
            setOf("A", "B"),
        )
    }

    @Test
    fun `multiple operations - should emit a new state for each mutation`() {
        testCollection(reactiveSetOf<String>()) {
            addAll(listOf("A", "B", "C"))
            add("D")
            remove("B")
            addAll(listOf("E", "F"))
            retainAll(listOf("A", "D", "E", "X"))
            clear()
        }.emitsExactly(
            emptySet(),
            setOf("A", "B", "C"),
            setOf("A", "B", "C", "D"),
            setOf("A", "C", "D"),
            setOf("A", "C", "D", "E", "F"),
            setOf("A", "D", "E"),
            emptySet(),
        )
    }

    @Test
    fun `mixed successful and unsuccessful operations - should only emit on changes`() {
        testCollection(reactiveSetOf(1, 2, 3)) {
            assertFalse(add(2)) // No change
            assertTrue(add(4)) // Change
            assertFalse(remove(5)) // No change
            assertTrue(remove(1)) // Change
            assertFalse(addAll(listOf(2, 3))) // No change
            assertTrue(addAll(listOf(5, 6))) // Change
        }.emitsExactly(
            setOf(1, 2, 3),
            setOf(1, 2, 3, 4),
            setOf(2, 3, 4),
            setOf(2, 3, 4, 5, 6),
        )
    }

    @Test
    fun `set uniqueness - should maintain set invariants`() {
        testCollection(reactiveSetOf<Int>()) {
            assertTrue(addAll(listOf(1, 1, 2, 2, 3, 3)))
            assertEquals(3, size)
            assertTrue(contains(1))
            assertTrue(contains(2))
            assertTrue(contains(3))
        }.emitsExactly(
            emptySet(),
            setOf(1, 2, 3),
        )
    }
}
