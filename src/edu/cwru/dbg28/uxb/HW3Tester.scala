package edu.cwru.dbg28.uxb

/**
  * EECS 293 HW5
  * Created by Daniel Grigsby on 9/13/2016.
  * dbg28@case.edu
  *
  * Tester class for HW3
  */
object HW3Tester {
  // Final fields for testing
  private final val DEFAULT_PRODUCT_CODE = Option(9)
  private final val DEFAULT_SERIAL_NUMBER = Option(BigInt(4))
  private final val DEFAULT_VERSION = 13
  private final val DEFAULT_CONNECTOR_TYPES_HUB = List(Connector.Type.COMPUTER,
    Connector.Type.COMPUTER, Connector.Type.COMPUTER, Connector.Type.PERIPHERAL)
  private final val DEFAULT_CONNECTOR_TYPES_PERIPHERAL = List(Connector.Type.PERIPHERAL, Connector.Type.PERIPHERAL)
  private final val DEFAULT_STRING_MESSAGE = "parser"
  private final val DEFAULT_BINARY_MESSAGE = 1000
  private final val FAIL = false
  private final val PASS = true


  // Calls the tester methods on execution
  def main(args: Array[String]): Unit = {
    messageTester()
    connectorTest()
    genericDeviceTester(getDefaultDevices)
    communicationDeviceSetup(getDefaultDevices)
    testUXBSystem()
  }

  // Tests all devices with genericDeviceHelper method
  private def genericDeviceTester[A <: AbstractDevice.Builder[A]](listOfDevices: List[AbstractDevice[A]]): Unit = {
    for (concreteDeviceBuilder <- listOfDevices) {
      genericDeviceHelper(concreteDeviceBuilder)
    }
    println("Unit testing for Devices completed with no errors.")
  }

  // Generic device tester - tests input device against standard test battery
  private def genericDeviceHelper[A <: AbstractDevice.Builder[A]](newDevice: AbstractDevice[A]): Unit = {
    assert(newDevice.getProductCode.equals(DEFAULT_PRODUCT_CODE)) // productCode was set correctly by builder
    assert(newDevice.getSerialNumber.equals(DEFAULT_SERIAL_NUMBER)) // serialNumber was set
    assert(newDevice.getVersion.equals(DEFAULT_VERSION)) // version was set correctly by builder
    assert(!newDevice.getDeviceClass.equals(DeviceClass.UNSPECIFIED)) // DeviceClass correctly overrode
    assert(newDevice.getConnectorTypes.equals(newDevice.getDeviceClass match {
      case DeviceClass.HUB => DEFAULT_CONNECTOR_TYPES_HUB
      case DeviceClass.PRINTER | DeviceClass.VIDEO => DEFAULT_CONNECTOR_TYPES_PERIPHERAL
      case _ => println("You set up an incorrect test")
    })) // Tests if connector types are set correctly
    assert(newDevice.getConnectorCount.equals(newDevice.getConnectorTypes.length)) // connectorCount is correct
  }

  // Testing Messages
  private def messageTester(): Unit = {

    // BinaryMessage tester
    val bMessage1 = new BinaryMessage(DEFAULT_BINARY_MESSAGE)
    assert(bMessage1.getValue.equals(DEFAULT_BINARY_MESSAGE)) // The value is set equal to input
    val bMessage2 = new BinaryMessage(null)
    assert(bMessage2.getValue.equals(0)) // The value is 0 when input is missing
    assert(!bMessage1.equals(bMessage2)) // "A is not equal to B if value's are different
    assert(bMessage1.equals(new BinaryMessage(DEFAULT_BINARY_MESSAGE))) // Check custom equals conditions
    assert(bMessage1.isInstanceOf[Message]) // A BinaryMessage is a Message

    // Testing directly written StringMessage methods
    val sMessage1 = new StringMessage(DEFAULT_STRING_MESSAGE)
    assert(sMessage1.getString.equals(DEFAULT_STRING_MESSAGE)) // The value is set equal to input
    val sMessage2 = new StringMessage(null)
    assert(sMessage2.getString.equals("")) // The value is an empty non-null string when input is missing
    assert(!sMessage1.equals(sMessage2)) // "A is not equal to B if value's are different
    assert(sMessage1.equals(new StringMessage(DEFAULT_STRING_MESSAGE))) // Check custom equals conditions
    assert(sMessage1.isInstanceOf[Message]) // A StringMessage is a Message

    // Testing delegated String methods in StringMessage
    assert(sMessage1.length().equals(DEFAULT_STRING_MESSAGE.length)) // Testing length() method
    assert(sMessage1.charAt(0).equals(DEFAULT_STRING_MESSAGE.charAt(0))) // Testing charAt method
    assert(sMessage1.contains("par").equals(DEFAULT_STRING_MESSAGE.contains("par"))) // Testing contains method
    assert(sMessage1.endsWith("er").equals(DEFAULT_STRING_MESSAGE.endsWith("er"))) // Testing endsWith method
    assert(sMessage1.indexOf('p').equals(DEFAULT_STRING_MESSAGE.indexOf('p'))) // Testing  indexOf methods
    assert(sMessage1.lastIndexOf('p').equals(DEFAULT_STRING_MESSAGE.lastIndexOf('p'))) // Testing indexOf methods
    assert(sMessage1.isEmpty.equals(DEFAULT_STRING_MESSAGE.isEmpty)) // Testing isEmpty method
    assert(sMessage1.hashCode().equals(DEFAULT_STRING_MESSAGE.hashCode())) // Testing that hashcode is delegated
    println("Unit testing for Messages completed with no errors.")
  }


