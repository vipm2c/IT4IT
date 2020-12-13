package it4it.backend.task

import javax.persistence.*

@Entity
@Table(name="requirement")
class Requirement (

        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        val id: Long,

        @OneToOne(cascade = [CascadeType.ALL])
        @JoinColumn(name = "task", referencedColumnName = "id")
        val task: Task?,

        @Column(nullable = true)
        val reqId: Long?
)