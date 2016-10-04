package edu.cwru.dbg28.uxb
import scala.annotation.tailrec

/**
  * EECS 293 HW5
  * Created by Daniel Grigsby on 9/10/2016.
  * dbg28@case.edu
  *
  * AbstractDevice:  A device type that adds functionality to the Device Interface
  */
abstract class AbstractDevice[A <: AbstractDevice.Builder[A]] private() extends Device {

  // Fields with default values
  private var productCode: Option[Int] = None
  private var serialNumber: Option[BigInt] = None
  private var connectors: List[Connector] = List[Connector]()
  private var connectorTypes: List[Connector.Type.Value] = Nil
  private var version: Int = -1

  // Methods

  /** Gets the type of all the connectors
    *
    * @return list of connector types
    */
  override def getConnectors: List[Connector] = this.connectors

  /** Gets types of connectors
    *
    * @return list of connector types
    */
  def getConnectorTypes: List[Connector.Type.Value] = connectorTypes

  /** Gets the product code of the device
    *
    * @return the product code
    */
  override def getProductCode: Option[Int] = this.productCode

  /** Gets the serial number for the device if it exists
    *
    * @return serial number or empty Option
    */
  override def getSerialNumber: Option[BigInt] = this.serialNumber

  /** Gets the connector at an index
    *
    * @param index the index of the connector
    * @return connector at the index spot in the list
    */
  override def getConnector(index: Int): Connector = this.connectors(index)

  /** Gets the UXB device class
    *
    * @return the class of the device
    */
  override def getDeviceClass: DeviceClass.Value = DeviceClass.UNSPECIFIED

  /** Gets UXB version the device supports
    *
    * @return UXB version
    */
  override def getVersion: Int = version

  /** Gets the number of connectors
    *
    * @return the connector count
    */
  override def getConnectorCount: Int = this.connectors.length

  /** Receives a StringMessage and logs the appropriate response
    *
    * @param message   the StringMessage to send
    * @param connector the connector sending the message
    */
  override def recv(message: StringMessage, connector: Connector): Unit

  /** Receives a BinaryMessage and logs the appropriate response
    *
    * @param message   the BinaryMessage to send
    * @param connector the connector sending the message
    */
  override def recv(message: BinaryMessage, connector: Connector): Unit


  /** Distributes messages sent to appropriate sub methods
    *
    * @param message the Message of unknown concrete type
    * @param connector the connector sending the Message
    */
  override def recv(message: Message, connector: Connector): Unit = {
    message match{
      case s: StringMessage => recv(s, connector)
      case b: BinaryMessage => recv(b, connector)
      case _ => throw new IllegalArgumentException("Invalid Message sent")
    }
  }

  /** Communicates between devices, if message is valid to send
    *
    * @param conIndex connector to send message on
    * @param message message to send
    */
  def communicate(conIndex: Int, message: Message): Unit = {
    checkValid(message, getConnector(conIndex), this)
    val peer: Option[Connector] = getConnector(conIndex).getPeer
    if(peer.isDefined) {
      peer.get.getDevice.recv(message, peer.get)
    }
  }

  /** Checks if the parameters for a message are valid
    *
    * @param message   the message being sent
    * @param connector the connector sending the message
    * @param device    the device receiving the message
    */
  @throws(classOf[NullPointerException]) // if connector or message input are invalid
  @throws(classOf[IllegalArgumentException]) // if connector does not belong to device
  def checkValid(message: Message, connector: Connector, device: AbstractDevice[A]): Unit = {
    if (Option(message).isEmpty || Option(connector).isEmpty) {
      throw new NullPointerException("Either the message sent or the recipient connector was null")
    }
    if (!device.getConnectors.contains(connector)) {
      throw new IllegalArgumentException("The connector does not belong to this device")
    }
    else {
      // Validation successful
    }
  }

  /** Gets devices that this device is connected to directly via a connector
    *
    * @return Set of peer devices
    */
  override def peerDevices: Set[Device] = getSpecificPeerDevices(this)


  /** Gets devices that the input device is connected to directly via a connector
    *
    * @return Set of peer devices
    */
  def getSpecificPeerDevices(device: Device): Set[Device] = device.getConnectors.flatMap(_.getPeer).map(_.getDevice).toSet

  /** Gets devices that are reachable either directly or indirectly accessible from this device
    *
    * @return Set of reachable devices
    */
  override def reachableDevices: Set[Device] = deviceFinder(peerDevices)


  /** Recursive method to get all devices directly and indirectly accessible from a set of devices
    * Sets all devices to the location status of the location parameter
    * Tail Recursive, breadth first search
    *
    * @param devices the set of devices to search
    * @return the set of all accessible devices
    */
  private def deviceFinder(devices: Set[Device]): Set[Device] = {
    @tailrec
    def subLoop(deviceSet: Set[Device], accumulator: Set[Device]): Set[Device] = {
      val temp: Set[Device] = getFilteredPeers(deviceSet, (x: Device) => !accumulator.contains(x))
      if (temp.isEmpty) {
        accumulator
      }
      else
        subLoop(temp, accumulator ++ temp)
    }
    subLoop(devices, devices)
  }

