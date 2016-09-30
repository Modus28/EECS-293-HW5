package edu.cwru.dbg28.uxb

/**
  * EECS 293 HW5
  * Created by Daniel on 9/17/2016.
  * dbg28@case.edu
  *
  * Abstract Peripheral -- Type of Abstract Device
  */
abstract class AbstractPeripheral[A <: AbstractDevice.Builder[A]] protected(builder: AbstractPeripheral.Builder[A])
  extends AbstractDevice[A](builder) {}


// Companion object for AbstractPeripheral -- holds Builder
object AbstractPeripheral {

  /** AbstractPeripheral Builder - Builds AbstractPeripherals
    *
    * @param version the version # of the Device
    * @tparam A parameter that bounds the types of inputs
    */
  abstract class Builder[A <: AbstractDevice.Builder[A]](version: Int) extends AbstractDevice.Builder[A](version) {
    // Methods

    /** Checks if version number is valid throws NullPointerException if it isn't
      * Then checks if all connectors are peripherals, throws IllegalStateException if they aren't
      * */
    @throws(classOf[IllegalStateException]) // if a connector is not a peripheral
    @throws(classOf[NullPointerException]) //  when version # is invalid
    override def validate(): Unit = {
      super.validate() //
      if (!getConnectors.forall(_.equals(Connector.Type.PERIPHERAL))) {
        throw new IllegalStateException("All connectors should be Peripherals")
      }
    }
  }
}