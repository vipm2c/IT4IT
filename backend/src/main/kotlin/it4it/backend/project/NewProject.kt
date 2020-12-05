package it4it.backend.project

import com.fasterxml.jackson.annotation.JsonProperty
import java.io.Serializable

class NewProject : Serializable {

    @JsonProperty("name")
    var name: String? = null

    @JsonProperty("description")
    var description: String? = null

    @JsonProperty("key")
    var key: String? = null

    @JsonProperty("spec")
    var spec: String? = null


    constructor(){}

    constructor(name: String, description: String, key: String, spec: String){
        this.name = name
        this.description = description
        this.key = key
        this.spec = spec
    }

    companion object {
        private const val serialVersionUID = -1764970284520387975L
    }

}