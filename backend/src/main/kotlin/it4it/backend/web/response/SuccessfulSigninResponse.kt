package it4it.backend.web.response

import org.springframework.security.core.GrantedAuthority

class SuccessfulSigninResponse(var username: String?, val authorities: Collection<GrantedAuthority>) {

}