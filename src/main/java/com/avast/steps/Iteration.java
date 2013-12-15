package com.avast.steps;

import java.util.Iterator;

/**
 * User: zslajchrt
 * Date: 12/1/13
 * Time: 11:20 PM
 */
public interface Iteration<T> {

    void iterate(Iterator<T> concatenatedIterator);

}
