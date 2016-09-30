package edu.cwru.dbg28.uxb

/**
  * EECS 293 HW%
  * Created by Daniel on 9/17/2016.
  * dbg28@case.edu
  *
  * A concrete Printer
  */
class GoAmateur[A <: AbstractDevice.Builder[A]](builder: GoAmateur.Builder[A]) extends AbstractVideo[A](builder) {
  // Fields
  private final val STRINGMESSAGE_HEADER = "GoAmateur does not understand string messages: "
  private final val BINARYMESSAGE_HEADER = "GoAmateur is not yet active: "

  // Methods

  /** Receives a StringMessage and logs the appropriate response
    *
    * @param message   the StringMessage to send
    * @param connector the connector sending the message
    */
  override def recv(message: StringMessage, connector: Connector): Unit = {
    checkValid(message, connector, this)
    val s = STRINGMESSAGE_HEADER + message.getString + " " + getConnectors.indexOf(connector)
    println(s)
  }

  /** Receives a BinaryMessage and logs the appropriate response
    *
    * @param message   the BinaryMessage to send
    * @param connector the connector sending the message
    */
  override def recv(message: BinaryMessage, connector: Connector): Unit = {
    checkValid(message, connector, this)
    val s = BINARYMESSAGE_HEADER + message.getValue
    println(s)
  }
}

// Companion object for GoAmateur -- holds Builder
object GoAmateur {

  /** GoAmateur Builder
    * Forms AbstractDevices - Specifically GoAmateurs
    */
  class Builder[A <: AbstractDevice.Builder[A]](version: Int) extends AbstractVideo.Builder[A](version) {

    /** Builds a GoAmateur from the current Builder
      *
      * @return the formed GoAmateur
      */
    @throws(classOf[IllegalStateException]) // when validate() fails
    def build(): GoAmateur[A] = {
      validate()
      new GoAmateur[A](getThis)
    }

    /** Gets the current object
      *
      * @return this object
      */
    override protected def getThis: Builder[A] = this


  }

}