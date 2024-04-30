package fr.isen.mouillot.datacommunication

import kotlinx.serialization.Serializable
@Serializable
data class LedMessage(
    val id: Char?,
    val state: Char?
)