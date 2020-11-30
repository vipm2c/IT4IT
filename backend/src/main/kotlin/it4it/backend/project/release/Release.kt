package it4it.backend.project.release

import it4it.backend.project.Project
import javax.persistence.*

@Entity
@Table(name="release")
class Release (

        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        val id: Long,

        @Column(nullable = true)
        val version: String?,

        @Column(nullable = true)
        val description: String?,

        @OneToOne(cascade = [CascadeType.ALL])
        @JoinColumn(name = "project", referencedColumnName = "id")
        val project: Project?,

        @Column(nullable = true)
        val released: Boolean?,

        @Column(nullable = true)
        val spec: String?
)