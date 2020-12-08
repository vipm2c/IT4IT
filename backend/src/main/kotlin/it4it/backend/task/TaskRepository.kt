package it4it.backend.task

import it4it.backend.project.Project
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface TaskRepository: CrudRepository<Task, Long> {

    fun findAllByProject(project: Project): List<Task>

    fun findByKey(key: String): Optional<Task>

}