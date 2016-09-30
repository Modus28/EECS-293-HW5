package edu.cwru.dbg28.uxb

import edu.cwru.dbg28.uxb.DeviceClass.DeviceClass

/**
  * EECS 293 HW5
  * Created by Daniel on 9/17/2016.
  * dbg28@case.edu
  *
  * A Peripheral that is a Printer
  */
abstract class AbstractPrinter[A <: AbstractDevice.Builder[A]] protected(builder: AbstractPrinter.Builder[A])
  extends AbstractPeripheral[A](builder) {

  /** Gets the UXB device class
    *
    * @return the class of the device -- Always returns PRINTER
    */
  override def getDeviceClass: DeviceClass = DeviceClass.PRINTER
}

// Companion object for AbstractPrinter -- holds Builder
object AbstractPrinter {

  /** AbstractPrinter Builder - Builds AbstractPrinters
    *
    * @param version the version # of the Device
    * @tparam A parameter that bounds the types of inputs
    */
  abstract class Builder[A <: AbstractDevice.Builder[A]](version: Int)
    extends AbstractPeripheral.Builder[A](version) {}
}