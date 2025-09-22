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

import dev.shreyaspatil.reactivecollections.core.MutableReactiveList

/**
 * The internal implementation of [MutableReactiveList].
 */
internal class ReactiveListImpl<E>(
    internalCollection: MutableList<E>,
) : AbstractReactiveCollection<E, List<E>, MutableList<E>>(internalCollection, internalCollection::toList),
    MutableReactiveList<E>,
    MutableList<E> by internalCollection {
    override fun add(element: E): Boolean = runNotifying { add(element) }

    override fun add(
        index: Int,
        element: E,
    ) = runNotifying { add(index, element) }

    override fun set(
        index: Int,
        element: E,
    ): E = runNotifying { set(index, element) }

    override fun addAll(elements: Collection<E>): Boolean = runNotifying { addAll(elements) }

    override fun addAll(
        index: Int,
        elements: Collection<E>,
    ): Boolean = runNotifying { addAll(index, elements) }

    override fun clear() = runNotifying { clear() }

    override fun remove(element: E): Boolean = runNotifying { remove(element) }

    override fun removeAt(index: Int): E = runNotifying { removeAt(index) }

    override fun removeAll(elements: Collection<E>): Boolean = runNotifying { removeAll(elements) }

    override fun retainAll(elements: Collection<E>): Boolean = runNotifying { retainAll(elements) }
}
