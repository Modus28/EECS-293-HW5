package edu.cwru.dbg28.uxb

import edu.cwru.dbg28.uxb.DeviceClass.DeviceClass

/**
  * EECS 293 HW5
  * Created by Daniel on 9/10/2016.
  * dbg28@case.edu
  *
  * Hub: A specific form of an AbstractDevice
  */
class Hub[A <: AbstractDevice.Builder[A]] private(builder: Hub.Builder[A]) extends AbstractDevice[A](builder) {
  // Fields
  private final val STRINGMESSAGE_HEADER = "recv not yet supported"
  private final val BINARYMESSAGE_HEADER = "recv not yet supported"

  // Methods

  /** Gets the UXB device class
    *
    * @return the class of the device
    */
  override def getDeviceClass: DeviceClass = DeviceClass.HUB


  /** Receives a StringMessage and logs the appropriate response
    *
    * @param message   the StringMessage to send
    * @param connector the connector sending the message
    */
  override def recv(message: StringMessage, connector: Connector): Unit = {
    checkValid(message, connector, this)
    println(STRINGMESSAGE_HEADER)
  }

  /** Receives a BinaryMessage and logs the appropriate response
    *
    * @param message   the BinaryMessage to send
    * @param connector the connector sending the message
    */
  override def recv(message: BinaryMessage, connector: Connector): Unit = {
    checkValid(message, connector, this)
    println(BINARYMESSAGE_HEADER)
  }
}

// Companion object for Hub -- holds Builder
object Hub {

  /** Hub Builder
    * Forms AbstractDevices - Specifically Hubs
    */
  class Builder[A <: AbstractDevice.Builder[A]](version: Int) extends AbstractDevice.Builder[A](version) {

    /** Builds a hub from the current Hub.Builder
      *
      * @return the formed Hub
      */
    @throws (classOf[IllegalStateException]) // when validate() fails
    def build(): Hub[A] = {
      validate()
      new Hub[A](getThis)
    }

    /** Gets the current object
      *
      * @return this object
      */
    override protected def getThis = this

    /** Checks if version number is null
      */
    @throws (classOf[IllegalStateException]) // when version # is invalid, or missing computer/peripheral connection
    override protected def validate(): Unit = {
      if (Option(version).isEmpty) {
        throw new IllegalStateException("Version number has no value")
      }
      else if (!getConnectors.exists(_.equals(Connector.Type.COMPUTER))) {
        throw new IllegalStateException("Has no Computer Connector")
      }
      else if (!getConnectors.exists(_.equals(Connector.Type.PERIPHERAL))){
        throw new IllegalStateException("Has no Peripheral Connector")
      }
      else {
        // If the validation is successful, nothing needs to be done
      }
    }
  }
}
