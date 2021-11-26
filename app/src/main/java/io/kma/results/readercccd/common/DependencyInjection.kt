package io.kma.results.readercccd.common

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import io.kma.results.readercccd.App
import io.kma.results.readercccd.usecase.*


val App.settings
    get() = Settings.getInstance(applicationContext)


val barcodeParser
    get() = BarcodeParser

val barcodeImageScanner
    get() = BarcodeImageScanner

val barcodeImageGenerator
    get() = BarcodeImageGenerator

val AppCompatActivity.barcodeDatabase
    get() = BarcodeDatabase.getInstance(this)

val AppCompatActivity.settings
    get() = Settings.getInstance(this)


val permissionsHelper
    get() = PermissionsHelper

val rotationHelper
    get() = RotationHelper


val scannerCameraHelper
    get() = ScannerCameraHelper

val Fragment.barcodeParser
    get() = BarcodeParser

val Fragment.barcodeDatabase
    get() = BarcodeDatabase.getInstance(requireContext())

val Fragment.settings
    get() = Settings.getInstance(requireContext())

val Fragment.permissionsHelper
    get() = PermissionsHelper