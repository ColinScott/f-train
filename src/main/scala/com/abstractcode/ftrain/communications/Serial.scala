package com.abstractcode.ftrain.communications

import cats.effect.IO
import cats.implicits._
import com.fazecast.jSerialComm.SerialPort

class Serial private(val serialPort: SerialPort) {}

object Serial {

  sealed trait BaudRate { def rate: Int }
  case object BaudRate9200 extends BaudRate { val rate: Int = 9200 }
  case object BaudRate19200 extends BaudRate { val rate = 19200 }

  def open(portName: String, baudRate: BaudRate): IO[Serial] = for {
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
  } yield Serial(port)

  private def apply(serialPort: SerialPort) = new Serial(serialPort)

  def write(serial: Serial)(data: Vector[Byte]): IO[Either[Throwable, Unit]] = IO {
    Either.catchNonFatal {
      serial.serialPort.getOutputStream.write(data.toArray)
    }
  }

  def read(serial: Serial)(size: Int): IO[Either[Throwable, Vector[Byte]]] = IO {
    Either.catchNonFatal {
      val buffer: Array[Byte] = Array.ofDim(size)
      serial.serialPort.getInputStream.read(buffer, 0, size)
      buffer.toVector
    }
  }
}
