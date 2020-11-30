package it4it.backend.task

import javax.persistence.*

@Entity
@Table(name="requirement")
class Requirement (

        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        val id: Long,

        @OneToOne(fetch = FetchType.EAGER)
        @JoinTable(
                name = "task",
                joinColumns = [JoinColumn(name = "task", referencedColumnName = "id")]
        )
        val task: Task?,

        @Column(nullable = true)
        val reqId: Long?
)