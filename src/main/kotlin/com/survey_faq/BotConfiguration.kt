package com.survey_faq

import com.justai.jaicf.BotEngine
import com.justai.jaicf.activator.catchall.CatchAllActivator
import com.justai.jaicf.activator.event.BaseEventActivator
import com.justai.jaicf.activator.rasa.RasaIntentActivator
import com.justai.jaicf.activator.rasa.api.RasaApi
import com.justai.jaicf.activator.regex.RegexActivator
import com.survey_faq.scenario.mainScenario
import com.survey_faq.configuration.AppConfiguration
import com.justai.jaicf.logging.Slf4jConversationLogger
import com.sksamuel.hoplite.ConfigLoader
import com.sksamuel.hoplite.PropertySource

private val applicationConfigLoader = ConfigLoader.Builder()
    .addSource(PropertySource.resource("/config.yml", optional = false))
    .build()

val Configuration: AppConfiguration = applicationConfigLoader.loadConfigOrThrow()

val bot = BotEngine(
    scenario = mainScenario,
    conversationLoggers = arrayOf(
        Slf4jConversationLogger()
    ),
    activators = arrayOf(
        BaseEventActivator,
        RegexActivator,
        CatchAllActivator,
        RasaIntentActivator.Factory(RasaApi(Configuration.rasa.uri), confidenceThreshold = 0.95)
    )
)