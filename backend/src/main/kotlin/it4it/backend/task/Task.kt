package it4it.backend.task

import it4it.backend.project.Project
import it4it.backend.project.release.Release
import it4it.backend.user.User
import javax.persistence.*

@Entity
@Table(name="task")
class Task (
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        val id: Long,

        @OneToOne(fetch = FetchType.EAGER)
        @JoinTable(
                name = "project",
                joinColumns = [JoinColumn(name = "project", referencedColumnName = "id")]
        )
        val project: Project?,

        @Column(nullable = true)
        val summary: String?,

        @Column(nullable = true)
        val description: String?,

        @OneToOne(fetch = FetchType.EAGER)
        @JoinTable(
                name = "release",
                joinColumns = [JoinColumn(name = "fix_version", referencedColumnName = "id")]
        )
        val fixVersion: Release?,

        @Column(nullable = true)
        val key: String?,

        @OneToOne(fetch = FetchType.EAGER)
        @JoinTable(
                name = "taskstatus",
                joinColumns = [JoinColumn(name = "status", referencedColumnName = "id")]
        )
        val status: TaskStatus?,

        @OneToOne(fetch = FetchType.EAGER)
        @JoinTable(
                name = "users",
                joinColumns = [JoinColumn(name = "assignee", referencedColumnName = "id")]
        )
        val assignee: User?,

        @OneToOne(fetch = FetchType.EAGER)
        @JoinTable(
                name = "users",
                joinColumns = [JoinColumn(name = "reporter", referencedColumnName = "id")]
        )
        val reporter: User?,

        @OneToOne(fetch = FetchType.EAGER)
        @JoinTable(
                name = "release",
                joinColumns = [JoinColumn(name = "affected_version", referencedColumnName = "id")]
        )
        val affectedVersion: Release?
)