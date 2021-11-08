package io.kma.results.readercccd.di

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import io.kma.results.readercccd.App
import io.kma.results.readercccd.usecase.*


val App.settings
    get() = Settings.getInstance(applicationContext)


val barcodeParser
    get() = BarcodeParser

val AppCompatActivity.barcodeImageScanner
    get() = BarcodeImageScanner

val AppCompatActivity.barcodeImageGenerator
    get() = BarcodeImageGenerator

val AppCompatActivity.barcodeDatabase
    get() = BarcodeDatabase.getInstance(this)

val AppCompatActivity.settings
    get() = Settings.getInstance(this)


val AppCompatActivity.permissionsHelper
    get() = PermissionsHelper

val AppCompatActivity.rotationHelper
    get() = RotationHelper


val Fragment.scannerCameraHelper
    get() = ScannerCameraHelper

val Fragment.barcodeParser
    get() = BarcodeParser

val Fragment.barcodeDatabase
    get() = BarcodeDatabase.getInstance(requireContext())

val Fragment.settings
    get() = Settings.getInstance(requireContext())

val Fragment.permissionsHelper
    get() = PermissionsHelper