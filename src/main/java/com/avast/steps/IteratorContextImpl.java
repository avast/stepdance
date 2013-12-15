package com.avast.steps;

import java.io.Closeable;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * User: zslajchrt
 * Date: 12/1/13
 * Time: 10:57 PM
 */
public class IteratorContextImpl<T> implements IteratorContext<T>, Closeable {

    private final List<Iterator<T>> iterators = new LinkedList<Iterator<T>>();

    @Override
    public <I extends Iterator<T> & Closeable> void manageIterator(I iterator) {
       iterators.add(new ManagedIterator<T>(iterator));
    }

    @Override
    public void iterateAll(Iteration<T> iteration) throws Exception {
        Iterator<T> concatenated = null; // todo: use Guava or so
        try {
            iteration.iterate(concatenated);
        } finally {
            close();
        }
    }

    @Override
    public void close() throws IOException {
        for (Iterator<T> iterator : iterators) {
            try {
                ((Closeable)iterator).close();
            } catch (Throwable t){
                t.printStackTrace();
            }
        }
    }
}