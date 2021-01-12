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

        @ManyToOne(cascade = [CascadeType.ALL])
        @JoinColumn(name = "inward", referencedColumnName = "id", insertable = false, updatable = false)
        val inward: Task?,

        @ManyToOne(cascade = [CascadeType.ALL])
        @JoinColumn(name = "outward", referencedColumnName = "id", insertable = false, updatable = false )
        val outward: Task?
)