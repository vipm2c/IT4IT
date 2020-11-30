package it4it.backend.task

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface TaskStatusRepository: CrudRepository<TaskStatus, Long> {


}