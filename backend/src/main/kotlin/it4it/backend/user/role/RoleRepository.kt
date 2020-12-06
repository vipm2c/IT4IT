package it4it.backend.user.role

import it4it.backend.user.User
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface RoleRepository: CrudRepository<Role, Long> {

    fun findByName(@Param("name") name: String): Optional<Role>

}