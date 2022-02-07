package com.soarex16.kaliper

import arrow.meta.CliPlugin
import arrow.meta.Meta
import arrow.meta.phases.CompilerContext

class KaliperMetaPlugin : Meta {
    override fun intercept(ctx: CompilerContext): List<CliPlugin> = listOf(generateShallowSize)
}