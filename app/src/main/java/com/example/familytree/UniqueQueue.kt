package com.example.familytree

/**
 * A queue that ensures uniqueness, meaning an element will not be added
 * if it is already present in the queue. It follows FIFO (First-In, First-Out) order.
 *
 * @param T The type of elements in the queue.
 */
class UniqueQueue<T> {
    private val queue = ArrayDeque<T>()
    private val set = mutableSetOf<T>()

    /**
     * Adds an item to the queue if it is not already present.
     *
     * @param item The item to add.
     * @return `true` if the item was added, `false` if it was already in the queue.
     */
    fun add(item: T): Boolean {
        return if (set.add(item)) { // Only add if not already in set
            queue.addLast(item)
            true
        } else {
            false
        }
    }

    /**
     * Retrieves and removes the head of the queue.
     *
     * @return The first item in the queue, or `null` if the queue is empty.
     */
    fun pull(): T? {
        return if (queue.isNotEmpty()) {
            val item = queue.removeFirst()
            set.remove(item)
            item
        } else {
            null
        }
    }

    /**
     * Retrieves, but does not remove, the head of the queue.
     *
     * @return The first item in the queue, or `null` if the queue is empty.
     */
    fun peek(): T? = queue.firstOrNull()

    /**
     * Checks if the queue is empty.
     *
     * @return `true` if the queue has no elements, `false` otherwise.
     */
    fun isEmpty(): Boolean = queue.isEmpty()

    /**
     * Checks if the queue is not empty.
     *
     * @return `true` if the queue has elements, `false` otherwise.
     */
    fun isNotEmpty(): Boolean = queue.isNotEmpty()

    /**
     * Returns the number of elements in the queue.
     *
     * @return The size of the queue.
     */
    fun size(): Int = queue.size
}
