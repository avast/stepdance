package com.avast.steps;

import java.io.Closeable;
import java.io.IOException;
import java.util.Iterator;

/**
 * User: zslajchrt
 * Date: 12/1/13
 * Time: 11:23 PM
 */
class ManagedIterator<T> implements Iterator<T>, Closeable {

    private final Iterator<T> iterator;

    ManagedIterator(Iterator<T> iterator) {
        this.iterator = iterator;
    }

    @Override
    public T next() {
        return null;
    }

    @Override
    public boolean hasNext() {
        return false;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void close() throws IOException {
        ((Closeable)iterator).close();
    }
}
