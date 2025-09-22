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

import dev.shreyaspatil.reactivecollections.core.valueAsFlow
import dev.shreyaspatil.reactivecollections.reactiveMapOf
import dev.shreyaspatil.reactivecollections.util.testCollection
import dev.shreyaspatil.reactivecollections.util.testFlow
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ReactiveMapImplTest {
    @Test
    fun `put - should emit new map state on each addition`() {
        testCollection(reactiveMapOf<String, Int>()) {
            assertNull(put("A", 1))
            assertNull(put("B", 2))
            assertNull(put("C", 3))
        }.emitsExactly(
            emptyMap(),
            mapOf("A" to 1),
            mapOf("A" to 1, "B" to 2),
            mapOf("A" to 1, "B" to 2, "C" to 3),
        )
    }

    @Test
    fun `put with key replacement - should emit new state and return old value`() {
        testCollection(reactiveMapOf("A" to 1, "B" to 2)) {
            assertEquals(1, put("A", 10))
            assertEquals(2, put("B", 20))
            assertNull(put("C", 3))
        }.emitsExactly(
            mapOf("A" to 1, "B" to 2),
            mapOf("A" to 10, "B" to 2),
            mapOf("A" to 10, "B" to 20),
            mapOf("A" to 10, "B" to 20, "C" to 3),
        )
    }

    @Test
    fun `put same key-value pair - should not emit when value doesn't change`() {
        testCollection(reactiveMapOf("A" to 1)) {
            assertEquals(1, put("A", 1))
        }.emitsExactly(
            mapOf("A" to 1),
        )
    }

    @Test
    fun `putAll - should emit new state for non-empty maps`() {
        testCollection(reactiveMapOf("A" to 1)) {
            putAll(mapOf("B" to 2, "C" to 3))
            putAll(emptyMap())
            putAll(mapOf("D" to 4))
        }.emitsExactly(
            mapOf("A" to 1),
            mapOf("A" to 1, "B" to 2, "C" to 3),
            mapOf("A" to 1, "B" to 2, "C" to 3, "D" to 4),
        )
    }

    @Test
    fun `putAll with key replacements - should emit new state and replace existing keys`() {
        testCollection(reactiveMapOf("A" to 1, "B" to 2)) {
            putAll(mapOf("A" to 10, "C" to 3))
            putAll(mapOf("B" to 20, "D" to 4))
        }.emitsExactly(
            mapOf("A" to 1, "B" to 2),
            mapOf("A" to 10, "B" to 2, "C" to 3),
            mapOf("A" to 10, "B" to 20, "C" to 3, "D" to 4),
        )
    }

    @Test
    fun `putAll with empty map - should not emit when no changes`() {
        testCollection(reactiveMapOf("A" to 1)) { putAll(emptyMap()) }
            .emitsExactly(mapOf("A" to 1))
    }

    @Test
    fun `remove - should emit new state only on successful removal`() {
        testCollection(reactiveMapOf("A" to 1, "B" to 2, "C" to 3)) {
            assertEquals(2, remove("B"))
            assertNull(remove("X")) // No emission for non-existent key
            assertEquals(1, remove("A"))
            assertNull(remove("B")) // No emission for already removed key
        }.emitsExactly(
            mapOf("A" to 1, "B" to 2, "C" to 3),
            mapOf("A" to 1, "C" to 3),
            mapOf("C" to 3),
        )
    }

    @Test
    fun `remove from empty map - should return null and not emit`() {
        testCollection(reactiveMapOf<String, Int>()) { assertNull(remove("A")) }
            .emitsExactly(emptyMap())
    }

    @Test
    fun `remove non-existent key - should return null and not emit`() {
        testCollection(reactiveMapOf("A" to 1)) {
            assertNull(remove("B"))
            assertNull(remove("C"))
        }.emitsExactly(mapOf("A" to 1))
    }

    @Test
    fun `clear - should emit an empty map`() {
        testCollection(reactiveMapOf("A" to 1, "B" to 2, "C" to 3)) { clear() }
            .emitsExactly(mapOf("A" to 1, "B" to 2, "C" to 3), emptyMap())
    }

    @Test
    fun `clear empty map - should not emit when already empty`() {
        testCollection(reactiveMapOf<String, Int>()) { clear() }
            .emitsExactly(emptyMap())
    }

    @Test
    fun `multiple operations - should emit a new state for each operation`() {
        testCollection(reactiveMapOf<String, Int>()) {
            put("A", 1)
            put("B", 2)
            putAll(mapOf("C" to 3, "D" to 4))
            put("A", 10)
            remove("B")
            putAll(mapOf("E" to 5))
            clear()
        }.emitsExactly(
            emptyMap(),
            mapOf("A" to 1),
            mapOf("A" to 1, "B" to 2),
            mapOf("A" to 1, "B" to 2, "C" to 3, "D" to 4),
            mapOf("A" to 10, "B" to 2, "C" to 3, "D" to 4),
            mapOf("A" to 10, "C" to 3, "D" to 4),
            mapOf("A" to 10, "C" to 3, "D" to 4, "E" to 5),
            emptyMap(),
        )
    }

    @Test
    fun `map operations with null values - should handle null values correctly`() {
        testCollection(reactiveMapOf<String, String?>()) {
            assertNull(put("A", null))
            assertNull(put("B", "value"))
            assertEquals("value", put("B", null))
            assertEquals(null, put("A", "new_value"))
        }.emitsExactly(
            emptyMap(),
            mapOf("A" to null),
            mapOf("A" to null, "B" to "value"),
            mapOf("A" to null, "B" to null),
            mapOf("A" to "new_value", "B" to null),
        )
    }

    @Test
    fun `putAll with overlapping keys - should replace existing values`() {
        testCollection(reactiveMapOf("A" to 1, "B" to 2, "C" to 3)) {
            putAll(mapOf("B" to 20, "C" to 30, "D" to 4))
        }.emitsExactly(
            mapOf("A" to 1, "B" to 2, "C" to 3),
            mapOf("A" to 1, "B" to 20, "C" to 30, "D" to 4),
        )
    }

    @Test
    fun `complex key-value operations - should maintain map semantics`() {
        testCollection(reactiveMapOf<Int, String>()) {
            put(1, "one")
            put(2, "two")
            putAll(mapOf(3 to "three", 1 to "ONE"))
            assertEquals("two", remove(2))
            assertNull(remove(5)) // No emission for non-existent key
            put(4, "four")
            assertEquals(3, size)
        }.emitsExactly(
            emptyMap(),
            mapOf(1 to "one"),
            mapOf(1 to "one", 2 to "two"),
            mapOf(1 to "ONE", 2 to "two", 3 to "three"),
            mapOf(1 to "ONE", 3 to "three"),
            mapOf(1 to "ONE", 3 to "three", 4 to "four"),
        )
    }

    @Test
    fun `operations only emit when state actually changes - StateFlow behavior`() {
        testCollection(reactiveMapOf("A" to 1)) {
            assertEquals(1, put("A", 1)) // No emission - same value
            putAll(emptyMap()) // No emission - no changes
            assertNull(remove("B")) // No emission - key doesn't exist
            clear() // Emission - state changes
        }.emitsExactly(
            mapOf("A" to 1),
            emptyMap(),
        )
    }

    // Tests for valueAsFlow extension function
    @Test
    fun `valueAsFlow - should emit initial value and updates for existing key`() {
        val map = reactiveMapOf("A" to 1, "B" to 2)

        testFlow(map.valueAsFlow("A")) {
            map["A"] = 10
            map["A"] = 20
            map["B"] = 30 // Should not affect "A" flow
            map.remove("A")
            map["A"] = 40
        }.emitsExactly(1, 10, 20, null, 40)
    }

    @Test
    fun `valueAsFlow - should emit null for non-existent key and track additions`() {
        val map = reactiveMapOf<String, Any>("A" to 1)

        testFlow(map.valueAsFlow("B")) {
            map["A"] = 2 // Should not affect "B" flow
            map["B"] = "hello"
            map["B"] = "world"
            map.remove("B")
        }.emitsExactly(null, "hello", "world", null)
    }

    @Test
    fun `valueAsFlow - should use distinctUntilChanged behavior`() {
        val map = reactiveMapOf("A" to 1)

        testFlow(map.valueAsFlow("A")) {
            map["A"] = 1 // Same value - should not emit due to distinctUntilChanged
            map["A"] = 2 // Different value - should emit
            map["A"] = 2 // Same value - should not emit
            map["A"] = 3 // Different value - should emit
        }.emitsExactly(1, 2, 3)
    }

    @Test
    fun `valueAsFlow - should handle null values correctly`() {
        val map = reactiveMapOf<String, String?>("A" to "value")

        testFlow(map.valueAsFlow("A")) {
            map["A"] = null
            map["A"] = "new_value"
            map["A"] = null
            map.remove("A")
        }.emitsExactly("value", null, "new_value", null)
    }

    @Test
    fun `valueAsFlow - should not emit when other keys change`() {
        val map = reactiveMapOf("A" to 1, "B" to 2, "C" to 3)

        testFlow(map.valueAsFlow("A")) {
            // Modify other keys - should not affect "A" flow
            map["B"] = 20
            map["C"] = 30
            map.remove("B")
            map.putAll(mapOf("D" to 4, "E" to 5))

            // Only modify "A" at the end
            map["A"] = 10
        }.emitsExactly(1, 10)
    }

    @Test
    fun `valueAsFlow - should handle clear operation correctly`() {
        val map = reactiveMapOf("A" to 1, "B" to 2)

        testFlow(map.valueAsFlow("A")) {
            // Clear the map
            map.clear()

            // Add the key back
            map["A"] = 5
        }.emitsExactly(1, null, 5)
    }

    @Test
    fun `valueAsFlow - should work with complex value types`() {
        data class Person(
            val name: String,
            val age: Int,
        )

        val map = reactiveMapOf<String, Person>()
        val person1 = Person("Alice", 25)
        val person2 = Person("Bob", 30)

        testFlow(map.valueAsFlow("user")) {
            map["user"] = person1
            map["user"] = person2
            map["user"] = person1 // Different instance but same data
            map.remove("user")
        }.emitsExactly(null, person1, person2, person1, null)
    }
}
