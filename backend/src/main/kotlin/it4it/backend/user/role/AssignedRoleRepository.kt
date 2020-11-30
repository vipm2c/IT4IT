package it4it.backend.user.role

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface AssignedRoleRepository: CrudRepository<AssignedRole, Long> {


}