/*
 * Copyright (c) 2017 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.util;

import java.util.Comparator;
import java.util.Objects;

@SuppressWarnings("unused")
public interface FunctionCompat {

    @FunctionalInterface
    interface BiConsumer<T, U> {

        void accept(T t, U u);

        default BiConsumer<T, U> andThen(BiConsumer<? super T, ? super U> after) {
            Objects.requireNonNull(after);
            return (l, r) -> {
                accept(l, r);
                after.accept(l, r);
            };
        }
    }

    @FunctionalInterface
    interface BiFunction<T, U, R> {

        R apply(T t, U u);

        default <V> BiFunction<T, U, V> andThen(Function<? super R, ? extends V> after) {
            Objects.requireNonNull(after);
            return (T t, U u) -> after.apply(apply(t, u));
        }
    }

    @FunctionalInterface
    interface BinaryOperator<T> extends BiFunction<T, T, T> {

        static <T> BinaryOperator<T> minBy(Comparator<? super T> comparator) {
            Objects.requireNonNull(comparator);
            return (a, b) -> comparator.compare(a, b) <= 0 ? a : b;
        }

        static <T> BinaryOperator<T> maxBy(Comparator<? super T> comparator) {
            Objects.requireNonNull(comparator);
            return (a, b) -> comparator.compare(a, b) >= 0 ? a : b;
        }
    }

    @FunctionalInterface
    interface BiPredicate<T, U> {

        boolean test(T t, U u);

        default BiPredicate<T, U> and(BiPredicate<? super T, ? super U> other) {
            Objects.requireNonNull(other);
            return (T t, U u) -> test(t, u) && other.test(t, u);
        }

        default BiPredicate<T, U> negate() {
            return (T t, U u) -> !test(t, u);
        }

        default BiPredicate<T, U> or(BiPredicate<? super T, ? super U> other) {
            Objects.requireNonNull(other);
            return (T t, U u) -> test(t, u) || other.test(t, u);
        }
    }

    @FunctionalInterface
    interface BooleanSupplier {

        boolean getAsBoolean();
    }

    @FunctionalInterface
    interface Consumer<T> {

        void accept(T t);

        default Consumer<T> andThen(Consumer<? super T> after) {
            Objects.requireNonNull(after);
            return (T t) -> { accept(t); after.accept(t); };
        }
    }

    @FunctionalInterface
    interface DoubleBinaryOperator {

        double applyAsDouble(double left, double right);
    }

    @FunctionalInterface
    interface DoubleConsumer {

        void accept(double value);

        default DoubleConsumer andThen(DoubleConsumer after) {
            Objects.requireNonNull(after);
            return (double t) -> { accept(t); after.accept(t); };
        }
    }

    @FunctionalInterface
    interface DoubleFunction<R> {

        R apply(double value);
    }

    @FunctionalInterface
    interface DoublePredicate {

        boolean test(double value);

        default DoublePredicate and(DoublePredicate other) {
            Objects.requireNonNull(other);
            return (value) -> test(value) && other.test(value);
        }

        default DoublePredicate negate() {
            return (value) -> !test(value);
        }

        default DoublePredicate or(DoublePredicate other) {
            Objects.requireNonNull(other);
            return (value) -> test(value) || other.test(value);
        }
    }

    @FunctionalInterface
    interface DoubleSupplier {

        double getAsDouble();
    }

    @FunctionalInterface
    interface DoubleToIntFunction {

        int applyAsInt(double value);
    }

    @FunctionalInterface
    interface DoubleToLongFunction {

        long applyAsLong(double value);
    }

    @FunctionalInterface
    interface DoubleUnaryOperator {

        double applyAsDouble(double operand);

        default DoubleUnaryOperator compose(DoubleUnaryOperator before) {
            Objects.requireNonNull(before);
            return (double v) -> applyAsDouble(before.applyAsDouble(v));
        }

        default DoubleUnaryOperator andThen(DoubleUnaryOperator after) {
            Objects.requireNonNull(after);
            return (double t) -> after.applyAsDouble(applyAsDouble(t));
        }

        static DoubleUnaryOperator identity() {
            return t -> t;
        }
    }

    @FunctionalInterface
    interface Function<T, R> {

        R apply(T t);

        default <V> Function<V, R> compose(Function<? super V, ? extends T> before) {
            Objects.requireNonNull(before);
            return (V v) -> apply(before.apply(v));
        }

        default <V> Function<T, V> andThen(Function<? super R, ? extends V> after) {
            Objects.requireNonNull(after);
            return (T t) -> after.apply(apply(t));
        }

        static <T> Function<T, T> identity() {
            return t -> t;
        }
    }

    @FunctionalInterface
    interface IntBinaryOperator {

        int applyAsInt(int left, int right);
    }

    @FunctionalInterface
    interface IntConsumer {

        void accept(int value);

        default IntConsumer andThen(IntConsumer after) {
            Objects.requireNonNull(after);
            return (int t) -> { accept(t); after.accept(t); };
        }
    }

    @FunctionalInterface
    interface IntFunction<R> {

        R apply(int value);
    }

    @FunctionalInterface
    interface IntPredicate {

        boolean test(int value);

        default IntPredicate and(IntPredicate other) {
            Objects.requireNonNull(other);
            return (value) -> test(value) && other.test(value);
        }

        default IntPredicate negate() {
            return (value) -> !test(value);
        }

        default IntPredicate or(IntPredicate other) {
            Objects.requireNonNull(other);
            return (value) -> test(value) || other.test(value);
        }
    }

    @FunctionalInterface
    interface IntSupplier {

        int getAsInt();
    }

    @FunctionalInterface
    interface IntToDoubleFunction {

        double applyAsDouble(int value);
    }

    @FunctionalInterface
    interface IntToLongFunction {

        long applyAsLong(int value);
    }

    @FunctionalInterface
    interface IntUnaryOperator {

        int applyAsInt(int operand);

        default IntUnaryOperator compose(IntUnaryOperator before) {
            Objects.requireNonNull(before);
            return (int v) -> applyAsInt(before.applyAsInt(v));
        }

        default IntUnaryOperator andThen(IntUnaryOperator after) {
            Objects.requireNonNull(after);
            return (int t) -> after.applyAsInt(applyAsInt(t));
        }

        static IntUnaryOperator identity() {
            return t -> t;
        }
    }

    @FunctionalInterface
    interface LongBinaryOperator {

        long applyAsLong(long left, long right);
    }

    @FunctionalInterface
    interface LongConsumer {

        void accept(long value);

        default LongConsumer andThen(LongConsumer after) {
            Objects.requireNonNull(after);
            return (long t) -> { accept(t); after.accept(t); };
        }
    }

    @FunctionalInterface
    interface LongFunction<R> {

        R apply(long value);
    }

    @FunctionalInterface
    interface LongPredicate {

        boolean test(long value);

        default LongPredicate and(LongPredicate other) {
            Objects.requireNonNull(other);
            return (value) -> test(value) && other.test(value);
        }

        default LongPredicate negate() {
            return (value) -> !test(value);
        }

        default LongPredicate or(LongPredicate other) {
            Objects.requireNonNull(other);
            return (value) -> test(value) || other.test(value);
        }
    }

    @FunctionalInterface
    interface LongSupplier {

        long getAsLong();
    }

    @FunctionalInterface
    interface LongToDoubleFunction {

        double applyAsDouble(long value);
    }

    @FunctionalInterface
    interface LongToIntFunction {

        int applyAsInt(long value);
    }

    @FunctionalInterface
    interface LongUnaryOperator {

        long applyAsLong(long operand);

        default LongUnaryOperator compose(LongUnaryOperator before) {
            Objects.requireNonNull(before);
            return (long v) -> applyAsLong(before.applyAsLong(v));
        }

        default LongUnaryOperator andThen(LongUnaryOperator after) {
            Objects.requireNonNull(after);
            return (long t) -> after.applyAsLong(applyAsLong(t));
        }

        static LongUnaryOperator identity() {
            return t -> t;
        }
    }

    @FunctionalInterface
    interface ObjDoubleConsumer<T> {

        void accept(T t, double value);
    }

    @FunctionalInterface
    interface ObjIntConsumer<T> {

        void accept(T t, int value);
    }

    @FunctionalInterface
    interface ObjLongConsumer<T> {

        void accept(T t, long value);
    }

    @FunctionalInterface
    interface Predicate<T> {

        boolean test(T t);

        default Predicate<T> and(Predicate<? super T> other) {
            Objects.requireNonNull(other);
            return t -> test(t) && other.test(t);
        }

        default Predicate<T> negate() {
            return t -> !test(t);
        }

        default Predicate<T> or(Predicate<? super T> other) {
            Objects.requireNonNull(other);
            return t -> test(t) || other.test(t);
        }

        static <T> Predicate<T> isEqual(Object targetRef) {
            return targetRef == null
                    ? ObjectsCompat::isNull
                    : targetRef::equals;
        }
    }

    @FunctionalInterface
    interface Supplier<T> {

        T get();
    }

    @FunctionalInterface
    interface ToDoubleBiFunction<T, U> {

        double applyAsDouble(T t, U u);
    }

    @FunctionalInterface
    interface ToDoubleFunction<T> {

        double applyAsDouble(T value);
    }

    @FunctionalInterface
    interface ToIntBiFunction<T, U> {

        int applyAsInt(T t, U u);
    }

    @FunctionalInterface
    interface ToIntFunction<T> {

        int applyAsInt(T value);
    }

    @FunctionalInterface
    interface ToLongBiFunction<T, U> {

        long applyAsLong(T t, U u);
    }

    @FunctionalInterface
    interface ToLongFunction<T> {

        long applyAsLong(T value);
    }

    @FunctionalInterface
    interface UnaryOperator<T> extends Function<T, T> {
        static <T> UnaryOperator<T> identity() {
            return t -> t;
        }
    }
}
