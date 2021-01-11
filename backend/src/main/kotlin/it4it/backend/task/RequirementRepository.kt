package it4it.backend.task

import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*
import javax.transaction.Transactional


@Repository
interface RequirementRepository: CrudRepository<Requirement, Long> {

    fun findAllByTask(task:Task): List<Requirement>

    fun findByReqIdAndTask(reqId:Long,task:Task): Optional<Requirement>

    @Transactional
    override fun deleteById(@Param("id") id:Long)
}