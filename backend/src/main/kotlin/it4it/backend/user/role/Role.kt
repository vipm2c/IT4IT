package it4it.backend.user.role

import javax.persistence.*

@Entity
@Table(name="role")
class Role (

        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        val id: Long,

        @Column(nullable = true)
        val name: String?
)