  /** Determines if a device is reachable by this device
    * Tail Recursive, breadth first search of all accessible devices
    *
    * @param device the device to check if is reachable
    * @return true if the device is reachable
    */
  override def isReachable(device: Device): Boolean = {
    @tailrec
    def subLoop(devicesInLoop: Set[Device], accumulator: Set[Device]): Boolean = {
      if (devicesInLoop.contains(device)){
        true
      }
      else if (devicesInLoop.isEmpty) {
        false
      }
      else {
         subLoop(getFilteredPeers(devicesInLoop, (x: Device) => !accumulator.contains(x)), accumulator ++ devicesInLoop)
      }
    }
    subLoop(this.peerDevices + this, Set[Device]())
  }

  /** Gets the peer devices of all devices in a set, filters them based on a function parameter
    *
    * @param devicesToFilter the set of devices to filter
    * @param f the function to filter by
    * @return devices that pass the filter function
    */
  private def getFilteredPeers(devicesToFilter: Set[Device], f: Device => Boolean): Set[Device] = {
    for (device <- devicesToFilter ; q <- device.peerDevices if f(q)) yield q
  }

  // Constructor -  Initializes class with builder input
  protected def this(builder: AbstractDevice.Builder[A]) {
    this
    this.version = builder.getVersion
    this.productCode = builder.getProductCode
    this.serialNumber = builder.getSerialNumber
    this.connectorTypes = builder.getConnectorTypes
    createConnectorList(connectorTypes)
  }


  /** Adds Connectors equivalent to the ConnectorTypes to the device.
    *
    * @param connectorTypes connector types to convert to connectors
    */
  private def createConnectorList(connectorTypes: List[Connector.Type.Value]): Unit = {
    for(index <- connectorTypes.indices){
      connectors = connectors ::: List(new Connector(this, index, connectorTypes(index)))
    }
  }

  /** Simplified tool for connecting two devices
    *
    * @param device the device to connect to this
    * @param thisConnectorIndex the index of this device's connector to connect with
    * @param oppositeConnectorIndex the index of the input device's connector to connect with
    */
  def connect(device: Device, thisConnectorIndex: Int, oppositeConnectorIndex: Int): Unit ={
    if (!(this.getConnectors.indices.contains(thisConnectorIndex) && device.getConnectors.indices.contains(oppositeConnectorIndex))) {
      throw new IllegalArgumentException("You have chosen invalid connector indexes")
    }
    else {
      this.getConnector(thisConnectorIndex).setPeer(device.getConnector(oppositeConnectorIndex))
    }
  }
}

// Companion object with Builder

object AbstractDevice {

  /** Abstract Device Builder
    * Forms AbstractDevices
    */
  abstract class Builder[A <: AbstractDevice.Builder[A]](private val version: Int) {

    // Fields
    private var productCodeVal: Option[Int] = None
    private var serialNumberVal: Option[BigInt] = None
    private var connectorList: List[Connector] = Nil
    private var connectorTypes: List[Connector.Type.Value] = Nil

    // Public Methods

    /** Sets the product code to input, returns this
      *
      * @param productCode the product code to set
      * @return the current object
      */
    def productCode(productCode: Int): Builder[A] = {
      this.productCodeVal = Option(productCode)
      getThis
    }

    /** Sets the serial number to input, returns this
      *
      * @param serialNumber the serial number to set
      * @return the current object
      */
    def serialNumber(serialNumber: BigInt): Builder[A] = {
      this.serialNumberVal = Option(serialNumber)
      getThis
    }

    /** Sets the connector types to a copy of the input
      *
      * @param connectors the connector type list to set
      * @return the current object
      */
    def connectors(connectors: List[Connector.Type.Value]): Builder[A] = {
      this.connectorTypes = connectors.map(n => n)
      getThis
    }

    /** Sets the connector list to a copy of the input
      *
      * @param connectorList the connector list to set
      * @return the current object
      */
    def setConnectors(connectorList: List[Connector]): Builder[A] = {
      this.connectorList = connectorList.map(n => n)
      getThis
    }


    // Getter Methods - No documentation
    def getProductCode: Option[Int] = productCodeVal

    def getSerialNumber: Option[BigInt] = serialNumberVal

    def getVersion: Int = version

    def getConnectorList: List[Connector] = connectorList

    def getConnectorTypes: List[Connector.Type.Value] = connectorTypes


    // Protected Methods
    def build(): AbstractDevice[A]

    /** Gets the current object
      *
      * @return this object
      */
    protected def getThis: Builder[A]

    /** Gets a copy of the connector types
      *
      * @return copy of connectors object
      */
    protected def getConnectors: List[Connector.Type.Value] = connectorTypes

    /** Checks if version number is null, throws NullPointerException if so
      */
    @throws(classOf[NullPointerException]) //throws null pointer when version # is invalid
    protected def validate(): Unit = {
      if (Option(version).isEmpty)
        throw new NullPointerException("Version number is null")
      else {
        // If version is not null, then nothing needs to be done
      }
    }
  }
}

