package it4it.backend.project

import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface ProjectRepository: CrudRepository<Project, Long> {

    fun findProjectByKey(@Param("projectKey") key: String): Optional<Project>

    fun findProjectById(@Param("id") id: Long): Optional<Project>

    override fun findAll():List<Project>

}