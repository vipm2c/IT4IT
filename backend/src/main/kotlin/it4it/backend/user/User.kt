package it4it.backend.user

import javax.persistence.*

@Entity
@Table (name="users")
data class User(

        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        val id: Long,

        @Column(nullable = true)
        val name: String?,

        @Column(nullable = true)
        val username: String?,

        @Column(nullable = true)
        val email: String?,

        @Column(nullable = true)
        val credential: String?,

        @Column(nullable = true)
        val active: Boolean?,

        @Column(nullable = true)
        val admin: Boolean?
)