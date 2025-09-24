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
 * A foundational interface for mutable collections that expose their state as a [StateFlow].
 *
 * This allows observers to reactively receive updates whenever the collection is modified.
 * The emitted value is an immutable snapshot of the collection at the time of modification.
 *
 * @param E The type of elements in the collection.
 * @param IC The type of immutable collection snapshot emitted by the [StateFlow] (e.g., [List], [Set], [Map]).
 * @param MC The underlying mutable collection type that this interface wraps
 *  (e.g., [MutableList], [MutableSet], [MutableMap]).
 */
public interface MutableReactiveCollection<E, out IC, out MC : IC> {
    /**
     * Returns the collection's content as a [StateFlow] of an immutable collection [IC] type.
     *
     * The flow emits a new immutable snapshot of the collection whenever a change occurs.
     * Observers can collect this flow to react to data modifications.
     *
     * @return A [StateFlow] that emits the latest immutable state of the collection.
     */
    public fun asStateFlow(): StateFlow<IC>

    /**
     * Executes a block of modifications on the underlying mutable collection and notifies observers
     * only once after the block has completed.
     *
     * This is useful for performing multiple additions, removals, or other mutations in a single,
     * atomic operation, preventing multiple emissions from the [StateFlow]. This is particularly
     * important for performance when making bulk changes, as it avoids triggering multiple
     * reactive updates that could cause unnecessary recomputations in observers.
     *
     * Example:
     * ```kotlin
     * val shoppingList = reactiveListOf<String>()
     *
     * // Without batch - triggers 3 separate emissions
     * shoppingList.add("Apple")
     * shoppingList.add("Banana")
     * shoppingList.add("Orange")
     *
     * // With batch - triggers only 1 emission at the end
     * shoppingList.batchNotify {
     *     add("Milk")
     *     add("Bread")
     *     removeAt(0) // Remove "Apple"
     *     set(1, "Blueberries") // Replace "Banana"
     * } // Observers are notified only once here with final state
     * ```
     *
     * @param block A lambda function with the mutable collection as its receiver, where
     * modifications can be performed.
     */
    public fun batchNotify(block: MC.() -> Unit)

    /**
     * Executes a suspending block of modifications on the underlying mutable collection and notifies
     * observers only once after the block has completed.
     *
     * This is the asynchronous equivalent of [batchNotify], suitable for operations that involve
     * coroutines or other suspending functions. This is particularly useful when you need to perform
     * async operations (like network calls or database queries) as part of your collection updates.
     *
     * Example:
     * ```kotlin
     * val userCache = reactiveMapOf<String, User>()
     *
     * // Batch async operations - single emission at the end
     * userCache.batchNotifyAsync {
     *     // Simulate async operations
     *     val user1 = userRepository.fetchUser("alice") // suspending call
     *     val user2 = userRepository.fetchUser("bob")   // suspending call
     *
     *     put("alice", user1)
     *     put("bob", user2)
     *     remove("charlie") // Remove cached user
     *
     *     delay(100) // Some async processing
     * } // Observers are notified only once here with all updates
     * ```
     *
     * @param block A suspending lambda function with the mutable collection as its receiver.
     */
    public suspend fun batchNotifyAsync(block: suspend MC.() -> Unit)
}
