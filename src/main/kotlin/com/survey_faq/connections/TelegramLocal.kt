package com.survey_faq.connections

import com.justai.jaicf.channel.telegram.TelegramChannel
import com.survey_faq.Configuration
import com.survey_faq.bot

fun main() {
    TelegramChannel(bot, Configuration.telegram.token).run()
}