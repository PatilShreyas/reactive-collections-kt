# ⚡ Reactive Collections for Kotlin

> _Mutable Collections that are also `StateFlow`s. Simple, powerful, and fun._

**Reactive Collections** is a Kotlin Multiplatform library that brings reactive programming to standard collections. 
Transform your regular `List`, `Set`, and `Map` into reactive data structures that emit updates through Kotlin Flows 
whenever their content changes.

## 🤔 The Core Idea

In modern reactive programming, we often find ourselves needing to observe changes to a list or map. The common 
approach is to wrap it in a `MutableStateFlow`:

```kotlin
private val _users = MutableStateFlow<List<String>>(emptyList())
val users: StateFlow<List<String>> = _users.asStateFlow()

fun addUser(name: String) {
    val currentUsers = _users.value.toMutableList()
    currentUsers.add(name)
    _users.value = currentUsers.toList()
}
```

This is boilerplate-heavy and error-prone. **ReactiveCollections** solves this by merging the collection and the 
`StateFlow` into a single, elegant object:

```kotlin
val users = reactiveListOf<String>()

// Just... modify the list directly. That's it! ✨
users.add("Alice")
```

Every mutation automatically and efficiently emits a new immutable snapshot to its collectors.

## 🚀 Quick Start

### 📦 Installation

```kotlin
// build.gradle.kts
dependencies {
    implementation("dev.shreyaspatil.reactivecollections:reactive-collections:1.0.0")
}
```

### Basic Usage

```kotlin
// Create reactive collections
val reactiveList = reactiveListOf("Apple", "Banana")
val reactiveMap = reactiveMapOf("key1" to "value1")
val reactiveSet = reactiveSetOf(1, 2, 3)

// Observe changes
reactiveList.asStateFlow().collect { list ->
    println("List updated: $list")
}

// Modify and see reactive updates - use any standard mutable collection operation
reactiveList.add("Cherry") // Triggers emission: [Apple, Banana, Cherry]
reactiveList.removeAt(0)   // Triggers emission: [Banana, Cherry]
```

## 📚 Core API

### Creating Reactive Collections

```kotlin
// Lists
val emptyList = reactiveListOf<String>()
val listWithItems = reactiveListOf("A", "B", "C")
val fromCollection = listOf("X", "Y").toMutableReactiveList()

// Maps
val emptyMap = reactiveMapOf<String, Int>()
val mapWithItems = reactiveMapOf("key1" to 1, "key2" to 2)
val fromMap = mapOf("a" to 1).toMutableReactiveMap()

// Sets
val emptySet = reactiveSetOf<Int>()
val setWithItems = reactiveSetOf(1, 2, 3)
val fromSet = setOf("x", "y").toMutableReactiveSet()
```

### Observing Changes

All reactive collections expose a `StateFlow` that emits immutable snapshots:

```kotlin
val list = reactiveListOf<String>()

// Basic observation
list.asStateFlow().collect { snapshot ->
    println("Current list: $snapshot")
}

// With lifecycle awareness (Android/Compose)
list.asStateFlow().collectAsState()
```

## 🎯 Advanced Features

### Batch Operations

If you need to perform multiple mutations at once, you can batch them to ensure the `StateFlow` only emits a single 
update after all operations are complete. This is crucial for performance-sensitive work, as it prevents multiple rapid 
computations.

Perform multiple operations with a single emission:

```kotlin
val list = reactiveListOf("A", "B", "C")

// Without batch - triggers 3 emissions
list.add("D")
list.add("E") 
list.removeAt(0)

// With batch - triggers only 1 emission
list.batchUpdate {
    add("D")
    add("E")
    removeAt(0)
}

// Async batch operations for suspending functions
list.batchUpdateAsync {
    // Suspending operations
    delay(100)
    add("F")
}
```

### 🛠Extension Functions

#### Lists: Observe Specific Indices

```kotlin
val list = reactiveListOf("A", "B", "C")

// Observe element at index 1
list.getAsFlow(1).collect { element ->
    println("Element at index 1: $element") // null if index doesn't exist
}

list[1] = "Modified" // Triggers emission: "Modified"
list.removeAt(1)     // Triggers emission: null
```

#### Lists: Observe Sublists

