package com.github.trevorwhitney.prometheus

import io.github.mweirauch.micrometer.jvm.extras.ProcessMemoryMetrics
import io.github.mweirauch.micrometer.jvm.extras.ProcessThreadMetrics
import io.micrometer.core.instrument.Tags
import io.micrometer.core.instrument.binder.jvm.ClassLoaderMetrics
import io.micrometer.core.instrument.binder.jvm.JvmGcMetrics
import io.micrometer.core.instrument.binder.jvm.JvmMemoryMetrics
import io.micrometer.core.instrument.binder.jvm.JvmThreadMetrics
import io.micrometer.core.instrument.binder.system.ProcessorMetrics
import io.micrometer.prometheus.PrometheusConfig
import io.micrometer.prometheus.PrometheusMeterRegistry
import io.micrometer.prometheus.PrometheusRenameFilter
import java.time.Instant

object JvmPrometheusMeterRegistry {
    val prometheusMeterRegistry by lazy {
        PrometheusMeterRegistry(PrometheusConfig.DEFAULT).apply {
            initialize(this)
        }
    }

    private fun initialize(prometheusMeterRegistry: PrometheusMeterRegistry) {
        prometheusMeterRegistry.config().meterFilter(PrometheusRenameFilter())
        prometheusMeterRegistry.config().namingConvention(MetricNamingConvention())

        ClassLoaderMetrics().bindTo(prometheusMeterRegistry)
        JvmMemoryMetrics().bindTo(prometheusMeterRegistry)
        JvmGcMetrics().bindTo(prometheusMeterRegistry)
        ProcessorMetrics().bindTo(prometheusMeterRegistry)
        JvmThreadMetrics().bindTo(prometheusMeterRegistry)
        ProcessMemoryMetrics().bindTo(prometheusMeterRegistry)
        ProcessThreadMetrics().bindTo(prometheusMeterRegistry)

        prometheusMeterRegistry.counter("started.at.total", Tags.of("name", "builtin")).increment(Instant.now().toEpochMilli().toDouble())
    }
}