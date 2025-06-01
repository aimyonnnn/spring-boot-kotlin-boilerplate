package com.example.demo.kotest.common.config

import io.kotest.core.config.AbstractProjectConfig
import io.kotest.extensions.spring.SpringTestExtension
import io.kotest.extensions.spring.SpringTestLifecycleMode

object KotestSpringConfig : AbstractProjectConfig() {
	override fun extensions() = mutableListOf(SpringTestExtension(SpringTestLifecycleMode.Root))
}
