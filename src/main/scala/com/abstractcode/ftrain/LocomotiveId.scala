package com.abstractcode.ftrain

sealed trait LocomotiveId

case object Broadcast extends LocomotiveId
case class PrimaryAddress(id: Byte) extends LocomotiveId
case class ExtendedAddress(id: Short) extends LocomotiveId
