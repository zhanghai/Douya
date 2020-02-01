/*
 * Copyright (c) 2020 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.util

interface UntilRange<T : Comparable<T>> {
    val start: T

    val endExclusive: T

    operator fun contains(value: T): Boolean = value >= start && value < endExclusive

    fun isEmpty(): Boolean = start >= endExclusive
}

infix fun <T: Comparable<T>> T.comparableUntil(that: T): UntilRange<T> =
    ComparableUntilRange(this, that)

private class ComparableUntilRange<T : Comparable<T>>(
    override val start: T,
    override val endExclusive: T
) : UntilRange<T> {

    override fun equals(other: Any?): Boolean {
        return other is ComparableUntilRange<*> && (isEmpty() && other.isEmpty() || (start == other.start && endExclusive == other.endExclusive))
    }

    override fun hashCode(): Int {
        return if (isEmpty()) -1 else 31 * start.hashCode() + endExclusive.hashCode()
    }

    override fun toString(): String = "$start until $endExclusive"
}
