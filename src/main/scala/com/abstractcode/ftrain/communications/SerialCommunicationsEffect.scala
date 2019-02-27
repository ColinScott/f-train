package com.abstractcode.ftrain.communications

import com.abstractcode.ftrain.communications.SerialCommunicationsEffect._
import org.atnos.eff.{Eff, |=}

object SerialCommunicationsEffect {

  sealed trait SerialCommunicationsEffect[A]

  type SerialCommunications[R] = SerialCommunicationsEffect |= R

  sealed trait ResponseSize {
    val size: Int
  }
  object ResponseSize {
    private case class ResponseSizeImpl(size: Int) extends ResponseSize

    val zeroByte: ResponseSize = ResponseSizeImpl(0)
    val singleByte: ResponseSize = ResponseSizeImpl(1)
    val doubleByte: ResponseSize = ResponseSizeImpl(2)
    val tripleByte: ResponseSize = ResponseSizeImpl(3)
    val quadByte: ResponseSize = ResponseSizeImpl(4)
    val sixteenByte: ResponseSize = ResponseSizeImpl(16)
  }

  case class Request(serial: Serial, payload: Vector[Byte], responseSize: ResponseSize) extends SerialCommunicationsEffect[Either[Throwable, Response]]
  case class Response(payload: Vector[Byte])

}

trait SerialCommunicationsEffectDSL {
  def makeRequest[R: SerialCommunications](
    serial: Serial,
    payload: Vector[Byte],
    responseSize: ResponseSize
  ): Eff[R, Either[Throwable, Response]] = Eff.send(Request(serial, payload, responseSize))
}
