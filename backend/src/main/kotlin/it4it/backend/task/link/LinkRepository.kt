package it4it.backend.task.link

import it4it.backend.task.Task
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface LinkRepository : CrudRepository<Link, Long> {

    fun findAllByInward(inward: Task):List<Link>

    fun findAllByOutward(outward: Task):List<Link>

}