package it4it.backend.controller

import it4it.backend.project.NewProject
import org.springframework.web.bind.annotation.*
import it4it.backend.project.Project
import it4it.backend.project.ProjectRepository
import it4it.backend.project.ProjectService
import it4it.backend.project.release.ReleaseRepository
import it4it.backend.project.release.ReleaseService
import it4it.backend.repository.UserRepository
import it4it.backend.task.*
import it4it.backend.task.link.LinkRepository
import it4it.backend.task.link.LinkService
import it4it.backend.task.link.LinkTypeRepository
import it4it.backend.task.link.NewLink
import it4it.backend.user.NewUser
import it4it.backend.user.User
import it4it.backend.user.role.*
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

    @Autowired
    lateinit var taskRepository: TaskRepository

    @Autowired
    lateinit var taskService: TaskService

    @Autowired
    lateinit var projectService: ProjectService

    @Autowired
    lateinit var releaseService: ReleaseService

    @Autowired
    lateinit var requirementService: RequirementService

    @Autowired
    lateinit var linkService: LinkService

    @GetMapping("/project/all")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @ResponseBody
    fun getProjects(authentication: Authentication): ResponseEntity<*> {
        val user: User = userRepository.findByUsername(authentication.name).get()
        return ResponseEntity.accepted().body(projectService.getProjects(user))
    }

    @PostMapping("/project")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseBody
    fun newProject(authentication: Authentication, @Valid @RequestBody newProject: NewProject): ResponseEntity<*> {
        val user: User = userRepository.findByUsername(authentication.name).get()
        val projectCandidate = projectRepository.findProjectByKey(newProject.key!!)
        return if (!projectCandidate.isPresent) {
            if (projectNameExists(newProject.key!!)) {
                return ResponseEntity(ResponseMessage("Project key is already taken!"), HttpStatus.BAD_REQUEST)
            }
            else {
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
                ResponseEntity(ResponseMessage("Project created successfully!"), HttpStatus.OK)
            }
        } else {
            ResponseEntity(ResponseMessage("Project key already exists!"), HttpStatus.BAD_REQUEST)
        }
    }

    @PutMapping("/project/{projectKey}")
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
            ResponseEntity.accepted().body(project)
        } else {
            ResponseEntity(ResponseMessage("Project does not exists!"), HttpStatus.BAD_REQUEST)
        }
    }

    @GetMapping("/project/{projectKey}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @ResponseBody
    fun getProjectKey(authentication: Authentication, @PathVariable projectKey: String): ResponseEntity<*> {
        val user: User = userRepository.findByUsername(authentication.name).get()
        val projectCandidate = projectRepository.findProjectByKey(projectKey)
        return if (projectCandidate.isPresent) {
            if (user.admin) {
                ResponseEntity.accepted().body(projectCandidate.get())
            } else {
                ResponseEntity.accepted().body(projectCandidate.get())
            }
        }
        else {
            ResponseEntity(ResponseMessage("Project does not exists!"), HttpStatus.BAD_REQUEST)
        }
    }

    @GetMapping("/project/{projectKey}/release/all")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @ResponseBody
    fun getProjectReleases(authentication: Authentication, @PathVariable projectKey: String): ResponseEntity<*> {
        val user: User = userRepository.findByUsername(authentication.name).get()
        val project = projectRepository.findProjectByKey(projectKey)
        return if (project.isPresent) {
            if (user.admin) {
                ResponseEntity.accepted().body(releaseRepository.findReleaseByProject(project.get()))
            } else {
                ResponseEntity.accepted().body(releaseRepository.findReleaseByProject(project.get()))
            }
        }
        else {
            ResponseEntity(ResponseMessage("Project does not exists!"), HttpStatus.BAD_REQUEST)
        }
    }

    @GetMapping("/project/{projectKey}/release/{releaseId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @ResponseBody
    fun getProjectReleaseById(authentication: Authentication, @PathVariable projectKey: String, @PathVariable releaseId: Long): ResponseEntity<*> {
        val user: User = userRepository.findByUsername(authentication.name).get()
        val projectCandidate = projectRepository.findProjectByKey(projectKey)
        return if (projectCandidate.isPresent) {
            if (user.admin) {
                ResponseEntity.accepted().body(releaseRepository.findReleaseById(releaseId))
            } else {
                ResponseEntity.accepted().body(releaseRepository.findReleaseById(releaseId))
            }
        }
        else{
            ResponseEntity(ResponseMessage("Project does not exists!"), HttpStatus.BAD_REQUEST)
        }
    }

    @GetMapping("/project/{projectKey}/users")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @ResponseBody
    fun getAssignedRoles(authentication: Authentication, @PathVariable projectKey: String): ResponseEntity<*> {
        val user: User = userRepository.findByUsername(authentication.name).get()
        val projectCandidate = projectRepository.findProjectByKey(projectKey)
        return if (projectCandidate.isPresent) {
            val project = projectCandidate.get()
            return if (user.admin) {
                ResponseEntity.accepted().body(assignedRoleRepository.findAllByProject(project))
            }
            else {
                ResponseEntity.accepted().body(assignedRoleRepository.findAllByProject(project))
            }
        }
        else{
            ResponseEntity(ResponseMessage("Project does not exists!"), HttpStatus.BAD_REQUEST)
        }
    }

    @PostMapping("/project/{projectKey}/users")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @ResponseBody
    fun newAssignedRoles(authentication: Authentication, @PathVariable projectKey: String, @Valid @RequestBody newAssignedRole: NewAssignedRole): ResponseEntity<*> {
        val user: User = userRepository.findByUsername(authentication.name).get()
        print(newAssignedRole.user)
        print(newAssignedRole.role)
        print(projectKey)
        val userCandidate = userRepository.findByUsername(newAssignedRole.user!!)
        val roleCandidate = roleRepository.findById(newAssignedRole.role!!)
        val projectCandidate = projectRepository.findProjectByKey(projectKey)
        return if (projectCandidate.isPresent and userCandidate.isPresent and roleCandidate.isPresent) {
            val assignedRoleCandidate = assignedRoleRepository.findAllByRoleAndUserAndProject(roleCandidate.get(),userCandidate.get(),projectCandidate.get())
            if (assignedRoleCandidate.count() == 0) {
                return if (user.admin) {
                    val newObject = AssignedRole(
                            0,
                            roleCandidate.get(),
                            userCandidate.get(),
                            projectCandidate.get()
                    )
                    assignedRoleRepository.save(newObject)
                    ResponseEntity.accepted().body(newObject)
                } else {
                    val newObject = AssignedRole(
                            0,
                            roleCandidate.get(),
                            userCandidate.get(),
                            projectCandidate.get()
                    )
                    assignedRoleRepository.save(newObject)
                    ResponseEntity.accepted().body(newObject)
                }
            }
            else{
                ResponseEntity(ResponseMessage("Assigned Role already exists!"),HttpStatus.BAD_REQUEST)
            }
        }
        else{
            ResponseEntity(ResponseMessage("Project,User or Role does not exists!"),HttpStatus.BAD_REQUEST)
        }
    }

    @GetMapping("/status")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @ResponseBody
    fun getStatuses(authentication: Authentication): ResponseEntity<*> {
        val user: User = userRepository.findByUsername(authentication.name).get()
        return  ResponseEntity.accepted().body(taskStatusRepository.findAll())
    }

    @GetMapping("/task")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @ResponseBody
    fun getTasks(authentication: Authentication): ResponseEntity<*> {
        val user: User = userRepository.findByUsername(authentication.name).get()
        return if (user.admin) {
            ResponseEntity.accepted().body(taskRepository.findAll())
        }
        else{
            val tasks: MutableList<Task> = emptyList<Task>().toMutableList<Task>()
            val assignedRoles = assignedRoleRepository.findAllByUser(user)
            assignedRoles.forEach { role ->
                val projectTasks = taskRepository.findAllByProject(role.project)
                projectTasks.forEach{task ->
                    tasks.add(task)
                }
            }
            ResponseEntity.accepted().body(tasks.toList())
        }
    }

    @PostMapping("/task")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @ResponseBody
    fun newTask(authentication: Authentication,@Valid @RequestBody newTask: NewTask): ResponseEntity<*> {
        val user: User = userRepository.findByUsername(authentication.name).get()
        return if ((newTask.assignee != null) and (newTask.summary != null) and (newTask.project != null) ) {
            val task = taskService.newTask(user,newTask)
            ResponseEntity.accepted().body(task)
        }
        else{
            ResponseEntity(ResponseMessage("Not enough data!"),HttpStatus.BAD_REQUEST)
        }
    }

    @PutMapping("/task/{taskId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @ResponseBody
    fun updateTask(authentication: Authentication, @Valid @RequestBody newTask: NewTask, @PathVariable taskId: Long): ResponseEntity<*> {
        val user: User = userRepository.findByUsername(authentication.name).get()
        val task = taskRepository.findById(taskId)
        return if (task.isPresent) {
            taskService.updateTask(user, task.get(), newTask)
            ResponseEntity.accepted().body(task.get())
        }
        else{
            ResponseEntity(ResponseMessage("Task is not updated!"),HttpStatus.BAD_REQUEST)
        }
    }

    @DeleteMapping("/task/{taskId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @ResponseBody
    fun deleteTask(authentication: Authentication, @PathVariable taskId: Long): ResponseEntity<*> {
        val user: User = userRepository.findByUsername(authentication.name).get()
        return ResponseEntity(ResponseMessage(taskService.deleteTaskById(taskId)),HttpStatus.OK)
    }

    @GetMapping("/task/{taskId}/requirements")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @ResponseBody
    fun getTaskRequirements(authentication: Authentication, @PathVariable taskId: Long): ResponseEntity<*> {
        val user: User = userRepository.findByUsername(authentication.name).get()
        val task = taskService.getTaskById(taskId)
        return if (task.isPresent) {
            ResponseEntity.accepted().body(requirementService.getRequirements(task.get()))
        }
        else{
            ResponseEntity(ResponseMessage("Task is not exist!"),HttpStatus.BAD_REQUEST)
        }
    }

    @PostMapping("/task/{taskId}/requirements")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @ResponseBody
    fun newTaskRequirements(authentication: Authentication, @Valid @RequestBody newRequirement: NewRequirement, @PathVariable taskId: Long): ResponseEntity<*> {
        val user: User = userRepository.findByUsername(authentication.name).get()
        val task = taskService.getTaskById(taskId)
        return if (task.isPresent) {
            requirementService.newRequirement(task.get(),newRequirement)
            ResponseEntity.accepted().body(requirementService.getRequirements(task.get()))
        }
        else{
            ResponseEntity(ResponseMessage("Task is not exist!"),HttpStatus.BAD_REQUEST)
        }
    }

    @DeleteMapping("/task/{taskId}/requirements/{reqId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @ResponseBody
    fun deleteTaskRequirements(authentication: Authentication, @PathVariable taskId: Long, @PathVariable reqId: Long): ResponseEntity<*> {
        val task = taskService.getTaskById(taskId)
        requirementService.deleteRequirement(reqId)
        return ResponseEntity.accepted().body(requirementService.getRequirements(task.get()))
    }

    @GetMapping("/task/{taskId}/inwardLinks")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @ResponseBody
    fun getInwardTaskLink(authentication: Authentication, @PathVariable taskId: Long): ResponseEntity<*> {
        val user: User = userRepository.findByUsername(authentication.name).get()
        val task = taskService.getTaskById(taskId)
        return if (task.isPresent) {
            ResponseEntity.accepted().body(linkService.getInwardLinks(task.get()))
        }
        else{
            ResponseEntity(ResponseMessage("Task is not exist!"),HttpStatus.BAD_REQUEST)
        }
    }

    @GetMapping("/task/{taskId}/outwardLinks")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @ResponseBody
    fun getOutwardTaskLink(authentication: Authentication, @PathVariable taskId: Long): ResponseEntity<*> {
        val user: User = userRepository.findByUsername(authentication.name).get()
        val task = taskService.getTaskById(taskId)
        return if (task.isPresent) {

            ResponseEntity.accepted().body(linkService.getOutwardLinks(task.get()))
        }
        else{
            ResponseEntity(ResponseMessage("Task is not exist!"),HttpStatus.BAD_REQUEST)
        }
    }

    @PostMapping("/task/{taskId}/link")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @ResponseBody
    fun newTaskLink(authentication: Authentication, @Valid @RequestBody newLink: NewLink, @PathVariable taskId: Long): ResponseEntity<*> {
        val user: User = userRepository.findByUsername(authentication.name).get()
        val task = taskService.getTaskById(taskId)
        return if (task.isPresent) {
            linkService.newLink(newLink.linkType!!,newLink.inward!!,newLink.outward!!)
            ResponseEntity.accepted().body(requirementService.getRequirements(task.get()))
        }
        else{
            ResponseEntity(ResponseMessage("Task is not exist!"),HttpStatus.BAD_REQUEST)
        }
    }

    @GetMapping("/user/all")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @ResponseBody
    fun getUsers(authentication: Authentication): ResponseEntity<*> {
        val user: User = userRepository.findByUsername(authentication.name).get()
        return if (user.admin) {
            ResponseEntity.accepted().body(userRepository.findAll())
        }
        else{
            ResponseEntity.accepted().body(userRepository.findAll())
        }
    }

    @GetMapping("/user/currentUser")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @ResponseBody
    fun getCurrentUser(authentication: Authentication): ResponseEntity<*> {
        val user: User = userRepository.findByUsername(authentication.name).get()
        return ResponseEntity.accepted().body(user)
    }

    @GetMapping("/user/currentUserRoles")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @ResponseBody
    fun getCurrentUserRoles(authentication: Authentication): ResponseEntity<*> {
        val user: User = userRepository.findByUsername(authentication.name).get()
        return ResponseEntity.accepted().body(assignedRoleRepository.findAllByUser(user))
    }

    @PutMapping("/user/{username}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @ResponseBody
    fun updateUser(authentication: Authentication, @PathVariable username: String, @Valid @RequestBody newUser: NewUser): ResponseEntity<*> {
        val user: User = userRepository.findByUsername(authentication.name).get()
        val candidateUser = userRepository.findByUsername(username)
        return if (candidateUser.isPresent) {
            if (user.admin) {
                updateUserObject(candidateUser,newUser)
                ResponseEntity.accepted().body(userRepository.findByUsername(username).get())
            } else{
                ResponseEntity.accepted().body(userRepository.findByUsername(username).get())
            }
        } else {
            ResponseEntity(ResponseMessage("User does not exists!"),HttpStatus.BAD_REQUEST)
        }
    }

    @GetMapping("/role/all")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @ResponseBody
    fun getRoles(authentication: Authentication): ResponseEntity<*> {
        val user: User = userRepository.findByUsername(authentication.name).get()
        return if (user.admin) {
            ResponseEntity.accepted().body(roleRepository.findAll())
        }
        else{
            ResponseEntity.accepted().body(roleRepository.findAll())
        }
    }

    @PostMapping("/role")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseBody
    fun newRoles(authentication: Authentication, @Valid @RequestBody newRole: Role): ResponseEntity<*> {
        val user: User = userRepository.findByUsername(authentication.name).get()
        val roleCandidate = roleRepository.findByName(newRole.name!!)
        if (!roleCandidate.isPresent){
            newRole.id = 0
            roleRepository.save(newRole)
        }
        return ResponseEntity.accepted().body(roleRepository.findAll())
    }

    @GetMapping("/links")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseBody
    fun getLinks(authentication: Authentication): ResponseEntity<*> {
        val user: User = userRepository.findByUsername(authentication.name).get()
        return ResponseEntity.accepted().body(linkTypeRepository.findAll())
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
        val updateUser: User = candidateUser.get()
        if ((newUser.name != null) and (newUser.name != updateUser.name)) {
            updateUser.name = newUser.name
        }
        if ((newUser.username != null) and (newUser.username != updateUser.username)) {
            updateUser.username = newUser.username
        }
        if ((newUser.email != null) and (newUser.email != updateUser.email)) {
            updateUser.email = newUser.email
        }
        if (newUser.credential != null) {
            updateUser.setCredential(newUser.credential)
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