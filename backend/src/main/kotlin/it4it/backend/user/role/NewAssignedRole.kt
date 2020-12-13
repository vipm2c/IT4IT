package it4it.backend.user.role

import com.fasterxml.jackson.annotation.JsonProperty

class NewAssignedRole {

    @JsonProperty("role")
    var role: Long? = null

    @JsonProperty("user")
    var user: String? = null

    @JsonProperty("project")
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