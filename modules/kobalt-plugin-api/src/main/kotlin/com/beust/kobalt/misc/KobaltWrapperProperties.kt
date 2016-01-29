package com.beust.kobalt.misc

import com.beust.kobalt.api.Kobalt
import com.google.inject.Inject
import java.io.File
import java.io.FileReader
import java.time.Instant
import java.util.*

/**
 * Manage kobalt-wrapper.properties.
 */
class KobaltWrapperProperties @Inject constructor() {
    private val WRAPPER_DIR = KFiles.KOBALT_DIR + "/wrapper"
    private val KOBALT_WRAPPER_PROPERTIES = "kobalt-wrapper.properties"
    private val PROPERTY_VERSION = "kobalt.version"
    private val PROPERTY_VERSION_LAST_CHECKED = "kobalt.version.last_checked"
    private val PROPERTY_DOWNLOAD_URL = "kobalt.downloadUrl"

    fun create(version: String, versionLastChecked: Instant) {
        log(2, "Creating $file with $version and ${defaultUrlFor(version)}")
        KFiles.saveFile(file, listOf(
                "$PROPERTY_VERSION=$version",
                "$PROPERTY_VERSION_LAST_CHECKED=$versionLastChecked"
//                "$PROPERTY_DOWNLOAD_URL=${defaultUrlFor(version)}"
        ).joinToString("\n"))
    }

    private fun defaultUrlFor(version: String) =
            "http://beust.com/kobalt/kobalt-$version.zip"

    private val file: File
            get() = File("$WRAPPER_DIR/$KOBALT_WRAPPER_PROPERTIES")

    private val properties : Properties
        get() {
            val config = file
            if (!config.exists()) {
                create(Kobalt.version, Instant.MIN)
            }

            val result = Properties()
            result.load(FileReader(config))
            return result
        }

    val version : String
        get() = properties.getProperty(PROPERTY_VERSION)

    val versionLastChecked: Instant
        get() = Instant.parse(properties.getProperty(PROPERTY_VERSION_LAST_CHECKED))

    val downloadUrl : String
        get() = properties.getProperty(PROPERTY_DOWNLOAD_URL)
}
