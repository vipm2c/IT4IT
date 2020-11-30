package it4it.backend.task.link

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table (name="linktype")
class LinkType (

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Long,

    @Column(nullable = true)
    val name: String?,

    @Column(nullable = true)
    val inwardName: String?,

    @Column(nullable = true)
    val outwardName: String?
)