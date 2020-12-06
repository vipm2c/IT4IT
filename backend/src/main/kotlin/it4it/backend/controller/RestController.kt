package it4it.backend.controller

import it4it.backend.project.NewProject
import org.springframework.web.bind.annotation.*
import it4it.backend.project.Project
import it4it.backend.project.ProjectRepository
import it4it.backend.project.release.Release
import it4it.backend.project.release.ReleaseRepository
import it4it.backend.repository.UserRepository
import it4it.backend.task.TaskStatusRepository
import it4it.backend.task.link.LinkRepository
import it4it.backend.task.link.LinkTypeRepository
import it4it.backend.user.NewUser
import it4it.backend.user.User
import it4it.backend.user.role.AssignedRoleRepository
import it4it.backend.user.role.Role
import it4it.backend.user.role.RoleRepository
import it4it.backend.web.response.ResponseMessage
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.RestController
import java.util.*
import java.util.concurrent.atomic.AtomicLong
import javax.validation.Valid


@RestController
@RequestMapping("/api")
class RestController() {

    val counter = AtomicLong()

    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var projectRepository: ProjectRepository

    @Autowired
    lateinit var releaseRepository: ReleaseRepository

    @Autowired
    lateinit var roleRepository: RoleRepository

    @Autowired
    lateinit var assignedRoleRepository: AssignedRoleRepository

    @Autowired
    lateinit var linkRepository: LinkRepository

    @Autowired
    lateinit var taskStatusRepository: TaskStatusRepository

    @Autowired
    lateinit var linkTypeRepository: LinkTypeRepository

    @GetMapping("/project/all")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @ResponseBody
    fun getProjects(authentication: Authentication): MutableIterable<Project> {
        val user: User = userRepository.findByUsername(authentication.name).get()
        if (user.admin) {
            return projectRepository.findAll()
        }
        else{
            return projectRepository.findAll()
        }
    }

    @PostMapping("/project")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseBody
    fun newProject(authentication: Authentication, @Valid @RequestBody newProject: NewProject): ResponseEntity<*> {
        val user: User = userRepository.findByUsername(authentication.name).get()
        val projectCandidate = projectRepository.findProjectByKey(newProject.key!!)
        if (!projectCandidate.isPresent) {
            if (projectNameExists(newProject.key!!)) {
                return ResponseEntity(ResponseMessage("Project key is already taken!"),
                        HttpStatus.BAD_REQUEST)
            }

            val project = Project(
                    0,
                    newProject.name!!,
                    newProject.description!!,
                    newProject.key!!,
                    newProject.spec,
                    archived = false,
                    count = 1
            )

            projectRepository.save(project)

            return ResponseEntity(ResponseMessage("Project created successfully!"), HttpStatus.OK)
        } else {
            return ResponseEntity(ResponseMessage("Project key already exists!"),
                    HttpStatus.BAD_REQUEST)
        }
    }

