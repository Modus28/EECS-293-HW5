package edu.cwru.dbg28.uxb

/**
  * EECS 293 HW5
  * Created by Daniel on 9/17/2016.
  * dbg28@case.edu
  */
class StringMessage(private val value: String) extends Message {

  // Methods

  override def hashCode(): Int = value.hashCode

  /** Checks if two StringMessages are equal to each other
    *
    * @param anObject the object to compare to
    * @return the equality of the objects as a Boolean
    */
  override def equals(anObject: Any): Boolean = {
    Option(anObject).nonEmpty &&
      anObject.isInstanceOf[StringMessage] &&
      anObject.asInstanceOf[StringMessage].getString.equals(this.getString)
  }

  /** Gets the value of the StringMessage
    *
    * @return the value
    */
  def getString = value match {
    case null => ""
    case _ => value
  }
}

