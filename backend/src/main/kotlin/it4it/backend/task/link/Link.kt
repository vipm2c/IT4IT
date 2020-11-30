package it4it.backend.task.link

import javax.persistence.*
import it4it.backend.task.Task

@Entity
@Table(name="tasklinks")
class Link (

        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        val id: Long,

        @OneToOne(fetch = FetchType.EAGER)
        @JoinTable(
                name = "linktype",
                joinColumns = [JoinColumn(name = "link_type", referencedColumnName = "id")]
        )
        val linkType: LinkType?,

        @OneToOne(fetch = FetchType.EAGER)
        @JoinTable(
                name = "task",
                joinColumns = [JoinColumn(name = "inward", referencedColumnName = "id")]
        )
        val inward: Task?,

        @OneToOne(fetch = FetchType.EAGER)
        @JoinTable(
                name = "task",
                joinColumns = [JoinColumn(name = "outward", referencedColumnName = "id")]
        )
        val outward: Task?
)