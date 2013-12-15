package com.avast.steps;

import java.io.Closeable;
import java.util.Iterator;

/**
 * User: zslajchrt
 * Date: 12/1/13
 * Time: 10:57 PM
 */
public interface IteratorContext<T> {

    <I extends Iterator<T> & Closeable> void manageIterator(I iterator);

    void iterateAll(Iteration<T> iteration) throws Exception;

}