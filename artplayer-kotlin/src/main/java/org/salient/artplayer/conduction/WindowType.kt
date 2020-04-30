package org.salient.artplayer.conduction

sealed class WindowType {

    object NORMAL : WindowType()

    object LIST : WindowType()

    object FULLSCREEN : WindowType()

    object TINY : WindowType()

}