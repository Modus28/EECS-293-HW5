package edu.cwru.dbg28.uxb

/**
  * EECS 293 HW5
  * Created by Daniel on 9/10/2016.
  * dbg28@case.edu
  *
  * DeviceClass Enum, contains types of Devices
  */
object DeviceClass extends Enumeration {
  type DeviceClass = Value
  val UNSPECIFIED, AUDIO, COMM, HID, PID, IMAGE, PRINTER,
  STORAGE, VIDEO, AV, VR, HUB = Value
}
