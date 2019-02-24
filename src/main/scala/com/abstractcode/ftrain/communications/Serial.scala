package com.abstractcode.ftrain.communications

import cats.effect.IO
import cats.implicits._
import com.abstractcode.ftrain.hardware.nce.NceComms
import com.fazecast.jSerialComm.SerialPort

trait Serial {
  def write(data: Vector[Byte]): IO[Either[Throwable, Unit]]
  def read(size: Int): IO[Either[Throwable, Vector[Byte]]]
}

object Serial {

  sealed trait BaudRate { def rate: Int }
  case object BaudRate9200 extends BaudRate { val rate: Int = 9200 }
  case object BaudRate19200 extends BaudRate { val rate = 19200 }

  private case class SerialImpl(serialPort: SerialPort) extends Serial {
    override def write(data: Vector[Byte]): IO[NceComms[Unit]] = IO {
      Either.catchNonFatal {
        serialPort.getOutputStream.write(data.toArray)
      }
    }

    override def read(size: Int): IO[NceComms[Vector[Byte]]] = IO {
      Either.catchNonFatal {
        val buffer: Array[Byte] = Array.ofDim(size)
        serialPort.getInputStream.read(buffer, 0, size)
        buffer.toVector
      }
    }
  }

  def apply(portName: String, baudRate: BaudRate): IO[Serial] = for {
    port <- IO {
      val p = SerialPort.getCommPort(portName)
      p.setNumStopBits(1)
      p.setNumDataBits(8)
      p.setBaudRate(baudRate.rate)
      p.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING | SerialPort.TIMEOUT_WRITE_BLOCKING, 100, 0)
      p
    }
    _ <- IO {
      port.openPort()
    }
  } yield SerialImpl(port)
}
