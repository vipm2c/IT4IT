package it4it.backend.task.link

import it4it.backend.task.RequirementRepository
import it4it.backend.task.Task
import it4it.backend.task.TaskRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class LinkService {

    @Autowired
    lateinit var linkRepository: LinkRepository

    @Autowired
    lateinit var linkTypeRepository: LinkTypeRepository

    @Autowired
    lateinit var taskRepository: TaskRepository

    fun getInwardLinks(task:Task):List<Link>{
        return linkRepository.findAllByInward(task)
    }

    fun getOutwardLinks(task:Task):List<Link>{
        return linkRepository.findAllByOutward(task)
    }

    fun newLink(linkTypeId:Long,inwardId:Long,outwardId:Long){
        val inward = taskRepository.findById(inwardId)
        val outward = taskRepository.findById(outwardId)
        val linkType = linkTypeRepository.findById(linkTypeId)
        if(inward.isPresent && outward.isPresent && linkType.isPresent) {
            val link = Link(0, linkType.get(), inward.get(), outward.get())
            linkRepository.save(link)
        }
    }

    fun deleteLink(linkId:Long){
        linkRepository.deleteById(linkId)
    }

}