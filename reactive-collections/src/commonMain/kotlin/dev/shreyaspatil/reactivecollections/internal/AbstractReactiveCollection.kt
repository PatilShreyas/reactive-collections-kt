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

import dev.shreyaspatil.reactivecollections.core.MutableReactiveCollection
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * An abstract base class for [MutableReactiveCollection] implementations.
 * It manages the [StateFlow] and orchestrates state notifications.
 *
 * @property internalCollection The underlying mutable collection instance that holds the actual data.
 */
internal abstract class AbstractReactiveCollection<E, IC, MC : IC>(
    private val internalCollection: MC,
    private val getImmutableSnapshot: () -> IC,
) : MutableReactiveCollection<E, IC, MC> {
    private val state = MutableStateFlow(getImmutableSnapshot())

    override fun asStateFlow(): StateFlow<IC> = state.asStateFlow()

    override fun batchNotify(block: MC.() -> Unit) {
        runNotifying { block() }
    }

    override suspend fun batchNotifyAsync(block: suspend MC.() -> Unit) {
        runNotifying { block() }
    }

    /**
     * Executes a modifying [block] of code and notifies the [StateFlow] observers with a new
     * immutable snapshot upon completion.
     */
    protected inline fun <R> runNotifying(block: MC.() -> R): R {
        try {
            return internalCollection.block()
        } finally {
            notifyCollectionChanged()
        }
    }

    private fun notifyCollectionChanged() {
        state.value = getImmutableSnapshot()
    }

    override fun equals(other: Any?): Boolean {
        if (other !is AbstractReactiveCollection<*, *, *>) return false
        return internalCollection?.equals(other.internalCollection) ?: false
    }

    override fun hashCode(): Int = internalCollection.hashCode()

    override fun toString(): String = internalCollection.toString()
}
