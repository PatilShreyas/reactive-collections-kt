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
 * A mutable list that also exposes a [StateFlow] via [asStateFlow], allowing for reactive observation of its contents.
 *
 * It delegates [MutableList] functionality to an internal list and emits an immutable [List] snapshot to its
 * collectors whenever the list is modified.
 *
 * @param E The type of elements contained in the list.
 */
public interface MutableReactiveList<E> :
    MutableReactiveCollection<E, List<E>, MutableList<E>>,
    MutableList<E>

/**
 * Creates a [Flow] that emits the element at the specified [index] whenever the list changes.
 *
 * If the index is out of bounds, it emits `null`. This is useful for observing a specific
 * position in the list without causing an exception if the list shrinks.
 *
 * @param index The index of the element to observe.
 * @return A [Flow] that emits the element at the given index, or `null` if the index is invalid.
 */
public fun <E> MutableReactiveList<E>.getAsFlow(index: Int): Flow<E?> = asStateFlow().map { it.getOrNull(index) }.distinctUntilChanged()

/**
 * Returns a [Flow] that emits a sublist view of this reactive list between the specified
 * [fromIndex] (inclusive) and [toIndex] (exclusive).
 *
 * This function observes the reactive list and emits a new sublist snapshot whenever the original list's content
 * changes in a way that affects the sublist. It prevents emissions of identical subsequent sublists.
 *
 * ## Behavior Modes
 *
 * ### Strict Mode (default: `strict = true`)
 * - Returns an empty list if any index is out of bounds or invalid
 * - Validates that `0 <= fromIndex <= toIndex <= list.size`
 *
 * ### Lenient Mode (`strict = false`)
 * - Automatically coerces indices to valid ranges using [coerceIn]
 * - Useful when you want to observe "as much as possible" of a range
 *
 * @param fromIndex The starting index of the sublist (inclusive).
 * @param toIndex The ending index of the sublist (exclusive).
 * @param strict If `true` (default), an empty list is returned for any invalid indices. This approach avoids exceptions
 * for invalid ranges. If `false` (lenient mode), indices are safely coerced to the valid range.
 * @return A [Flow] that emits the specified sublist when the source list changes.
 */
public fun <E> MutableReactiveList<E>.subListAsFlow(
    fromIndex: Int,
    toIndex: Int,
    strict: Boolean = true,
): Flow<List<E>> =
    asStateFlow()
        .map { list ->
            val start = if (strict) fromIndex else fromIndex.coerceIn(0, list.size)
            val end = if (strict) toIndex else toIndex.coerceIn(0, list.size)

            if (start in 0..end && end <= list.size) {
                list.subList(start, end)
            } else {
                emptyList()
            }
        }.distinctUntilChanged()