  // Testing Connector -- Line Order important
  private def connectorTest(): Unit = {
    val tempD = getDefaultDevices(2)
    val conn1 = new Connector(tempD, 3, Connector.Type.PERIPHERAL)
    assert(conn1.getDevice.equals(tempD)) // getDevice returns right device
    assert(conn1.getIndex.equals(3)) // getIndex gets the index
    assert(conn1.getType.equals(Connector.Type.PERIPHERAL)) // getType returns the Type
    assert(conn1.getPeer.isEmpty) // When no peer is set, getPeer returns None safely
    try {
      Console.withOut(new java.io.ByteArrayOutputStream()) { // blocks printing of intentional errors
        conn1.setPeer(conn1)
      }
      assert(FAIL, "An Exception should have been thrown")
    } catch {
      case e: ConnectionException => // Success
      case f: NullPointerException => assert(FAIL, "Expected ConnectionException")
    }

    println("Unit testing for Connector completed with no errors.")
  }

  // Gets a List of each concrete devices initialized to default parameters
  // Indexes of return List: 0: CannonPrinter, 1: GoAmateur, 2: SisterPrinter, 3: Hub
  private def getDefaultDevices[A <: AbstractDevice.Builder[A]]: List[AbstractDevice[Nothing]] = {
    val listOfConcreteDeviceBuilders = List(new CannonPrinter.Builder(DEFAULT_VERSION),
      new GoAmateur.Builder(DEFAULT_VERSION), new SisterPrinter.Builder(DEFAULT_VERSION),
      new Hub.Builder(DEFAULT_VERSION))

    for (concreteDeviceBuilder <- listOfConcreteDeviceBuilders) {
      concreteDeviceBuilder.productCode(DEFAULT_PRODUCT_CODE.getOrElse(0))
        .serialNumber(DEFAULT_SERIAL_NUMBER.getOrElse(0))
      if (concreteDeviceBuilder.isInstanceOf[Hub.Builder[_]]) {
        concreteDeviceBuilder.connectors(DEFAULT_CONNECTOR_TYPES_HUB)
      }
      else {
        concreteDeviceBuilder.connectors(DEFAULT_CONNECTOR_TYPES_PERIPHERAL)
      }
    }
    listOfConcreteDeviceBuilders.map(_.build())
  }

  // Create Devices & Messages to test UXB Communications with
  private def communicationDeviceSetup[A <: AbstractDevice.Builder[A]](deviceList: List[AbstractDevice[A]]): Unit = {
    // Message Creation
    val bMessage = new BinaryMessage(DEFAULT_BINARY_MESSAGE)
    val sMessage = new StringMessage(DEFAULT_STRING_MESSAGE)
    val messageList = List(bMessage, sMessage)
    testCommunications(deviceList, messageList)
  }

