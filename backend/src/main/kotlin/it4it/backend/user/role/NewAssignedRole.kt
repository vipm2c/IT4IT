package it4it.backend.user.role

import com.fasterxml.jackson.annotation.JsonProperty

class NewAssignedRole {

    @JsonProperty("name")
    var role: Long? = null

    @JsonProperty("username")
    var user: String? = null

    @JsonProperty("email")
    var project: String? = null

    constructor(){}

    constructor(role: Long, user: String, project: String){
        this.role = role
        this.user = user
        this.project = project
    }

    companion object {
        private const val serialVersionUID = -1764970284520387975L
    }

}