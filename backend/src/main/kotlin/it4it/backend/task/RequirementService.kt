package it4it.backend.task

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class RequirementService {

    @Autowired
    lateinit var requirementRepository: RequirementRepository

    fun getRequirements(task: Task): List<Requirement>{
        return requirementRepository.findAllByTask(task)
    }

    fun newRequirement(task:Task, newRequirement: NewRequirement){
        val requirement = Requirement(0,task, newRequirement.reqId?.toLong())
        requirementRepository.save(requirement)
    }

    fun deleteRequirement(reqId: Long){
        requirementRepository.delete(requirementRepository.findById(reqId).get())
    }

    fun deleteRequirementByTask(task:Task){
        val requirements = requirementRepository.findAllByTask(task)
        if (requirements.count() > 0) {
            requirements.forEach {
                requirementRepository.delete(it)
            }
        }
    }
}