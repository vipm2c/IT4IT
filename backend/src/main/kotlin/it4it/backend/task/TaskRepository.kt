package it4it.backend.task

import it4it.backend.task.link.LinkType
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface TaskRepository: CrudRepository<LinkType, Long> {


}