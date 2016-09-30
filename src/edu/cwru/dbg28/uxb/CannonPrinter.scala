package edu.cwru.dbg28.uxb

/**
  * EECS 293 HW5
  * Created by Daniel on 9/17/2016.
  * dbg28@case.edu
  *
  * A concrete Printer
  */
class CannonPrinter[A <: AbstractDevice.Builder[A]](builder: AbstractPrinter.Builder[A])
  extends AbstractPrinter[A](builder) {
  // Fields
  private final val STRINGMESSAGE_HEADER = "Cannon printer has printed the string: "
  private final val BINARYMESSAGE_HEADER = "Cannon printer has printed the binary message: "

  /** Receives a StringMessage and logs the appropriate response
    *
    * @param message   the StringMessage to send
    * @param connector the connector sending the message
    */
  override def recv(message: StringMessage, connector: Connector): Unit = {
    checkValid(message, connector, this)
    val s = STRINGMESSAGE_HEADER + message.getString + " " + getVersion
    println(s)
  }

  /** Receives a BinaryMessage and logs the appropriate response
    *
    * @param message   the BinaryMessage to send
    * @param connector the connector sending the message
    */
  override def recv(message: BinaryMessage, connector: Connector): Unit = {
    checkValid(message, connector, this)
    val s = BINARYMESSAGE_HEADER + (message.getValue * getSerialNumber.getOrElse(1))
    println(s)
  }
}

// Companion object for CannonPrinter -- holds Builder
object CannonPrinter {

  class Builder[A <: AbstractDevice.Builder[A]](version: Int) extends AbstractPrinter.Builder[A](version) {

    /** Builds a CannonPrinter from the current Builder
      *
      * @return the formed CannonPrinter
      */
    @throws(classOf[IllegalStateException]) // when validate() fails
    def build(): CannonPrinter[A] = {
      validate()
      new CannonPrinter[A](getThis)
    }

    /** Gets the current object
      *
      * @return this object
      */
    override protected def getThis: Builder[A] = this
  }

}