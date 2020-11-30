package it4it.backend.user.role

import it4it.backend.project.Project
import it4it.backend.user.User
import javax.persistence.*

@Entity
@Table(name="assignedrole")
class AssignedRole (

        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        val id: Long,

        @OneToOne(fetch = FetchType.EAGER)
        @JoinTable(
                name = "role",
                joinColumns = [JoinColumn(name = "role", referencedColumnName = "id")]
        )
        val role: Role?,

        @OneToOne(fetch = FetchType.EAGER)
        @JoinTable(
                name = "users",
                joinColumns = [JoinColumn(name = "user", referencedColumnName = "id")]
        )
        val user: User?,

        @OneToOne(fetch = FetchType.EAGER)
        @JoinTable(
                name = "project",
                joinColumns = [JoinColumn(name = "project", referencedColumnName = "id")]
        )
        val project: Project?
)