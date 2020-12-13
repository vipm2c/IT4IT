package it4it.backend.user.role

import it4it.backend.project.ProjectRepository
import it4it.backend.project.release.ReleaseRepository
import it4it.backend.repository.UserRepository
import it4it.backend.task.TaskRepository
import it4it.backend.user.User
import it4it.backend.task.TaskStatusRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class RoleService {

    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var taskRepository: TaskRepository

    @Autowired
    lateinit var roleRepository: RoleRepository

    @Autowired
    lateinit var assignedRoleRepository: AssignedRoleRepository

    @Autowired
    lateinit var taskStatusRepository: TaskStatusRepository

    @Autowired
    lateinit var releaseRepository: ReleaseRepository

    @Autowired
    lateinit var projectRepository: ProjectRepository

    fun getUserProjects(user:User): List<AssignedRole>{
        return assignedRoleRepository.findAllByUser(user)
    }

    fun getUserManagerProjects(user:User): List<AssignedRole>{
        val managerRole = roleRepository.findByName("Manager").get()
        return assignedRoleRepository.findAllByRoleAndUser(managerRole,user)
    }

}