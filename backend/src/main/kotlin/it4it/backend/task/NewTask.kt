package it4it.backend.task

import com.fasterxml.jackson.annotation.JsonProperty

class NewTask {

    @JsonProperty("project")
    var project: Long? = null

    @JsonProperty("summary")
    var summary: String? = null

    @JsonProperty("status")
    var status: Long? = null

    @JsonProperty("description")
    var description: String? = null

    @JsonProperty("fixVersion")
    var fixVersion: Long? = null

    @JsonProperty("assignee")
    var assignee: Long? = null

    @JsonProperty("affectedVersion")
    var affectedVersion: Long? = null

    constructor(){}

    constructor(status: Long, project: Long, summary: String, description: String, fixVersion: Long, assignee: Long, affectedVersion: Long){
        this.project = project
        this.summary = summary
        this.description = description
        this.fixVersion = fixVersion
        this.assignee = assignee
        this.affectedVersion = affectedVersion
        this.status = status
    }

    companion object {
        private const val serialVersionUID = -1764970284520387975L
    }
}