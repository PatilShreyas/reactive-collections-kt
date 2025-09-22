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
     * atomic operation, preventing multiple emissions from the [StateFlow].
     *
     * Example:
     * ```
     * val list = reactiveListOf<String>()
     * list.batchUpdate {
     *   add("Apple")
     *   add("Banana")
     *   remove("OldFruit")
     * } // Observers are notified only once here.
     * ```
     *
     * @param block A lambda function with the mutable collection as its receiver, where
     * modifications can be performed.
     */
    public fun batchUpdate(block: MC.() -> Unit)

    /**
     * Executes a suspending block of modifications on the underlying mutable collection and notifies
     * observers only once after the block has completed.
     *
     * This is the asynchronous equivalent of [batchUpdate], suitable for operations that involve
     * coroutines or other suspending functions.
     *
     * @param block A suspending lambda function with the mutable collection as its receiver.
     */
    public suspend fun batchUpdateAsync(block: suspend MC.() -> Unit)
}