```kotlin
val list = reactiveListOf("A", "B", "C", "D", "E")

// Observe sublist [1, 4) - indices 1, 2, 3
list.subListAsFlow(1, 4).collect { sublist ->
    println("Sublist: $sublist")
}

// Strict mode (default): returns empty list if indices become invalid
list.subListAsFlow(1, 4, strict = true).collect { sublist -> /* ... */ }

// Lenient mode: coerces indices to valid range
list.subListAsFlow(1, 4, strict = false).collect { sublist -> /* ... */ }
```

#### Maps: Observe Specific Keys

```kotlin
val map = reactiveMapOf("user" to "John", "age" to "25")

// Observe value for specific key
map.valueAsFlow("user").collect { value ->
    println("User: $value") // null if key doesn't exist
}

map["user"] = "Jane"  // Triggers emission: "Jane"
map.remove("user")    // Triggers emission: null
```

## 🔧 Real-World Examples

### Android ViewModel with Reactive Collections

```kotlin
class TodoViewModel : ViewModel() {
    private val _todos = reactiveListOf<Todo>()
    val todos: StateFlow<List<Todo>> = _todos.asStateFlow()
    
    fun addTodo(todo: Todo) {
        _todos.add(todo)
    }
    
    fun updateTodos(updates: List<Todo>) {
        _todos.batchUpdate {
            clear()
            addAll(updates)
        }
    }
    
    fun toggleTodo(index: Int) {
        _todos[index] = _todos[index].copy(completed = !_todos[index].completed)
    }
}
```

### Real-time Data Synchronization

```kotlin
class DataRepository {
    private val _cache = reactiveMapOf<String, User>()
    val cache: StateFlow<Map<String, User>> = _cache.asStateFlow()
    
    suspend fun syncUsers() {
        val users = api.fetchUsers()
        _cache.batchUpdate {
            clear()
            users.forEach { user -> put(user.id, user) }
        }
    }
    
    fun observeUser(userId: String): Flow<User?> {
        return _cache.valueAsFlow(userId)
    }
}
```

and there can be many use cases...

## ❓ Frequently Asked Questions (FAQ)

### 1. Why not just use `MutableStateFlow<List<T>>`?

While that works, it requires manual state management: you must get the current list, create a modified copy, and then 
set that new copy as the flow's value. This is verbose and introduces boilerplate. ReactiveCollections handles this 
process automatically under the hood, giving you a much cleaner and more direct API.

### 2. What is the performance overhead?

This depends on the size of data which is going to be used within collection. For each mutation, the library creates a 
new immutable snapshot (`toList()`, `toSet()`, `toMap()`, etc.) to emit. For most lightweight workloads, this is 
negligible. For high-frequency, bulk operations, you should use the `batchUpdate` function to ensure only one snapshot 
is created after all mutations are complete.

### 3. Is it thread-safe?

Reactive Collections **does not** provide built-in thread safety. This design choice gives you full control over
concurrency. The state emission is atomic. When you modify the collection, the update to the underlying `StateFlow` is 
a single, atomic operation. This means collectors will never receive a partially updated or corrupt state snapshot. 
However, the underlying mutable collection (`MutableList`, `MutableSet`, etc.) is not synchronized for concurrent writes. 
If you plan to call mutation functions like `add()` or `remove()` from multiple threads simultaneously, you must provide 
your own external synchronization (e.g., using a `Mutex`) to prevent race conditions.

For typical use cases, like mutations from a single UI thread or a dedicated background dispatcher, this is perfectly 
safe.

**Example:**

```kotlin
// Option 1: Synchronize access yourself
val list = reactiveListOf<String>()
val mutex = Mutex()

suspend fun safeAdd(item: String) {
    mutex.withLock {
        list.add(item)
    }
}

// Option 2: Use thread-safe underlying collections (Example for JVM)
val list = Collections.synchronizedList(mutableListOf<String>()).toMutableReactiveList()
```

## 🤝 Contributing

Contributions are what make the open-source community such an amazing place to learn, inspire, and create. 
Any contributions you make are greatly appreciated. 

Please feel free to open an issue to discuss a feature or bug, or submit a pull request directly.

## 📄 License

```
Copyright 2025 Shreyas Patil

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
