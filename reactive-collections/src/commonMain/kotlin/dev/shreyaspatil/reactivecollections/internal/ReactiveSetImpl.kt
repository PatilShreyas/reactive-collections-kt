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

import dev.shreyaspatil.reactivecollections.core.MutableReactiveSet

/**
 * The internal implementation of [MutableReactiveSet].
 */
internal class ReactiveSetImpl<E>(
    internalCollection: MutableSet<E>,
) : AbstractReactiveCollection<E, Set<E>, MutableSet<E>>(internalCollection, internalCollection::toSet),
    MutableReactiveSet<E>,
    MutableSet<E> by internalCollection {
    override fun add(element: E): Boolean = runNotifying { add(element) }

    override fun addAll(elements: Collection<E>): Boolean = runNotifying { addAll(elements) }

    override fun clear() = runNotifying { clear() }

    override fun remove(element: E): Boolean = runNotifying { remove(element) }

    override fun removeAll(elements: Collection<E>): Boolean = runNotifying { removeAll(elements) }

    override fun retainAll(elements: Collection<E>): Boolean = runNotifying { retainAll(elements) }
}
