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
 * whenever the set is modified.
 *
 * @param E The type of elements contained in the set.
 */
public interface MutableReactiveSet<E> :
    MutableReactiveCollection<E, Set<E>, MutableSet<E>>,
    MutableSet<E>
