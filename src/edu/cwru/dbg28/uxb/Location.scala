package edu.cwru.dbg28.uxb

/**
  * EECS 293 HW5
  * Created by Daniel on 9/24/2016.
  * dbg28@case.edu
  *
  * Location Enum, contains location data of Device or Connector
  */
object Location extends Enumeration {
  type Location = Value
  val UNSPECIFIED,FOUND, NOTFOUND = Value
}
