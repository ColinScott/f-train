package com.abstractcode

import com.abstractcode.ftrain.communications.{CreatePortEffectDSL, CreatePortSyntax, SerialCommunicationsEffectDSL, SerialCommunicationsSyntax}
import com.abstractcode.ftrain.hardware.nce._

package object ftrain {
  object DSL extends NceThrottleEffectDSL
    with SerialCommunicationsEffectDSL
    with CreatePortEffectDSL

  object Syntax extends NceThrottleSyntax
    with SerialCommunicationsSyntax
    with CreatePortSyntax
}
