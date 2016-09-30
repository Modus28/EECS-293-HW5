package edu.cwru.dbg28.uxb

/**
  * EECS 293 HW5
  * Created by Daniel Grigsby on 9/8/2016.
  * dbg28@case.edu
  */


/** Connector class - Integrates with devices, either a Computer or Peripheral
  *
  * @param index the plug number in the connector's device
  * @param aType the type of connector - can't be named type as type is a keyword
  */
final class Connector(private val device: Device, private val index: Integer, private val aType: Connector.Type.Value) {

  // Fields
  private var peer: Option[Connector] = None

  // Methods

  /** Gets the plug number in the connector's device
    *
    * @return the plug number
    */
  def getIndex: Integer = index

  /** Gets the Connector peer of this Connector
    *
    * @return the peer
    */
  def getPeer: Option[Connector] = peer

  /** Sets peer of this connector to input
    * Sets peer of peer to this
    *
    * @param peer the connector to set as this connector's peer
    */
  @throws(classOf[ConnectionException]) // if peer already exists, if peer type = input type, or makes a loop
  @throws(classOf[NullPointerException]) //  if connector is null
  def setPeer(peer: Connector): Unit = {
    validatePeer(peer)
    peer.validatePeer(this)
    this.peer = Option(peer)
    peer.peer = Option(this)
  }

  /** Validates the proposed peer of this connector
    *
    * @param peer the proposed peer to set
    */
  def validatePeer(peer: Connector): Unit = {
    if (Option(peer).isEmpty) {
      throw new NullPointerException
    }
    if (this.peer.isDefined) {
      throw new ConnectionException(ConnectionException.ErrorCode.CONNECTOR_BUSY, this)
    }
    if (this.peer.getOrElse(Connector.Type.INVALID).equals(peer.getType)) {
      throw new ConnectionException(ConnectionException.ErrorCode.CONNECTOR_MISMATCH, this)
    }
    if (getDevice.isReachable(peer.getDevice)) {
      throw new ConnectionException(ConnectionException.ErrorCode.CONNECTOR_CYCLE, this)
    }
    else{
      // Validation succeeded, nothing should happen
    }
  }

  /** Gets the Connector's Type
    *
    * @return the Type
    */
  def getType: Connector.Type.Value = aType

  /** Gets the device that the connector is part of
    *
    * @return the device containing the connector
    */
  def getDevice: Device = device

  /** Determines if a device is reachable by this connector's device
    *
    * @param device the device to check if is reachable
    * @return true if the device is reachable
    */
  def isReachable(device: Device): Boolean = {
    this.device.isReachable(device)
  }
}

/**
  * Connector Companion Object to hold the Enum
  */
object Connector {

  /** Type Enum -- Outside class because otherwise Constructor cannot access it.
    * Allows the connector to be specified as Peripheral or Computer
    */

  object Type extends Enumeration {
    type Type = Value
    val PERIPHERAL, COMPUTER, INVALID = Value
  }
}
