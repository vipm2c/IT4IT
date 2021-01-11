package it4it.backend.project

import it4it.backend.user.User
import it4it.backend.user.role.AssignedRoleRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.stereotype.Service
import java.util.stream.Collectors

@Service
class ProjectService {

    @Autowired
    lateinit var projectRepository: ProjectRepository

    @Autowired
    lateinit var assignedRoleRepository: AssignedRoleRepository

    fun getProjects(user: User): List<Project>{
        val projects: List<Project>
        if (user.admin){
            projects = projectRepository.findAll()
            projects.forEach { project ->
                project.role = "admin"
                val roles = assignedRoleRepository.findAllByProjectAndUser(project,user)
                if (roles.count() != 0){
                    roles.forEach { role ->
                        project.role += ",${role.role.name!!.toLowerCase()}"
                    }
                }
            }
        }
        else {
            val assignedRoles = assignedRoleRepository.findAllByUser(user)
            val cp: MutableList<Project> = mutableListOf<Project>()
            assignedRoles.forEach{
                if (it.role.name == "Manager"){
                    it.project.role = "manager"
                }
                else{
                    it.project.role = "user"
                }

                cp.add(it.project)
            }
            projects = cp.toList<Project>()
        }
        return projects
    }
}