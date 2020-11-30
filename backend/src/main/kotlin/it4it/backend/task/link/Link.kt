package it4it.backend.task.link

import javax.persistence.*
import it4it.backend.task.Task

@Entity
@Table(name="tasklinks")
class Link (

        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        val id: Long,

        @OneToOne(cascade = [CascadeType.ALL])
        @JoinColumn(name = "link_type", referencedColumnName = "id")
        val linkType: LinkType?,

        @OneToOne(cascade = [CascadeType.ALL])
        @JoinColumn(name = "inward", referencedColumnName = "id")
        val inward: Task?,

        @OneToOne(cascade = [CascadeType.ALL])
        @JoinColumn(name = "outward", referencedColumnName = "id")
        val outward: Task?
)