package it4it.backend.task.link

import it4it.backend.task.Task
import javax.persistence.*

@Entity
@Table(name = "tasklinks")
class Link(

        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        val id: Long,

        @OneToOne(cascade = [CascadeType.REFRESH])
        @JoinColumn(name = "link_type", referencedColumnName = "id")
        val linkType: LinkType?,

        @OneToOne(cascade = [CascadeType.REFRESH])
        @JoinColumn(name = "inward", referencedColumnName = "id")
        val inward: Task?,

        @OneToOne(cascade = [CascadeType.REFRESH])
        @JoinColumn(name = "outward", referencedColumnName = "id")
        val outward: Task?
)