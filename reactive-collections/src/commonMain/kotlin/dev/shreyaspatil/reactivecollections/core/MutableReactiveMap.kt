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
package dev.shreyaspatil.reactivecollections.core

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

/**
 * A mutable map that also exposes a [StateFlow] via [asStateFlow], allowing for reactive observation of its contents.
 *
 * It delegates [MutableMap] functionality to an internal map and emits an immutable [Map] snapshot to its collectors
 * whenever the map is modified. This enables reactive programming patterns where observers can automatically respond
 * to changes in the map's contents.
 *
 * Example:
 * ```kotlin
 * val userMap = reactiveMapOf("name" to "Alice", "age" to "25")
 *
 * // Observe all changes to the map
 * userMap.asStateFlow().collect { map ->
 *     println("User data updated: $map")
 * } // Immediately emits: "User data updated: {name=Alice, age=25}"
 *
 * // Observe specific key changes
 * userMap.valueAsFlow("name").collect { name ->
 *     println("Name changed to: $name")
 * } // Immediately emits: "Name changed to: Alice"
 *
 * // Modify the map - triggers reactive updates
 * userMap["city"] = "NYC" // Triggers both flows above
 * userMap.remove("age")   // Triggers map flow, name flow unaffected
 * ```
 *
 * @param K The type of keys in the map.
 * @param V The type of values in the map.
 */
public interface MutableReactiveMap<K, V> :
    MutableReactiveCollection<Map.Entry<K, V>, Map<K, V>, MutableMap<K, V>>,
    MutableMap<K, V>

/**
 * Creates a [Flow] that emits the value associated with the specified [key] whenever **this** map changes.
 *
 * This extension function allows you to observe changes to a specific key in the reactive map without
 * having to observe the entire map. The flow only emits when the value for the specified key actually
 * changes.
 *
 * If the key is not present in the map, it emits `null`. When the key is removed from the map,
 * it will emit `null` as well.
 *
 * Example:
 * ```kotlin
 * val userMap = reactiveMapOf("name" to "Alice", "age" to "25", "city" to "NYC")
 *
 * // Observe changes to the "name" key specifically
 * userMap.valueAsFlow("name").collect { name ->
 *     println("Name is now: $name")
 * } // Immediately emits: "Name is now: Alice"
 *
 * // Observe a key that doesn't exist
 * userMap.valueAsFlow("email").collect { email ->
 *     println("Email is: $email")
 * } // Immediately emits: "Email is: null"
 *
 * // Modify the map
 * userMap["name"] = "Bob"        // Emits: "Name is now: Bob"
 * userMap["age"] = "30"          // No emission for name flow
 * userMap["email"] = "bob@test"  // Emits: "Email is: bob@test"
 * userMap.remove("name")         // Emits: "Name is now: null"
 * ```
 *
 * @param K The type of keys in the map.
 * @param V The type of values in the map.
 * @param key The key whose associated value is to be observed.
 * @return A [Flow] that emits the value for the given key, or `null` if the key is absent.
 */
public fun <K, V> MutableReactiveMap<K, V>.valueAsFlow(key: K): Flow<V?> = asStateFlow().map { it[key] }.distinctUntilChanged()
