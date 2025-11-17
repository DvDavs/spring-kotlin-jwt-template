package mx.evolutiondev.template.core.email.model

data class EmailRequest(
    val to: String,
    val subject: String,
    val htmlBody: String,
    val textBody: String
)

