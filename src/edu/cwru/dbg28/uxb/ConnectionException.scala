package edu.cwru.dbg28.uxb

/**
  * EECS 293 HW5
  * Created by Daniel Grigsby on 9/23/2016.
  * dbg28@case.edu
  */
class ConnectionException(private val errorCode: ConnectionException.ErrorCode.Value,
                          private val connector: Connector) extends Exception {
  private val serialVersionUID = 293
  println("ConnectionException: " + errorCode) // Intentional: Displays errorcode type when error occurs
}

object ConnectionException {

  object ErrorCode extends Enumeration {
    type ErrorCode = Value
    val CONNECTOR_BUSY, CONNECTOR_MISMATCH, CONNECTOR_CYCLE = Value
  }

}