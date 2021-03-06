package com.beust.kobalt.app

import com.beust.kobalt.api.ITemplate
import com.beust.kobalt.api.ITemplateContributor
import com.beust.kobalt.app.java.JavaTemplateGenerator
import com.beust.kobalt.app.kotlin.KotlinTemplateGenerator
import com.beust.kobalt.internal.PluginInfo
import com.beust.kobalt.misc.log
import com.google.common.collect.ArrayListMultimap
import com.google.inject.Inject

class Templates @Inject constructor() : ITemplateContributor {
    override val templates = listOf(JavaTemplateGenerator(), KotlinTemplateGenerator(), IdeaFilesTemplate())

    fun list(pluginInfo: PluginInfo) {
        val map = ArrayListMultimap.create<String, ITemplate>()
        pluginInfo.initContributors.forEach {
            it.templates.forEach {
                map.put(it.pluginName, it)
            }
        }

        log(1, "Available templates")
        map.keySet().forEach {
            log(1, "  Plug-in: $it")
            map[it].forEach {
                log(1, "    \"" + it.templateName + "\"\t\t" + it.templateDescription)
            }
        }
    }
}
