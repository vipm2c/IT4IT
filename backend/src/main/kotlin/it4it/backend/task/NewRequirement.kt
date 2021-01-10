package it4it.backend.task

import com.fasterxml.jackson.annotation.JsonProperty

class NewRequirement {

    @JsonProperty("task")
    val task:Long? = null

    @JsonProperty("reqId")
    val reqId:String? = null

}