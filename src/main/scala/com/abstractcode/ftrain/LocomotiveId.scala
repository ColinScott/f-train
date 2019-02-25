package com.abstractcode.ftrain

sealed trait LocomotiveId

case object Broadcast extends LocomotiveId
case class PrimaryAddress(address: Byte) extends LocomotiveId
case class ExtendedAddress(address: Short) extends LocomotiveId
