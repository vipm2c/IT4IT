package it4it.backend.project.release

import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface ReleaseRepository: CrudRepository<Release, Long> {

    fun findReleaseByProject(@Param("projectKey") project: Long): List<Optional<Release>>

    fun findReleaseById(@Param("releaseId") id : Long): Optional<Release>

}