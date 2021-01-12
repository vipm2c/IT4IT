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
        var version: String?,

        @Column(nullable = true)
        var description: String?,

        @OneToOne(cascade = [CascadeType.ALL])
        @JoinColumn(name = "project", referencedColumnName = "id")
        val project: Project?,

        @Column(nullable = true)
        var released: Boolean?,

        @Column(nullable = true)
        var spec: String?
)