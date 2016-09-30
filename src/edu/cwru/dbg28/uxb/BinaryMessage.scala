package edu.cwru.dbg28.uxb

/**
  * EECS 293 HW5
  * Created by Daniel Grigsby on 9/9/2016.
  * dbg28@case.edu
  *
  * BinaryMessage: Binary message sent between devices
  */
final class BinaryMessage(val value: BigInt) extends Message {

  /** Checks if two BinaryMessages are equal to each other
    *
    * @param anObject the object to compare to
    * @return the equality of the objects as a Boolean
    */
  override def equals(anObject: Any): Boolean = {
    Option(anObject).nonEmpty &&
      anObject.isInstanceOf[BinaryMessage] &&
      anObject.asInstanceOf[BinaryMessage].getValue.equals(this.getValue)
  }

  /** Gets the value of the BinaryMessage
    *
    * @return BinaryMessage value
    */
  def getValue: BigInt = value match {
    case null => 0
    case _ => value
  }
}