    @PostMapping("/project/{projectKey}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @ResponseBody
    fun updateProject(@Valid @RequestBody newProject: NewProject, authentication: Authentication, @PathVariable projectKey: String): ResponseEntity<*> {
        val user: User = userRepository.findByUsername(authentication.name).get()
        val projectCandidate = projectRepository.findProjectByKey(projectKey)
        return if (projectCandidate.isPresent) {
            val project = projectCandidate.get()
            if (newProject.name != null && project.name != newProject.name) {
                project.name = newProject.name
            }
            if (newProject.description != null && project.description != newProject.description) {
                project.description = newProject.description
            }
            if (newProject.spec != null && project.spec != newProject.spec) {
                project.spec = newProject.spec
            }
            if (project.archived != newProject.archived) {
                project.archived = newProject.archived
            }

            projectRepository.save(project)

            ResponseEntity(ResponseMessage("Project updated successfully!"), HttpStatus.OK)
        } else {
            ResponseEntity(ResponseMessage("Project does not exists!"),
                    HttpStatus.BAD_REQUEST)
        }
    }

    @GetMapping("/project/{projectKey}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @ResponseBody
    fun getProjectKey(authentication: Authentication, @PathVariable projectKey: String): Optional<Project> {
        val user: User = userRepository.findByUsername(authentication.name).get()
        if (user.admin) {
            return projectRepository.findProjectByKey(projectKey)
        }
        else {
            return projectRepository.findProjectByKey(projectKey)
        }
    }

    @GetMapping("/project/{projectKey}/release/all")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @ResponseBody
    fun getProjectReleases(authentication: Authentication, @PathVariable projectKey: String): List<Optional<Release>> {
        val user: User = userRepository.findByUsername(authentication.name).get()
        val project = projectRepository.findProjectByKey(projectKey).get()
        if (user.admin) {
            return releaseRepository.findReleaseByProject(project.id)
        }
        else {
            return releaseRepository.findReleaseByProject(project.id)
        }
    }

    @GetMapping("/project/{projectKey}/release/{releaseId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @ResponseBody
    fun getProjectReleaseById(authentication: Authentication, @PathVariable projectKey: String, @PathVariable releaseId: Long): Optional<Release> {
        val user: User = userRepository.findByUsername(authentication.name).get()
        val project = projectRepository.findProjectByKey(projectKey).get()
        if (user.admin) {
            return releaseRepository.findReleaseById(releaseId)
        }
        else {
            return releaseRepository.findReleaseById(releaseId)
        }
    }

    @GetMapping("/project/{projectKey}/users")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @ResponseBody
    fun getAssignedRoles(authentication: Authentication, @PathVariable projectKey: String): MutableIterable<Role> {
        val user: User = userRepository.findByUsername(authentication.name).get()
        return if (user.admin) {
            roleRepository.findAll()
        }
        else{
            roleRepository.findAll()
        }
    }

    @GetMapping("/user/all")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @ResponseBody
    fun getUsers(authentication: Authentication): MutableIterable<Project> {
        val user: User = userRepository.findByUsername(authentication.name).get()
        if (user.admin) {
            return projectRepository.findAll()
        }
        else{
            return projectRepository.findAll()
        }
    }

    @PostMapping("/user/{username}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @ResponseBody
    fun updateUser(authentication: Authentication, @PathVariable username: String, @Valid @RequestBody newUser: NewUser): User {
        val user: User = userRepository.findByUsername(authentication.name).get()
        val candidateUser = userRepository.findByUsername(authentication.name)
        return if (candidateUser.isPresent) {
            if (user.admin) {
                updateUserObject(candidateUser,newUser)
                userRepository.findByUsername(username).get()
            } else{
                if (user.username == newUser.username){
                    updateUserObject(candidateUser,newUser)
                }
                userRepository.findByUsername(username).get()
            }
        } else {
            userRepository.findByUsername(username).get()
        }
    }

    @GetMapping("/role/all")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @ResponseBody
    fun getRoles(authentication: Authentication): MutableIterable<Role> {
        val user: User = userRepository.findByUsername(authentication.name).get()
        return if (user.admin) {
            roleRepository.findAll()
        }
        else{
            roleRepository.findAll()
        }
    }

    @PostMapping("/role")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseBody
    fun newRoles(authentication: Authentication, @Valid @RequestBody newRole: Role): MutableIterable<Role> {
        val user: User = userRepository.findByUsername(authentication.name).get()
        val roleCandidate = roleRepository.findByName(newRole.name!!)
        if (!roleCandidate.isPresent){
            newRole.id = 0
            roleRepository.save(newRole)
        }
        return roleRepository.findAll()
    }

    @GetMapping("/usercontent")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @ResponseBody
    fun getUserContent(authentication: Authentication): String {
        val user: User = userRepository.findByUsername(authentication.name).get()
        return "Hello " + user.name + "!"
    }


    @GetMapping("/admincontent")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseBody
    fun getAdminContent(): String {
        return "Admin's content"
    }


    private fun projectNameExists(key: String): Boolean {
        return projectRepository.findProjectByKey(key).isPresent
    }

    private fun updateUserObject(candidateUser: Optional<User>, newUser: NewUser){
        var updateUser: User = candidateUser.get()
        if ((newUser.name != null) and (newUser.name != updateUser.name)) {
            updateUser.name = newUser.name
        }
        if ((newUser.username != null) and (newUser.username != updateUser.username)) {
            updateUser.username = newUser.username
        }
        if ((newUser.email != null) and (newUser.email != updateUser.email)) {
            updateUser.email = newUser.email
        }
        if ((newUser.credential != null) and (newUser.credential != updateUser.credential)) {
            updateUser.credential = newUser.credential
        }
        if (newUser.admin != updateUser.admin) {
            updateUser.admin = newUser.admin
        }
        if (newUser.active != updateUser.active) {
            updateUser.active = newUser.active
        }
        userRepository.save(updateUser)
    }

}