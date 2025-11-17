package mx.evolutiondev.template.core.email.model

data class EmailContent(
    val subject: String,
    val htmlBody: String,
    val textBody: String
)

