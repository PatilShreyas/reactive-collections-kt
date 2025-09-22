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
 * A mutable map that also a [StateFlow] via [asStateFlow], allowing for reactive observation of its contents.
 *
 * It delegates [MutableMap] functionality to an internal map and emits an immutable [Map] snapshot to its collectors
 * whenever the map is modified.
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
 * If the key is not present in the map, it emits `null`.
 *
 * @param key The key whose associated value is to be observed.
 * @return A [Flow] that emits the value for the given key, or `null` if the key is absent.
 */
public fun <K, V> MutableReactiveMap<K, V>.valueAsFlow(key: K): Flow<V?> = asStateFlow().map { it[key] }.distinctUntilChanged()
