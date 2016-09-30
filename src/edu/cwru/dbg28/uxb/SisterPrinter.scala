package edu.cwru.dbg28.uxb

/**
  * EECS 293 HW5
  * Created by Daniel on 9/17/2016.
  * dbg28@case.edu
  *
  * A concrete Printer
  */
class SisterPrinter[A <: AbstractDevice.Builder[A]](builder: AbstractPrinter.Builder[A])
  extends AbstractPrinter[A](builder) {
  // Fields
  private final val STRINGMESSAGE_HEADER = "Sister printer has printed the string: "
  private final val BINARYMESSAGE_HEADER = "Sister printer has printed the binary message: "

  // Methods

  /** Receives a StringMessage and logs the appropriate response
    *
    * @param message   the StringMessage to send
    * @param connector the connector sending the message
    */
  override def recv(message: StringMessage, connector: Connector): Unit = {
    checkValid(message, connector, this)
    val s = STRINGMESSAGE_HEADER + message.getString + " " + getSerialNumber.getOrElse("")
    println(s)
  }

  /** Receives a BinaryMessage and logs the appropriate response
    *
    * @param message   the BinaryMessage to send
    * @param connector the connector sending the message
    */
  override def recv(message: BinaryMessage, connector: Connector): Unit = {
    checkValid(message, connector, this)
    val s = BINARYMESSAGE_HEADER + (message.getValue + (getProductCode match {
      case Some(i) => i
      case None => 0
    }))
    println(s)
  }
}

object SisterPrinter {

  class Builder[A <: AbstractDevice.Builder[A]](version: Int) extends AbstractPrinter.Builder[A](version) {

    /** Builds a SisterPrinter from the current Builder
      *
      * @return the formed SisterPrinter
      */
    @throws(classOf[IllegalStateException]) // when validate() fails
    def build(): SisterPrinter[A] = {
      validate()
      new SisterPrinter[A](getThis)
    }

    /** Gets the current object
      *
      * @return this object
      */
    override protected def getThis: Builder[A] = this
  }

}