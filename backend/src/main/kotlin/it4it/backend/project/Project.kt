package it4it.backend.project

import javax.persistence.*

@Entity
@Table(name="project")
class Project (

        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        val id: Long,

        @Column(nullable = true)
        val name: String?,

        @Column(nullable = true)
        val description: String?,

        @Column(nullable = true)
        val key: String?,

        @Column(nullable = true)
        val spec: String?,

        @Column(nullable = true)
        val archived: Boolean?
)