package com.avast.steps;

import java.io.Closeable;
import java.io.IOException;
import java.util.Iterator;

/**
 * User: zslajchrt
 * Date: 12/1/13
 * Time: 12:12 PM
 */
public final class StepJava {

    public static <T> StepFunction<T> fromIterator(final Iterator<T> iterator) {
        return fromIterator(iterator, NULL_CLOSEABLE);
    }

    public static <T> StepFunction<T> fromIterator(final Iterator<T> iterator, final Closeable closeable) {
        return new StepFunction<T>() {
            @Override
            public Step<T> apply() throws Throwable {
                if (!iterator.hasNext()) {
                    return noStep();
                } else {
                    T next = iterator.next();
                    if (iterator.hasNext()) {
                        return new NextStep<T>(next, this, closeable);
                    } else {
                        return new FinalStep<T>(next);
                    }
                }
            }
        };
    }

    public static abstract class Step<T> implements Closeable {
        private Step() {
        }

        public abstract Step<T> next() throws Throwable;

        public abstract StepType getType();

        public void foreach(Function1V<T> f) throws Throwable {
            Step<T> step = this;
            while (true) {
                switch (step.getType()) {
                    case NoStep: {
                        return;
                    }
                    case FinalStep: {
                        f.apply(((FinalStep<T>) step).getValue());
                        return;
                    }
                    case NextStep: {
                        NextStep<T> nextStep = (NextStep<T>) step;
                        try {
                            f.apply(nextStep.getValue());
                        } catch (Throwable t) {
                            nextStep.close();
                            throw t;
                        }
                        step = step.next();
                    }
                }
            }
        }
    }

    public static final class NoStep<T> extends Step<T> {

        private NoStep() {
        }

        @Override
        public StepType getType() {
            return StepType.NoStep;
        }

        @Override
        public Step<T> next() throws Throwable {
            return this;
        }

        @Override
        public void close() throws IOException {
        }
    }

    public static final NoStep NO_STEP = new NoStep();

    public static <T> NoStep<T> noStep() {
        //noinspection unchecked
        return NO_STEP;
    }

    public interface HavingValue<T> {
        T getValue();
    }

    public static final class FinalStep<T> extends Step<T> implements HavingValue<T> {

        private final T value;

        public FinalStep(T value) {
            this.value = value;
        }

        @Override
        public T getValue() {
            return value;
        }

        @Override
        public Step<T> next() throws Throwable {
            //noinspection unchecked
            return NO_STEP;
        }

        @Override
        public StepType getType() {
            return StepType.FinalStep;
        }

        @Override
        public void close() throws IOException {
        }
    }

    public static final class NextStep<T> extends Step<T> implements HavingValue<T> {

        private final T value;
        private final Function0<Step<T>> stepFunction;
        private final Closeable closeable;

        public NextStep(T value, Function0<Step<T>> stepFunction, Closeable closeable) {
            this.value = value;
            this.stepFunction = new NextStepFnWrapper(stepFunction);
            this.closeable = closeable;
        }

        @Override
        public T getValue() {
            return value;
        }

        public Function0<Step<T>> getStepFunction() {
            return stepFunction;
        }

        @Override
        public Step<T> next() throws Throwable {
            return stepFunction.apply();
        }

        @Override
        public StepType getType() {
            return StepType.NextStep;
        }

        @Override
        public void close() throws IOException {
            closeable.close();
        }

        class NextStepFnWrapper implements StepFunction<T> {
            private final Function0<Step<T>> stepFunction;

            NextStepFnWrapper(Function0<Step<T>> stepFunction) {
                this.stepFunction = stepFunction;
            }

            @Override
            public Step<T> apply() throws Throwable {
                Step<T> nextStep;
                try {
                    nextStep = stepFunction.apply();
                } catch (Throwable t) {
                    closeable.close();
                    throw t;
                }
                if (nextStep == null) {
                    nextStep = noStep();
                }
                switch (nextStep.getType()) {
                    case NoStep: {
                        close();
                        break;
                    }
                    case FinalStep: {
                        close();
                        break;
                    }
                    case NextStep: {
                        break;
                    }
                }
                return nextStep;
            }
        }
    }

    public interface Function0<R> {
        R apply() throws Throwable;
    }

    public interface StepFunction<R> extends Function0<Step<R>> {
        Step<R> apply() throws Throwable;
    }

    public interface Function1V<T> {
        void apply(T arg) throws Throwable;
    }

    public interface Function1<T, R> {
        R apply(T arg) throws Throwable;
    }

    public interface Function2<T1, T2, R> {
        R apply(T1 arg1, T2 arg2) throws Throwable;
    }

    public enum StepType {
        NoStep, FinalStep, NextStep
    }

    public static final Closeable NULL_CLOSEABLE = new Closeable() {
        @Override
        public void close() throws IOException {
        }
    };

}
