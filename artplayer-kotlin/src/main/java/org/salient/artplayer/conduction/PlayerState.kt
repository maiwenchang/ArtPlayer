package org.salient.artplayer.conduction

// all possible MediaPlayer states
sealed class PlayerState {
    object ERROR : PlayerState()
    object IDLE : PlayerState()
    object PREPARING : PlayerState()
    object PREPARED : PlayerState()
    object PLAYING : PlayerState()
    object PAUSED : PlayerState()
    object STOP : PlayerState()
    object COMPLETED : PlayerState()
    object SEEK_COMPLETE : PlayerState()

}