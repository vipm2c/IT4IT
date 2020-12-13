package it4it.backend.task

import it4it.backend.project.Project
import it4it.backend.project.release.Release
import it4it.backend.user.User
import javax.persistence.*
import javax.validation.constraints.Negative

@Entity
@Table(name="task")
class Task (

        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        val id: Long,

        @OneToOne(cascade = [CascadeType.ALL])
        @JoinColumn(name = "project", referencedColumnName = "id")
        val project: Project?,

        @Column(nullable = true)
        var summary: String?,

        @Column(nullable = true)
        var description: String?,

        @OneToOne(cascade = [CascadeType.ALL])
        @JoinColumn(name = "fix_version", referencedColumnName = "id")
        var fixVersion: Release?,

        @Column(nullable = true)
        val key: String?,

        @OneToOne(cascade = [CascadeType.ALL])
        @JoinColumn(name = "status", referencedColumnName = "id")
        var status: TaskStatus?,

        @OneToOne(cascade = [CascadeType.ALL])
        @JoinColumn(name = "assignee", referencedColumnName = "id")
        var assignee: User?,

        @OneToOne(cascade = [CascadeType.ALL])
        @JoinColumn(name = "reporter", referencedColumnName = "id")
        var reporter: User,

        @OneToOne(cascade = [CascadeType.ALL])
        @JoinColumn(name = "affected_version", referencedColumnName = "id")
        var affectedVersion: Release?
)