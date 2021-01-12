package it4it.backend.project.release

import com.fasterxml.jackson.annotation.JsonProperty

class NewRelease {

    @JsonProperty("version")
    var version: String? = null

    @JsonProperty("description")
    var description: String? = null

    @JsonProperty("project")
    var project: Long? = null

    @JsonProperty("released")
    var released: Boolean? = null

    @JsonProperty("spec")
    var spec: String? = null

    constructor(){}

    constructor(version: String, description: String, project: Long, released: Boolean, spec: String){
        this.version = version
        this.description = description
        this.project = project
        this.released = released
        this.description = description
        this.spec = spec
    }

    companion object {
        private const val serialVersionUID = -1764970284520387975L
    }

}