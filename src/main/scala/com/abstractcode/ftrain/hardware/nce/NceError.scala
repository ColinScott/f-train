package com.abstractcode.ftrain.hardware.nce

object NceError {
  sealed trait NceError
  case object CommandNotSupported extends NceError
  case object DecoderAddressOutOfRange extends NceError
  case object CabAddressOrOpCodeOutOfRange extends NceError
  case object CVAddressOrDataOutOfRange extends NceError
  case object ByteCountOutOfRange extends NceError
  case class UnknownNceError(errorCode: Byte) extends NceError

  def apply(error: Byte): NceError = error match {
    case 0x30 => CommandNotSupported
    case 0x31 => DecoderAddressOutOfRange
    case 0x32 => CabAddressOrOpCodeOutOfRange
    case 0x33 => CVAddressOrDataOutOfRange
    case 0x34 => ByteCountOutOfRange
    case e => UnknownNceError(e)
  }
}