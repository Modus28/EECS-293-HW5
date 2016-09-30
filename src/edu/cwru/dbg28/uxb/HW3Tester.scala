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
  private final val DEFAULT_SERIAL_NUMBER = Option(BigInt(1))
  private final val DEFAULT_VERSION = 13
  private final val DEFAULT_CONNECTOR_TYPES_HUB = List(Connector.Type.COMPUTER, Connector.Type.PERIPHERAL)
  private final val DEFAULT_CONNECTOR_TYPES_PERIPHERAL = List(Connector.Type.PERIPHERAL)
  private final val DEFAULT_STRING_MESSAGE = "parser"
  private final val DEFAULT_BINARY_MESSAGE = 1111
  private final val FAIL = false
  private final val PASS = true


  // Calls the tester methods on execution
  def main(args: Array[String]): Unit = {
    messageTester()
    connectorTest()
    genericDeviceTester(getDefaultDevices)
    communicationDeviceSetup(getDefaultDevices)
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

  // Helper method to test communications for a list of Devices and Messages
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
      assert(stream.toString().length > 330) // Verifies the STDOUT against known length, can't be specific due to OS
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
    d2.getConnector(0).setPeer(d1.getConnector(0)) // By the HW Instructions, setPeer is one way, should it be both?
    assert(d2.isReachable(d1)) // check to see if they are reachable by each other.
    assert(d1.isReachable(d2), println(d1.isReachable(d2))) // check the reverse

    val stream = new java.io.ByteArrayOutputStream() // Catch forward output
    Console.withOut(stream) {
      d1.communicate(0, new StringMessage(DEFAULT_STRING_MESSAGE))
      d2.communicate(0, new BinaryMessage(DEFAULT_BINARY_MESSAGE))
    }
    assert(stream.toString().contains("Sister printer has printed the string: parser 1")) // Check Device 1
    assert(stream.toString().contains("GoAmateur is not yet active: 1111")) // Check Device 2
    println("Unit testing for UXB Communications Completed with no errors")
  }
}