  // Broadcast: Helper method to test communications for a list of Devices and Messages
  private def testCommunications(devList: List[Device], messList: List[Message]): Unit = {
    // Specific method for sending one of each message to one of each concrete device
    def broadcast(): Unit = {
      // Capture STDOUT
      val stream = new java.io.ByteArrayOutputStream()
      Console.withOut(stream) {
        //all println in this block will be redirected
        for (mess <- messList) {
          for (dev <- devList) {
            dev.recv(mess, dev.getConnector(0))
          }
        }
      }
      assert(stream.toString().length > 245) // Verifies the STDOUT length, can't be specific due to OS differences
      stream.flush()
    }
    broadcast()

    // Testing simplified communicate message.
    val d1 = getDefaultDevices(1)
    val d2 = getDefaultDevices(2)
    Console.withOut(new java.io.ByteArrayOutputStream()) { // Block Connection Reason print out
      try {
        d1.getConnector(0).setPeer(d1.getConnector(0)) // Should throw a CONNECTOR_CYCLE exception
        assert(FAIL, "An Exception should have been thrown")
      } catch {
        case e: ConnectionException => // Success
        case _: Throwable => assert(FAIL, "Expected ConnectionException")
      }
    }

    // Checking the communication and connectivity of two a SisterPrinter and a GoAmateur
    d1.getConnector(0).setPeer(d2.getConnector(0))
    assert(d2.isReachable(d1)) // check to see if they are reachable by each other.
    assert(d1.isReachable(d2), println(d1.isReachable(d2))) // check the reverse

    val stream = new java.io.ByteArrayOutputStream() // Catch forward output
    Console.withOut(stream) {
      d1.communicate(0, new StringMessage(DEFAULT_STRING_MESSAGE))
      d2.communicate(0, new BinaryMessage(DEFAULT_BINARY_MESSAGE))
    }
    assert(stream.toString().contains("Sister printer has printed the string: parser 4")) // Check Device 1
    assert(stream.toString().contains("Sister printer has printed the binary message")) // Check Device 2's Forwarder
    println("Unit testing for UXB Communications Completed with no errors")
  }

  /** Tests the entire UXB system as a whole
    * Sends all message types through each Device Type
    */
  private def testUXBSystem(): Unit = {
    // Create Devices, Unconnected
    val hubLeft = getDefaultDevices(3)
    val hubRight = getDefaultDevices(3)
    val webCamCenter = getDefaultDevices(1)
    val sisterPrinterLeft = getDefaultDevices(2)
    val sisterPrinterRight = getDefaultDevices(2)
    val cannonPrinterLeft = getDefaultDevices.head
    // Create Messages
    val bMessage = new BinaryMessage(DEFAULT_BINARY_MESSAGE)
    val sMessage = new StringMessage(DEFAULT_STRING_MESSAGE)
    // Connect Devices in legal format.
    hubLeft.connect(webCamCenter, 0, 0)
    hubRight.connect(webCamCenter, 0, 1)
    hubLeft.connect(sisterPrinterLeft, 1, 0)
    hubLeft.connect(cannonPrinterLeft, 2, 0)
    hubRight.connect(sisterPrinterRight, 1, 0)
    // Message Send Scenario 1: HubLeft Broadcasts a StringMessage
    println("\nCommunication Scenario 1:")
    /* What we expect:
    The Left Hub receives the StringMessage from an invisible source (US), so it sends it to all its peers
      The Webcam cannot handle the StringMessage, so it says so, and prints the index of the connector sending it (0)
      The Left SisterPrinter should print the StringMessage and its serialNumber, (4)
      The Left CannonPrinter should print the StringMessage and its version (13)
     */
    hubLeft.recv(sMessage, hubLeft.getConnector(3))
    // Message Send Scenario 2: HubRight Sends BinaryMessage to Webcam
    println("Communication Scenario 2:")
    /* What we expect:
    The Webcam responds by sending a BinaryMessage (293) on all its connectors
      The Left Hub sends that BinaryMessage (293) to its other direct peers, Left Sister/CannonPrinter
        The Left SisterPrinter should print the BinaryMessage (293) plus its productCode(293+9)
        The Left CannonPrinter should print the BinaryMessage (293) times its serialNumber (293*4)
      The Right Hub sends that BinaryMessage (293) to its other direct peer, the Right SisterPrinter
        The Right SisterPrinter should print the BinaryMessage (293) plus its productCode(293+9)
     */
    hubRight.communicate(0, bMessage)
    // Message Send Scenario 3: HubLeft Broadcasts a BinaryMessage
    println("Communication Scenario 3:")
    /* What we expect
    The Left Hub receives the BinaryMessage (1000) from an invisible source (US), so it sends it to all its peers
      The Webcam responds by sending a BinaryMessage (293) on all its Connectors
        The Right Hub sends that BinaryMessage (293) to its other direct peer, the Right SisterPrinter
          The Right Sister Printer prints the BinaryMessage (293) plus its productCode (293+9)
        The Left Hub sends the Webcam's BinaryMessage (293) to its other direct peers, Left Sister/Cannon Printer
          The Left SisterPrinter prints the BinaryMessage (293) plus its productCode (293+9)
          The Left CannonPrinter receives the BinaryMessage (293) prints it times its serialNumber (293*4)
      The Left SisterPrinter receives the BinaryMessage (1000) and prints it plus its productCode (1000+9)
      the Left CannonPrinter receives the BinaryMessage (1000) and prints it times its SerialNumber (1000*4)
     */
    hubLeft.recv(bMessage, hubLeft.getConnector(3))
    println("\nUnit testing for UXB Full System Messaging completed with no errors")
  }
}