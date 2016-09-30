package edu.cwru.dbg28.uxb

/**
  * EECS 293 HW5
  * Created by Daniel Grigsby on 9/10/2016.
  * dbg28@case.edu
  */


import edu.cwru.dbg28.uxb.DeviceClass._

/** Device Interface - provides functionality to all devices
  */
trait Device {
  // Method stubs

  /** Receives a StringMessage and logs the appropriate response
    *
    * @param message   the StringMessage to send
    * @param connector the connector sending the StringMessage
    */
  def recv(message: StringMessage, connector: Connector): Unit

  /** Receives a BinaryMessage and logs the appropriate response
    *
    * @param message   the BinaryMessage to send
    * @param connector the connector sending the BinaryMessage
    */
  def recv(message: BinaryMessage, connector: Connector): Unit

  /** Distributes messages sent to appropriate sub methods
    *
    * @param message the Message of unknown concrete type
    * @param connector the connector sending the Message
    */
   def recv(message: Message, connector: Connector): Unit

  /** Gets the product code of the device
    *
    * @return the product code
    */
  def getProductCode: Option[Int]

  /** Gets the serial number for the device if it exists
    *
    * @return serial number or empty Option
    */
  def getSerialNumber: Option[BigInt]

  /** Gets UXB version the device supports
    *
    * @return UXB version
    */
  def getVersion: Int

  /** Gets the UXB device class
    *
    * @return the class of the device
    */
  def getDeviceClass: DeviceClass

  /** Gets the number of connectors
    *
    * @return the connector count
    */
  def getConnectorCount: Int

  /** Gets the type of all the connectors
    *
    * @return list of connector types
    */
  def getConnectors: List[Connector]

  /** Gets the connector at an index
    *
    * @param index the index of the connector
    * @return connector at the index spot in the list
    */
  def getConnector(index: Int): Connector

  /** Gets devices that this device is connected to directly via a connector
    *
    * @return Set of peer devices
    */
  def peerDevices: Set[Device]

  /** Gets devices that are reachable either directly or indirectly accessible
    *
    * @return Set of reachable devices
    */
  def reachableDevices: Set[Device]

  /** Determines if a device is reachable by this device
    *
    * @param device the device to check if is reachable
    * @return true if the device is reachable
    */
  def isReachable(device: Device): Boolean

  /** Sets the location status of the object
    *
    */
}
