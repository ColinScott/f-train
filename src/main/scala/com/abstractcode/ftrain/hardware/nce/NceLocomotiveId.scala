package com.abstractcode.ftrain.hardware.nce

import com.abstractcode.ftrain._

case class NceLocomotiveId(high: Byte, low: Byte)

object IdTranslation {

  implicit class LocomotiveIdTranslate(locomotiveId: LocomotiveId) {
    def toNceFormat: NceLocomotiveId = locomotiveId match {
      case Broadcast => NceLocomotiveId(0, 0)
      case PrimaryAddress(address) => NceLocomotiveId(0, address)
      case ExtendedAddress(address) => NceLocomotiveId(((address >>> 8) | 0xc0).toByte, (address & 0xff).toByte)
    }
  }
}
