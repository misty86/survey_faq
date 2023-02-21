package com.survey_faq.configuration

data class AppConfiguration(
    val telegram: TelegramConfig,
//    val rasa: RasaConfig
)
data class TelegramConfig(
    val token: String
)
//data class RasaConfig(
//    val uri: String
//)