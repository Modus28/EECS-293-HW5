package edu.cwru.dbg28
import scala.language.implicitConversions

/**
  * EECS 293 HW5
  * Created by Daniel on 9/17/2016.
  * dbg28@case.edu
  *
  * Implicit definition will be stored here
  */
package object uxb {
  // Definition: Let StringMessage act like a String, delegates String method calls to String field
  implicit def delegateToValue(d: StringMessage): String = new String(d.getString)
}
