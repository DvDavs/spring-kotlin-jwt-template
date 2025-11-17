package mx.evolutiondev.template.core.email

import mx.evolutiondev.template.core.email.model.EmailRequest
import mx.evolutiondev.template.core.email.strategy.EmailSenderStrategy
import org.springframework.stereotype.Service

@Service
class EmailService(
    private val emailSenderStrategy: EmailSenderStrategy,
    private val contentGenerator: EmailContentGenerator
) {

    suspend fun sendEmail(to: String, templateName: String, variables: Map<String, Any>) {
        val content = contentGenerator.generate(templateName, variables)

        val request = EmailRequest(
            to = to,
            subject = content.subject,
            htmlBody = content.htmlBody,
            textBody = content.textBody
        )

        runCatching {
            emailSenderStrategy.send(request)
        }.onFailure { exception ->
            throw exception
        }
    }

    suspend fun sendCustomEmail(to: String, subject: String, htmlBody: String, textBody: String) {
        val request = EmailRequest(
            to = to,
            subject = subject,
            htmlBody = htmlBody,
            textBody = textBody
        )

        runCatching {
            emailSenderStrategy.send(request)
        }.onFailure { exception ->
            throw exception
        }
    }
}

