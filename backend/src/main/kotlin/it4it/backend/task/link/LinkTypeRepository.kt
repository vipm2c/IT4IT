package it4it.backend.task.link

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface LinkTypeRepository: CrudRepository<LinkType, Long> {


}