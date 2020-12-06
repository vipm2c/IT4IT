package it4it.backend.user

import com.fasterxml.jackson.annotation.JsonProperty
import java.io.Serializable

class NewUser : Serializable {

    @JsonProperty("name")
    var name: String? = null

    @JsonProperty("username")
    var username: String? = null

    @JsonProperty("email")
    var email: String? = null

    @JsonProperty("credential")
    var credential: String? = null

    @JsonProperty("admin")
    var admin: Boolean = false

    @JsonProperty("active")
    var active: Boolean = true

    constructor(){}

    constructor(name: String, username: String, email: String, credential: String, admin: Boolean, active: Boolean){
        this.name = name
        this.username = username
        this.email = email
        this.credential = credential
        this.admin = admin
        this.active = active
    }

    companion object {
        private const val serialVersionUID = -1764970284520387975L
    }

}