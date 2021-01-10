package it4it.backend.project.release

import it4it.backend.project.ProjectRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class ReleaseService {

    @Autowired
    lateinit var releaseRepository: ReleaseRepository

    @Autowired
    lateinit var projectRepository: ProjectRepository

    
}