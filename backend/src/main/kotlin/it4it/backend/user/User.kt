package it4it.backend.user

import javax.persistence.*

@Entity
@Table (name="users")
data class User(

        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        val id: Long,

        @Column(nullable = true)
        var name: String?,

        @Column(nullable = true)
        var username: String?,

        @Column(nullable = true)
        var email: String?,

        @Column(nullable = true)
        private var credential: String?,

        @Column(nullable = true)
        var active: Boolean,

        @Column(nullable = true)
        var admin: Boolean


)
{
        fun setCredential(cred: String?){
                this.credential = cred
        }

        fun getCredential(): String{
                return this.credential!!
        }
}