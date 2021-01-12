package it4it.backend.task.link

import com.fasterxml.jackson.annotation.JsonProperty

class NewLink {

    @JsonProperty("linkType")
    var linkType: Long? = null

    @JsonProperty("outward")
    var outward: Long? = null

    @JsonProperty("inward")
    var inward: Long? = null

    constructor(){}

    constructor(linkType: Long, outward: Long, inward: Long){
        this.linkType = linkType
        this.outward = outward
        this.inward = inward
    }

    companion object {
        private const val serialVersionUID = -1764970284520387975L
    }

}