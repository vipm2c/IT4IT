package it4it.backend.task

import javax.persistence.*

@Entity
@Table(name="taskstatus")
class TaskStatus (

        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        val id: Long,

        @Column(nullable = true)
        val name: String?
)