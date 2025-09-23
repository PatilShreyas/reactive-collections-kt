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

import dev.shreyaspatil.reactivecollections.core.MutableReactiveMap

/**
 * The internal implementation of [MutableReactiveMap].
 */
internal class ReactiveMapImpl<K, V>(
    internalCollection: MutableMap<K, V>,
) : AbstractReactiveCollection<Map.Entry<K, V>, Map<K, V>, MutableMap<K, V>>(internalCollection, internalCollection::toMap),
    MutableReactiveMap<K, V>,
    MutableMap<K, V> by internalCollection {

    override fun put(key: K, value: V): V? = runNotifying { put(key, value) }
    override fun putAll(from: Map<out K, V>) = runNotifying { putAll(from) }
    override fun remove(key: K): V? = runNotifying { remove(key) }
    override fun clear() = runNotifying { clear() }
}
