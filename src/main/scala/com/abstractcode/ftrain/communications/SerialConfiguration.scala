package com.abstractcode.ftrain.communications

import cats.data.Reader
import com.abstractcode.ftrain.communications.Serial.BaudRate
import org.atnos.eff.|=

final case class SerialConfiguration(
  portName: String,
  baudRate: BaudRate
)

object SerialConfigurationEffect {
  type SerialConfigurationReader[A] = Reader[SerialConfiguration, A]
  type HasSerialConfiguration[R] = SerialConfigurationReader |= R
}
