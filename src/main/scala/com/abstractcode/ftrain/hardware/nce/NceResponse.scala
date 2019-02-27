package com.abstractcode.ftrain.hardware.nce

import cats.implicits._

object NceResponse {
  case class NceFailure(error: Option[NceError], throwable: Option[Throwable])

  object NceFailure {
    def apply(error: NceError): NceFailure = new NceFailure(Some(error), None)
    def apply(throwable: Throwable): NceFailure = new NceFailure(None, Some(throwable))
  }

  sealed trait NceError
  case object CommandNotSupported extends NceError
  case object DecoderAddressOutOfRange extends NceError
  case object CabAddressOrOpCodeOutOfRange extends NceError
  case object CVAddressOrDataOutOfRange extends NceError
  case object ByteCountOutOfRange extends NceError
  case class UnknownNceError(errorCode: Byte) extends NceError

  type NceResponse = Either[NceFailure, Unit]

  def apply(response: Byte): NceResponse = response match {
    case '!' => ().asRight
    case '0' => NceFailure(CommandNotSupported).asLeft
    case '1' => NceFailure(DecoderAddressOutOfRange).asLeft
    case '2' => NceFailure(CabAddressOrOpCodeOutOfRange).asLeft
    case '3' => NceFailure(CVAddressOrDataOutOfRange).asLeft
    case '4' => NceFailure(ByteCountOutOfRange).asLeft
    case e => NceFailure(UnknownNceError(e)).asLeft
  }
}