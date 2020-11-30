package it4it.backend.project.release

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface ReleaseRepository: CrudRepository<Release, Long> {


}