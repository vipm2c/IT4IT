package it4it.backend.controller

import it4it.backend.project.NewProject
import org.springframework.web.bind.annotation.*
import it4it.backend.project.Project
import it4it.backend.project.ProjectRepository
import it4it.backend.project.release.Release
import it4it.backend.project.release.ReleaseRepository
import it4it.backend.user.UserRepository
import it4it.backend.user.User
import it4it.backend.web.response.ResponseMessage
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import java.util.*
import java.util.concurrent.atomic.AtomicLong
import javax.validation.Valid


@RestController
@RequestMapping("/api")
class BackendController() {

    val counter = AtomicLong()

    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var projectRepository: ProjectRepository

    @Autowired
    lateinit var releaseRepository: ReleaseRepository

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/users")
    fun users() = userRepository.findAll()

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
                    count = 0
            )

            projectRepository.save(project)

            return ResponseEntity(ResponseMessage("Project created successfully!"), HttpStatus.OK)
        } else {
            return ResponseEntity(ResponseMessage("Project key already exists!"),
                    HttpStatus.BAD_REQUEST)
        }
    }

    @PutMapping("/project")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseBody
    fun updateProject(authentication: Authentication, @Valid @RequestBody newProject: NewProject): ResponseEntity<*> {
        val user: User = userRepository.findByUsername(authentication.name).get()
        val projectCandidate = projectRepository.findProjectByKey(newProject.key!!)
        return if (!projectCandidate.isPresent) {
            val project = projectCandidate.get()
            project.name = newProject.name
            project.description = newProject.description
            project.spec = newProject.spec

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

}