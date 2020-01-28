/*
 * Copyright (c) 2020 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.arch

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer

class EventLiveData<T> : MutableLiveData<T>() {
    private val eventObservers = mutableMapOf<Observer<in T>, EventObserver<T>>()

    override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {
        val eventObserver = eventObservers.getOrPut(observer) { EventObserver(observer) }
        super.observe(owner, eventObserver)
    }

    override fun observeForever(observer: Observer<in T>) {
        val eventObserver = eventObservers.getOrPut(observer) { EventObserver(observer) }
        super.observeForever(eventObserver)
    }

    override fun removeObserver(observer: Observer<in T>) {
        if (observer is EventObserver) {
            eventObservers.values.removeAll { it == observer }
            super.removeObserver(observer)
        } else {
            val eventObserver = eventObservers[observer] ?: return
            super.removeObserver(eventObserver)
        }
    }

    override fun setValue(value: T) {
        eventObservers.values.forEach { it.expectValue() }
        super.setValue(value)
    }

    private class EventObserver<T>(private val observer: Observer<in T>) : Observer<T> {
        private var expected = false

        override fun onChanged(value: T) {
            if (!expected) {
                return
            }
            observer.onChanged(value)
            expected = false
        }

        fun expectValue() {
            expected = true
        }
    }
}
