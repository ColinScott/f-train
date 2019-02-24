package com.abstractcode

import com.abstractcode.ftrain.communications.{CreatePortEffectDSL, CreatePortSyntax}
import com.abstractcode.ftrain.hardware.nce.{NceSerialCommunicationsEffectDSL, NceSerialCommunicationsSyntax}

package object ftrain {
  object DSL extends NceSerialCommunicationsEffectDSL
    with CreatePortEffectDSL

  object Syntax extends NceSerialCommunicationsSyntax
    with CreatePortSyntax
}
