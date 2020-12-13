package it4it.backend.project.release

import it4it.backend.project.Project
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface ReleaseRepository: CrudRepository<Release, Long> {

    fun findReleaseByProject(@Param("projectKey") project: Project): List<Release>

    fun findReleaseById(@Param("releaseId") id : Long): Optional<Release>

}