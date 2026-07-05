package com.hashem.firstandsecondderivativewithcompose

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "FirstAndSecondDerivativeWithCompose",
    ) {
        App()
    }
}