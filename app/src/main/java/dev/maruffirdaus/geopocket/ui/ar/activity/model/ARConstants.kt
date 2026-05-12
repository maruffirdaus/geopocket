package dev.maruffirdaus.geopocket.ui.ar.activity.model

import dev.romainguy.kotlin.math.Float3
import dev.romainguy.kotlin.math.Quaternion

object ARConstants {
    val FaceUpQuaternion = Quaternion.fromAxisAngle(Float3(1f, 0f, 0f), -90f)
}