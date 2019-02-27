package com.abstractcode.ftrain.hardware.nce

import cats.implicits._
import com.abstractcode.ftrain.hardware.nce.NceResponse.{NceFailure, NceResponse}
import org.atnos.eff.Eff

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

  def success[R]: Eff[R, NceResponse] = Eff.pure[R, NceResponse](().asRight[NceFailure])

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

object NceExceptions {
  implicit class NceExceptionWrapper(val throwable: Throwable) extends AnyVal {
    def toNceResponse[R]: Eff[R, NceResponse] = Eff.pure[R, NceResponse](NceFailure(throwable).asLeft[Unit])
  }
}