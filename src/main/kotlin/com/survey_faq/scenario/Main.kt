package com.survey_faq.scenario

import com.fasterxml.jackson.databind.ObjectMapper
import com.justai.jaicf.activator.rasa.rasa
import com.justai.jaicf.activator.regex.regex
import com.justai.jaicf.builder.Scenario
import com.justai.jaicf.channel.telegram.telegram
import com.justai.jaicf.hook.BotRequestHook
import java.io.File
import java.io.IOException

val mainScenario = Scenario {

    class Info(
        var phone: String,
        var C_licence: Any?,
        var country: String
    )
    val catchAllData = File("catchAll.txt")
    val mapper = ObjectMapper()

    handle<BotRequestHook> {
        context.session["lastState"] = context.dialogContext.currentState
    }

    state("salary", noContext = true) {
        globalActivators {
            regex(Regex("какая зарплата?"))
            intent("salary")
        }
        action {
            reactions.say("Зарплата N рублей на руки.")
            reactions.go(context.session["lastState"].toString())
        }
    }
    state("location", noContext = true) {
        globalActivators {
            regex(Regex("где находится офис?"))
            intent("location")
        }
        action {
            reactions.say("Офис находится по адесу: N.")
            reactions.go(context.session["lastState"].toString())
        }
    }
    state("work_schedule", noContext = true) {
        globalActivators {
            regex(Regex("какой график работы?"))
            intent("work_schedule")
        }
        action {
            reactions.say("График работы 5/2.")
            reactions.go(context.session["lastState"].toString())
        }
    }
    state("start_time", noContext = true) {
        globalActivators {
            regex(Regex("когда можно приступать?"))
            intent("start_time")
        }
        action {
            reactions.say("Приступить к работе можно в течении 2-ух недель.")
            reactions.go(context.session["lastState"].toString())
        }
    }
    state("greetings", noContext = true) {
        globalActivators {
            regex(Regex("привет"))
            intent("greet")
        }
        action {
            reactions.sayRandom("Добрый день!\n", "Здравствуйте!\n", "Приветствую!\n")
            reactions.go(context.session["lastState"].toString())
        }
    }
    state("help", noContext = true) {
        globalActivators {
            regex(Regex("подключите hr"))
            intent("help")
        }
        action {
            reactions.say("Хорошо, сейчас позову HR для вас.\n")
            context.session["support"] = true
//            reactions.go("/end")
        }
    }
    state("spam", noContext = true) {
        globalActivators {
            regex(Regex("купи слона"))
            intent("spam")
        }
        action {
            reactions.say("Не нужно такого в этом чате, пожалуйста.\n")
            reactions.go(context.session["lastState"].toString())
        }
    }

    state("start") {
        globalActivators {
            regex(Regex(".*start"))
        }
        action {
            context.session.clear()
            context.client.clear()
            reactions.say("Привет, давай я задам вам несколько вопросов.\n")
            reactions.go("/question_1")
        }
    }
    state("question_1") {
        action {
            reactions.say("Подскажите, пожалуйста, ваш номер телефона?")
        }
        state("check_phone") {
            activators {
                regex(Regex(".*([78][0-9]{10}).*"))
            }
            action {
                reactions.say("Спасибо!\n")
                context.client["phone"] = activator.regex?.matcher?.group(1).toString()
                println(context.client)
                reactions.go("/question_2")
            }
        }
        state("no_phone") {
            activators { intent("nlu_fallback") }
            action {
                reactions.say("Не очень похоже на номер телефона, попробуйте ещё раз.\n")
                reactions.go("/question_1")
            }
        }
    }
    state("question_2") {
        action {
            reactions.say("Есть ли у вас водительские права категории С?")
        }

        state("yes") {
            activators {
                intent("yes")
            }
            action {
                reactions.say("Отлично!\n")
                context.client["C_licence"] = true
                reactions.go("/question_3")
            }
        }
        state("no") {
            activators {
                intent("no")
            }
            action {
                reactions.say("Жаль, спасибо!")
                context.client["C_licence"] = false
                reactions.go("/question_3")
            }
        }
    }
    state("question_3") {
        action {
            reactions.say("Какое у вас гражданство?")
        }
        state("country") {
            activators {
                intent("country")
            }
            action {
                val entities = activator.rasa?.entities
                if (!entities.isNullOrEmpty()) {
                    context.client["country"] = entities[0].value
                } else {
                    reactions.say("Я такой страны не знаю, переформулируйте, пожалуйста.\n")
                }
                println(context.client)
                reactions.go("/end")
            }
        }
        state("no_country") {
            activators { intent("nlu_fallback") }
            action {
                reactions.say("Я такой страны не знаю, переформулируйте, пожалуйста.\n")
                reactions.go("/question_3")
            }
        }
    }

    state("end") {
        action {
            val res = Info(context.client["phone"].toString(), context.client["C_licence"], context.client["country"].toString())
            val username = request.telegram?.message?.chat?.username
            val myFile = if (!username.isNullOrEmpty()) {
                File("$username.json")
            } else {
                File("test.json")
            }
            try {
                mapper.writeValue(myFile, res)
            } catch (e: IOException) {
                e.printStackTrace()
            }
            reactions.say("Спасибо за ваши ответы!\n")
            reactions.say("Вот что мне удалось о вас узнать:\n" +
                    "{\n" + "\"phone\": ${context.client["phone"]},\n" +
                    "\"c_licence\": ${context.client["C_licence"]},\n" +
                    "\"country\": ${context.client["country"]}\n}"
            )
        }
    }
    state("catchall", noContext = true) {
        globalActivators { catchAll() }
        action {
            catchAllData.appendText(request.input + System.lineSeparator())
            reactions.say("Я вас не понял, повторите, пожалуйста.\n")
            reactions.go(context.session["lastState"].toString())
        }
    }
}
