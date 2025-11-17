package mx.evolutiondev.template.core.email

import mx.evolutiondev.template.core.email.model.EmailContent
import org.springframework.stereotype.Service

@Service
class EmailContentGenerator {

    fun generate(emailType: String, variables: Map<String, Any>): EmailContent {
        return when (emailType) {
            "password-reset" -> generatePasswordResetContent(variables)
            else -> throw IllegalArgumentException("Unknown email type: $emailType")
        }
    }

    private fun buildHtmlTemplate(subject: String, contentBody: String): String {
        return """
            <!DOCTYPE html>
            <html lang="es">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>$subject</title>
                <style>
                    body {
                        font-family: 'Helvetica Neue', Arial, sans-serif;
                        line-height: 1.6;
                        color: #333;
                        margin: 0;
                        padding: 0;
                        background-color: #f4f4f4;
                    }
                    .container {
                        max-width: 600px;
                        margin: 20px auto;
                        background: white;
                        border-radius: 8px;
                        overflow: hidden;
                        box-shadow: 0 2px 8px rgba(0,0,0,0.1);
                    }
                    .header {
                        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                        color: white;
                        padding: 30px;
                        text-align: center;
                    }
                    .header h1 {
                        margin: 0;
                        font-size: 24px;
                        font-weight: 600;
                    }
                    .content {
                        padding: 30px;
                    }
                    .footer {
                        background-color: #f8f9fa;
                        padding: 20px;
                        text-align: center;
                        font-size: 12px;
                        color: #6c757d;
                        border-top: 1px solid #e9ecef;
                    }
                    h1, h2, h3 {
                        color: #333;
                    }
                    a {
                        color: #667eea;
                    }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>Template Application</h1>
                    </div>
                    <div class="content">
                        $contentBody
                    </div>
                    <div class="footer">
                        <p>Este es un correo automático, por favor no respondas a este mensaje.</p>
                        <p>&copy; ${java.time.Year.now().value} Template Application. Todos los derechos reservados.</p>
                    </div>
                </div>
            </body>
            </html>
        """.trimIndent()
    }
    
    private fun generatePasswordResetContent(variables: Map<String, Any>): EmailContent {
        val customerName = variables["customerName"] as? String ?: "Usuario"
        val resetUrl = variables["resetUrl"] as? String ?: "#"

        val subject = "Restablecimiento de tu contraseña"
        
        val htmlBody = """
            <h1>Hola $customerName,</h1>
            <p>Hemos recibido una solicitud para restablecer tu contraseña.</p>
            <p>Para restablecer tu contraseña, haz clic en el siguiente botón:</p>
            
            <div style="text-align: center; margin: 30px 0;">
                <a href="$resetUrl" style="background-color: #667eea; color: white; padding: 12px 24px; text-decoration: none; border-radius: 5px; display: inline-block; font-weight: bold;">Restablecer Contraseña</a>
            </div>
            
            <p><strong>Este enlace expirará en 15 minutos por razones de seguridad.</strong></p>
            <p>Si no solicitaste este restablecimiento, puedes ignorar este correo de forma segura.</p>
        """.trimIndent()
        
        val textBody = """
            Hola $customerName,
            
            Hemos recibido una solicitud para restablecer tu contraseña.
            
            Para restablecer tu contraseña, haz clic en el siguiente enlace:
            $resetUrl
            
            Este enlace expirará en 15 minutos por razones de seguridad.
            
            Si no solicitaste este restablecimiento, puedes ignorar este correo de forma segura.
            
            Saludos,
            El equipo de Template
        """.trimIndent()

        return EmailContent(subject, buildHtmlTemplate(subject, htmlBody), textBody)
    }
}

