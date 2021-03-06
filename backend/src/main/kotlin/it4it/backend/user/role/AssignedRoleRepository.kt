package it4it.backend.user.role

import it4it.backend.project.Project
import it4it.backend.user.User
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface AssignedRoleRepository: CrudRepository<AssignedRole, Long> {

    fun findAllByProject(project: Project): List<AssignedRole>

    fun findAllByProjectAndUser(project: Project, user:User): List<AssignedRole>

    fun findAllByUser(user: User): List<AssignedRole>

    fun findAllByRole(role: Role): List<AssignedRole>

    fun findAllByRoleAndUser(role: Role, user: User): List<AssignedRole>

    fun findAllByRoleAndUserAndProject(role: Role, user: User, project: Project): List<AssignedRole>

}