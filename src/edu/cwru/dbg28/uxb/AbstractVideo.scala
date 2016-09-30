package edu.cwru.dbg28.uxb

import edu.cwru.dbg28.uxb.DeviceClass.DeviceClass

/**
  * EECS 293 HW5
  * Created by Daniel on 9/17/2016.
  * dbg28@case.edu
  *
  * An Peripheral with VIDEO properties
  */
abstract class AbstractVideo[A <: AbstractDevice.Builder[A]] protected(builder: AbstractPeripheral.Builder[A])
  extends AbstractPeripheral[A](builder) {

  /** Gets the UXB device class
    *
    * @return the class of the device -- Always returns VIDEO
    */
  override def getDeviceClass: DeviceClass = DeviceClass.VIDEO
}

// Companion object for AbstractVideo -- holds Builder
object AbstractVideo {

  /** AbstractVideo Builder - Builds AbstractVideos
    *
    * @param version the version # of the Device
    * @tparam A parameter that bounds the types of inputs
    */
  abstract class Builder[A <: AbstractDevice.Builder[A]](version: Int)
    extends AbstractPeripheral.Builder[A](version) {}

}