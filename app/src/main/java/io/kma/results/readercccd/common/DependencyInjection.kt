package io.kma.results.readercccd.common

import androidx.fragment.app.Fragment
import io.kma.results.readercccd.usecase.*

val barcodeImageScanner
    get() = BarcodeImageScanner

val barcodeImageGenerator
    get() = BarcodeImageGenerator

val permissionsHelper
    get() = PermissionsHelper

val rotationHelper
    get() = RotationHelper


val scannerCameraHelper
    get() = ScannerCameraHelper

val barcodeParser
    get() = BarcodeParser

val Fragment.permissionsHelper
    get() = PermissionsHelper