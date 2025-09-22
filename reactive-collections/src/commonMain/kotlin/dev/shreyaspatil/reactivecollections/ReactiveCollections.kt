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
package dev.shreyaspatil.reactivecollections

import dev.shreyaspatil.reactivecollections.core.MutableReactiveList
import dev.shreyaspatil.reactivecollections.core.MutableReactiveMap
import dev.shreyaspatil.reactivecollections.core.MutableReactiveSet
import dev.shreyaspatil.reactivecollections.internal.ReactiveListImpl
import dev.shreyaspatil.reactivecollections.internal.ReactiveMapImpl
import dev.shreyaspatil.reactivecollections.internal.ReactiveSetImpl

/**
 * Returns an empty new [MutableReactiveList].
 */
public fun <E> reactiveListOf(): MutableReactiveList<E> = ReactiveListImpl(mutableListOf())

/**
 * Returns a new [MutableReactiveList] containing the specified [elements]
 */
public fun <E> reactiveListOf(vararg elements: E): MutableReactiveList<E> = ReactiveListImpl(elements.toMutableList())

/**
 * Returns an empty new [MutableReactiveSet].
 */
public fun <E> reactiveSetOf(): MutableReactiveSet<E> = ReactiveSetImpl(mutableSetOf())

/**
 * Returns a new [MutableReactiveSet] containing the specified [elements]
 */
public fun <E> reactiveSetOf(vararg elements: E): MutableReactiveSet<E> = ReactiveSetImpl(elements.toMutableSet())

/**
 * Returns an empty new [MutableReactiveMap].
 */
public fun <K, V> reactiveMapOf(): MutableReactiveMap<K, V> = ReactiveMapImpl(mutableMapOf())

/**
 * Returns a [MutableReactiveMap] with the specified contents, given as a list of pairs where the first value is the
 * key and the second is the value.
 *
 * If multiple pairs have the same key, the resulting map will contain the value from the last of those pairs.
 *
 * Entries of the map are iterated in the order they were specified.
 */
public fun <K, V> reactiveMapOf(vararg pairs: Pair<K, V>): MutableReactiveMap<K, V> = ReactiveMapImpl(mutableMapOf(*pairs))

/**
 * Returns a new [MutableReactiveList] filled with all elements of this collection.
 */
public fun <E> Collection<E>.toMutableReactiveList(): MutableReactiveList<E> = ReactiveListImpl(this.toMutableList())

/**
 * Returns a new [MutableReactiveList] filled with all elements of this collection.
 */
public fun <E> List<E>.toMutableReactiveList(): MutableReactiveList<E> = ReactiveListImpl(this.toMutableList())

/**
 * Returns a new [MutableReactiveList] filled with all elements of this collection.
 */
public fun <E> MutableList<E>.toMutableReactiveList(): MutableReactiveList<E> = ReactiveListImpl(this)

/**
 * Returns a new [MutableReactiveSet] filled with all elements of this collection.
 */
public fun <E> Collection<E>.toMutableReactiveSet(): MutableReactiveSet<E> = ReactiveSetImpl(this.toMutableSet())

/**
 * Returns a new [MutableReactiveSet] filled with all elements of this collection.
 */
public fun <E> Set<E>.toMutableReactiveSet(): MutableReactiveSet<E> = ReactiveSetImpl(this.toMutableSet())

/**
 * Returns a new [MutableReactiveSet] filled with all elements of this collection.
 */
public fun <E> MutableSet<E>.toMutableReactiveSet(): MutableReactiveSet<E> = ReactiveSetImpl(this)

/**
 * Returns a new [MutableReactiveMap] filled with all elements of this collection.
 */
public fun <K, V> Map<K, V>.toMutableReactiveMap(): MutableReactiveMap<K, V> = ReactiveMapImpl(this.toMutableMap())
