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

        @OneToOne(cascade = [CascadeType.ALL])
        @JoinColumn(name = "role",referencedColumnName = "id")
        val role: Role,

        @OneToOne(cascade = [CascadeType.ALL])
        @JoinColumn(name = "users",referencedColumnName = "id")
        val user: User,

        @OneToOne(cascade = [CascadeType.ALL])
        @JoinColumn(name = "project",referencedColumnName = "id")
        val project: Project
)