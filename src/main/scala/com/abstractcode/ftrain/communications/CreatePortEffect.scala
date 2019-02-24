package com.abstractcode.ftrain.communications

import com.abstractcode.ftrain.communications.CreatePortEffect.{CreatePort, HasCreatePort}
import com.abstractcode.ftrain.communications.Serial.BaudRate
import org.atnos.eff.{Eff, |=}

object CreatePortEffect {
  sealed trait CreatePortEffect[A]

  type HasCreatePort[R] = CreatePortEffect |= R

  case class CreatePort(portName: String, baudRate: BaudRate) extends CreatePortEffect[Serial]
}

trait CreatePortEffectDSL {
  def createPort[R: HasCreatePort](portName: String, baudRate: BaudRate): Eff[R, Serial] =
    Eff.send(CreatePort(portName, baudRate))
}