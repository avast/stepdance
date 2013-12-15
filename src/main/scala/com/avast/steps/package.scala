package com.avast

/**
 * User: zslajchrt
 * Date: 11/27/13
 * Time: 9:30 PM
 */
package object steps {

  type Steps[+T] = () => Step[T]

}

