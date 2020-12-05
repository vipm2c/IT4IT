package it4it.backend.project

import javax.persistence.*

@Entity
@Table(name="project")
class Project (

        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        val id: Long,

        @Column(nullable = true)
        var name: String?,

        @Column(nullable = true)
        var description: String?,

        @Column(nullable = true)
        val key: String?,

        @Column(nullable = true)
        var spec: String?,

        @Column(nullable = true)
        var archived: Boolean?,

        @Column(nullable = true)
        var count: Long?
)