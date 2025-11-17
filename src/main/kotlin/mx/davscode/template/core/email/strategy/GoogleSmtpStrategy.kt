package mx.evolutiondev.template.core.email.strategy

import jakarta.mail.internet.MimeMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import mx.evolutiondev.template.core.email.exception.EmailSendingException
import mx.evolutiondev.template.core.email.model.EmailRequest
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Component

@Component
@ConditionalOnProperty(name = ["email.provider"], havingValue = "google")
class GoogleSmtpStrategy(
    private val mailSender: JavaMailSender,
    @Value("\${spring.mail.username}") private val fromEmail: String
) : EmailSenderStrategy {

    override suspend fun send(request: EmailRequest) {
        withContext(Dispatchers.IO) {
            runCatching {
                val message: MimeMessage = mailSender.createMimeMessage()
                val helper = MimeMessageHelper(message, true, "UTF-8")

                helper.setFrom(fromEmail)
                helper.setTo(request.to)
                helper.setSubject(request.subject)
                
                helper.setText(request.textBody, request.htmlBody)
                
                mailSender.send(message)
            }.onFailure { exception ->
                throw EmailSendingException("Failed to send email via Google SMTP", exception)
            }
        }
    }

    override fun getStrategyName(): String = "GoogleSmtp"
}

