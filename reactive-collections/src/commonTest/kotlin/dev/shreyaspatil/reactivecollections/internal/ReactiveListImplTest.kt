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

import dev.shreyaspatil.reactivecollections.core.getAsFlow
import dev.shreyaspatil.reactivecollections.core.subListAsFlow
import dev.shreyaspatil.reactivecollections.reactiveListOf
import dev.shreyaspatil.reactivecollections.util.testCollection
import dev.shreyaspatil.reactivecollections.util.testFlow
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ReactiveListImplTest {
    @Test
    fun `add - should emit new list state on each addition`() {
        testCollection(reactiveListOf<Int>()) {
            assertTrue(add(1))
            assertTrue(add(2))
            assertTrue(add(1))
        }.emitsExactly(
            emptyList(),
            listOf(1),
            listOf(1, 2),
            listOf(1, 2, 1),
        )
    }

    @Test
    fun `add at index - should emit new list state for each insertion`() {
        testCollection(reactiveListOf("A", "B", "D")) {
            add(2, "C")
            add(0, "Z")
            add(size, "E")
        }.emitsExactly(
            listOf("A", "B", "D"),
            listOf("A", "B", "C", "D"),
            listOf("Z", "A", "B", "C", "D"),
            listOf("Z", "A", "B", "C", "D", "E"),
        )
    }

    @Test
    fun `set - should emit new list state and return old value`() {
        testCollection(reactiveListOf("A", "B", "C")) {
            assertEquals("B", set(1, "X"))
            assertEquals("A", set(0, "Y"))
            assertEquals("C", set(2, "Z"))
        }.emitsExactly(
            listOf("A", "B", "C"),
            listOf("A", "X", "C"),
            listOf("Y", "X", "C"),
            listOf("Y", "X", "Z"),
        )
    }

    @Test
    fun `addAll - should emit new state only for non-empty additions`() {
        testCollection(reactiveListOf(1, 2)) {
            assertTrue(addAll(listOf(3, 4, 5)))
            assertFalse(addAll(emptyList()))
            assertTrue(addAll(listOf(6)))
        }.emitsExactly(
            listOf(1, 2),
            listOf(1, 2, 3, 4, 5),
            listOf(1, 2, 3, 4, 5, 6),
        )
    }

    @Test
    fun `addAll at index - should emit new state only for non-empty additions`() {
        testCollection(reactiveListOf("A", "D")) {
            assertTrue(addAll(1, listOf("B", "C")))
            assertTrue(addAll(0, listOf("X")))
            assertTrue(addAll(size, listOf("E", "F")))
            assertFalse(addAll(2, emptyList()))
        }.emitsExactly(
            listOf("A", "D"),
            listOf("A", "B", "C", "D"),
            listOf("X", "A", "B", "C", "D"),
            listOf("X", "A", "B", "C", "D", "E", "F"),
        )
    }

    @Test
    fun `clear - should emit an empty list`() {
        testCollection(reactiveListOf(1, 2, 3, 4, 5)) {
            clear()
        }.emitsExactly(
            listOf(1, 2, 3, 4, 5),
            emptyList(),
        )
    }

    @Test
    fun `remove - should emit new state on successful removal`() {
        testCollection(reactiveListOf("A", "B", "C", "B", "D")) {
            assertTrue(remove("B"))
            assertFalse(remove("X"))
            assertTrue(remove("D"))
        }.emitsExactly(
            listOf("A", "B", "C", "B", "D"),
            listOf("A", "C", "B", "D"),
            listOf("A", "C", "B"),
        )
    }

    @Test
    fun `removeAt - should emit new state and return removed element`() {
        testCollection(reactiveListOf("A", "B", "C", "D")) {
            assertEquals("B", removeAt(1))
            assertEquals("A", removeAt(0))
            assertEquals("D", removeAt(size - 1))
        }.emitsExactly(
            listOf("A", "B", "C", "D"),
            listOf("A", "C", "D"),
            listOf("C", "D"),
            listOf("C"),
        )
    }

    @Test
    fun `removeAll - should emit new state on successful removals`() {
        testCollection(reactiveListOf(1, 2, 3, 4, 5, 2, 6)) {
            assertTrue(removeAll(listOf(2, 4)))
            assertFalse(removeAll(listOf(10, 20)))
            assertTrue(removeAll(listOf(1, 3, 5)))
        }.emitsExactly(
            listOf(1, 2, 3, 4, 5, 2, 6),
            listOf(1, 3, 5, 6),
            listOf(6),
        )
    }

    @Test
    fun `retainAll - should emit new state when list is changed`() {
        testCollection(reactiveListOf(1, 2, 3, 4, 5, 6)) {
            assertTrue(retainAll(listOf(2, 4, 6, 8)))
            assertFalse(retainAll(listOf(2, 4, 6)))
            assertTrue(retainAll(listOf(2)))
        }.emitsExactly(
            listOf(1, 2, 3, 4, 5, 6),
            listOf(2, 4, 6),
            listOf(2),
        )
    }

    @Test
    fun `retainAll with empty collection - should clear the list`() {
        testCollection(reactiveListOf(1, 2, 3)) {
            assertTrue(retainAll(emptyList()))
        }.emitsExactly(
            listOf(1, 2, 3),
            emptyList(),
        )
    }

    @Test
    fun `multiple operations - should emit a new state for each mutation`() {
        testCollection(reactiveListOf<String>()) {
            addAll(listOf("A", "B", "C"))
            add(1, "X")
            this[0] = "Z"
            remove("B")
            removeAt(1)
            retainAll(listOf("Z", "C"))
        }.emitsExactly(
            emptyList(),
            listOf("A", "B", "C"),
            listOf("A", "X", "B", "C"),
            listOf("Z", "X", "B", "C"),
            listOf("Z", "X", "C"),
            listOf("Z", "C"),
        )
    }

    // Tests for getAsFlow extension function
    @Test
    fun `getAsFlow - should emit initial value and updates for valid index`() {
        val list = reactiveListOf("A", "B", "C")

        testFlow(list.getAsFlow(1)) {
            list[1] = "X"
            list.add(1, "Y")
            list.removeAt(1)
        }.emitsExactly("B", "X", "Y", "X")
    }

    @Test
    fun `getAsFlow - should emit null for out of bounds index`() {
        val list = reactiveListOf("A", "B")

        testFlow(list.getAsFlow(5)) {
            list.add("C")
            list.add("D")
            list.add("E")
            list.add("F") // Now index 5 should have value "F"
        }.emitsExactly(null, "F")
    }

    @Test
    fun `getAsFlow - should handle list shrinking and growing`() {
        val list = reactiveListOf("A", "B", "C", "D")

        testFlow(list.getAsFlow(2)) {
            list.removeAt(3) // Remove "D", index 2 still has "C" - no emission
            list.removeAt(2) // Remove "C", index 2 now out of bounds - emit null
            list.add("X") // Add "X", index 2 now has "X" - emit "X"
            list.add("Y") // Add "Y", index 2 still has "X" - no emission
        }.emitsExactly("C", null, "X")
    }

    @Test
    fun `getAsFlow - should use distinctUntilChanged behavior`() {
        val list = reactiveListOf("A", "B", "C")

        testFlow(list.getAsFlow(1)) {
            list[1] = "B" // Same value - should not emit
            list[1] = "X" // Different value - should emit
            list[1] = "X" // Same value - should not emit
            list[0] = "Z" // Different index - should not affect index 1
        }.emitsExactly("B", "X")
    }

    @Test
    fun `getAsFlow - should handle negative index`() {
        val list = reactiveListOf("A", "B", "C")

        testFlow(list.getAsFlow(-1)) {
            list.add("D")
            list.removeAt(0)
        }.emitsExactly(null)
    }

    @Test
    fun `getAsFlow - should handle index 0`() {
        val list = reactiveListOf<String>()

        testFlow(list.getAsFlow(0)) {
            list.add("First")
            list[0] = "Updated"
            list.add(0, "New First")
            list.removeAt(0)
        }.emitsExactly(null, "First", "Updated", "New First", "Updated")
    }

    // Tests for subListAsFlow extension function
    @Test
    fun `subListAsFlow - should emit initial sublist and updates in strict mode`() {
        val list = reactiveListOf("A", "B", "C", "D", "E")

        testFlow(list.subListAsFlow(1, 4)) {
            list[2] = "X"
            list.add(2, "Y")
            list.removeAt(1)
        }.emitsExactly(
            listOf("B", "C", "D"),
            listOf("B", "X", "D"),
            listOf("B", "Y", "X"),
            listOf("Y", "X", "D"),
        )
    }

    @Test
    fun `subListAsFlow - should return empty list for invalid indices in strict mode`() {
        val list = reactiveListOf("A", "B", "C")

        testFlow(list.subListAsFlow(5, 7, strict = true)) {
            list.add("D")
            list.add("E")
            list.add("F")
            list.add("G") // Now indices 5,6 are valid, should emit ["F", "G"]
        }.emitsExactly(
            emptyList(),
            listOf("F", "G"),
        )
    }

    @Test
    fun `subListAsFlow - should return empty list for negative fromIndex in strict mode`() {
        val list = reactiveListOf("A", "B", "C")

        testFlow(list.subListAsFlow(-1, 2, strict = true)) {
            list.add("D")
        }.emitsExactly(emptyList())
    }

    @Test
    fun `subListAsFlow - should return empty list when fromIndex greater than toIndex in strict mode`() {
        val list = reactiveListOf("A", "B", "C")

        testFlow(list.subListAsFlow(2, 1, strict = true)) {
            list.add("D")
        }.emitsExactly(emptyList())
    }

    @Test
    fun `subListAsFlow - should coerce indices in lenient mode`() {
        val list = reactiveListOf("A", "B", "C")

        testFlow(list.subListAsFlow(-1, 10, strict = false)) {
            list.add("D")
            list.add("E")
        }.emitsExactly(
            listOf("A", "B", "C"),
            listOf("A", "B", "C", "D"),
            listOf("A", "B", "C", "D", "E"),
        )
    }

    @Test
    fun `subListAsFlow - should handle partial range in lenient mode`() {
        val list = reactiveListOf("A", "B", "C")

        testFlow(list.subListAsFlow(1, 10, strict = false)) {
            list.add("D")
            list.removeAt(1)
        }.emitsExactly(
            listOf("B", "C"),
            listOf("B", "C", "D"),
            listOf("C", "D"),
        )
    }

    @Test
    fun `subListAsFlow - should not emit distinct items`() {
        val list = reactiveListOf("A", "B", "C", "D", "E")

        testFlow(list.subListAsFlow(1, 4)) {
            list[0] = "X" // Outside sublist range - should not emit
            list[4] = "Y" // Outside sublist range - should not emit
            list[2] = "Z" // Inside sublist range - should emit
        }.emitsExactly(
            listOf("B", "C", "D"),
            listOf("B", "Z", "D"),
        )
    }

    @Test
    fun `subListAsFlow - should handle empty sublist range`() {
        val list = reactiveListOf("A", "B", "C")

        testFlow(list.subListAsFlow(1, 1)) {
            list.add(1, "X")
            list.removeAt(1)
        }.emitsExactly(
            emptyList(),
        )
    }

    @Test
    fun `subListAsFlow - should handle full list range`() {
        val list = reactiveListOf("A", "B", "C")

        testFlow(list.subListAsFlow(0, 3)) {
            list.add("D") // Sublist range doesn't include index 3, so no change to [0,3)
            list.removeAt(0) // Now list is ["B", "C", "D"], sublist [0,3) is ["B", "C", "D"]
        }.emitsExactly(
            listOf("A", "B", "C"),
            listOf("B", "C", "D"),
        )
    }

    @Test
    fun `subListAsFlow in strict mode - should not emit items when list shrinks`() {
        val list = reactiveListOf("A", "B", "C", "D", "E")

        testFlow(list.subListAsFlow(2, 5, strict = true)) {
            list.removeAt(4) // Remove "E"
            list.removeAt(3) // Remove "D"
            list.removeAt(2) // Remove "C", now list is ["A", "B"]
        }.emitsExactly(
            listOf("C", "D", "E"),
            emptyList(),
        )
    }

    @Test
    fun `subListAsFlow in lenient mode - should emit items when list shrinks`() {
        val list = reactiveListOf("A", "B", "C", "D", "E")

        testFlow(list.subListAsFlow(2, 5, strict = false)) {
            list.removeAt(4) // Remove "E"
            list.removeAt(3) // Remove "D"
            list.removeAt(2) // Remove "C", now list is ["A", "B"]
        }.emitsExactly(
            listOf("C", "D", "E"),
            listOf("C", "D"),
            listOf("C"),
            emptyList(),
        )
    }

    @Test
    fun `subListAsFlow - should return empty list for invalid indices in strict mode1`() {
        val list = reactiveListOf("A", "B", "C")

        // Test negative fromIndex
        testFlow(list.subListAsFlow(-1, 2, strict = true)) {
            list.add("D")
        }.emitsExactly(emptyList())

        // Test fromIndex > toIndex
        testFlow(list.subListAsFlow(2, 1, strict = true)) {
            list.add("E")
        }.emitsExactly(emptyList())

        // Test fromIndex >= list.size
        testFlow(list.subListAsFlow(5, 10, strict = true)) {
            list.add("F")
        }.emitsExactly(emptyList())
    }

    @Test
    fun `subListAsFlow - should handle single element list`() {
        val list = reactiveListOf("A")

        testFlow(list.subListAsFlow(0, 1)) {
            list[0] = "B"
            list.clear()
        }.emitsExactly(
            listOf("A"),
            listOf("B"),
            emptyList(),
        )
    }

    @Test
    fun `subListAsFlow - should handle very large toIndex in strict mode`() {
        val list = reactiveListOf("A", "B")

        testFlow(list.subListAsFlow(0, 1000, strict = true)) {
            list.add("C")
        }.emitsExactly(emptyList()) // Should be empty due to invalid range
    }
}
