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

import kotlinx.coroutines.flow.StateFlow

/**
 * A mutable set that also exposes a [StateFlow] via [asStateFlow], allowing for reactive observation of its contents.
 *
 * It delegates [MutableSet] functionality to an internal set and emits an immutable [Set] snapshot to its collectors
 * whenever the set is modified. This enables reactive programming patterns where observers can automatically respond
 * to changes in the set's contents while maintaining set semantics (no duplicate elements).
 *
 * Example:
 * ```kotlin
 * val activeUsers = reactiveSetOf("alice", "bob")
 *
 * // Observe all changes to the set
 * activeUsers.asStateFlow().collect { users ->
 *     println("Active users: $users")
 * } // Immediately emits: "Active users: [alice, bob]"
 *
 * // Standard set operations
 * activeUsers.add("charlie")    // Triggers emission: [alice, bob, charlie]
 * activeUsers.add("alice")      // No emission (duplicate element)
 * activeUsers.remove("bob")     // Triggers emission: [alice, charlie]
 *
 * // Batch operations for multiple changes
 * activeUsers.batchNotify {
 *     addAll(setOf("david", "eve"))
 *     remove("alice")
 * } // Single emission: [charlie, david, eve]
 *
 * // Set-specific operations
 * val otherUsers = setOf("frank", "charlie")
 * activeUsers.retainAll(otherUsers)  // Triggers emission: [charlie]
 * println(activeUsers.contains("david"))  // false
 * ```
 *
 * @param E The type of elements contained in the set.
 */
public interface MutableReactiveSet<E> :
    MutableReactiveCollection<E, Set<E>, MutableSet<E>>,
    MutableSet<E>
