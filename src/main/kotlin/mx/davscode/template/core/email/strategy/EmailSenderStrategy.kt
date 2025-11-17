package mx.evolutiondev.template.core.email.strategy

import mx.evolutiondev.template.core.email.model.EmailRequest

interface EmailSenderStrategy {
    
    suspend fun send(request: EmailRequest)
    
    fun getStrategyName(): String
}

