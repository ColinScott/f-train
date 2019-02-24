package com.abstractcode

import com.abstractcode.ftrain.communications.{CreatePortEffectDSL, CreatePortSyntax}
import com.abstractcode.ftrain.hardware.nce._

package object ftrain {
  object DSL extends NceThrottleEffectDSL
    with NceSerialCommunicationsEffectDSL
    with CreatePortEffectDSL

  object Syntax extends NceThrottleSyntax
    with NceSerialCommunicationsSyntax
    with CreatePortSyntax
}
