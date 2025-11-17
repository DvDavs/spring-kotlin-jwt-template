package mx.evolutiondev.template.core.auth.ktx

import mx.evolutiondev.template.core.auth.model.CustomerEntity

fun CustomerEntity.isCustomerAvailable() : Boolean {
    return this.isEnabled && !this.isBanned
}

