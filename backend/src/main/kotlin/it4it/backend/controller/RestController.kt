package it4it.backend.controller

import it4it.backend.project.NewProject
import org.springframework.web.bind.annotation.*
import it4it.backend.project.Project
import it4it.backend.project.ProjectRepository
import it4it.backend.project.release.Release
import it4it.backend.project.release.ReleaseRepository
import it4it.backend.repository.UserRepository
import it4it.backend.task.NewTask
import it4it.backend.task.Task
import it4it.backend.task.TaskRepository
import it4it.backend.task.TaskStatusRepository
import it4it.backend.task.link.LinkRepository
import it4it.backend.task.link.LinkTypeRepository
import it4it.backend.user.NewUser
import it4it.backend.user.User
import it4it.backend.user.role.*
import it4it.backend.web.response.ResponseMessage
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.expression.spel.ast.Assign
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

    @GetMapping("/project/all")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @ResponseBody
    fun getProjects(authentication: Authentication): ResponseEntity<*> {
        val user: User = userRepository.findByUsername(authentication.name).get()
        return if (user.admin) {
            ResponseEntity.accepted().body(projectRepository.findAll())
        }
        else{
            ResponseEntity.accepted().body(projectRepository.findAll())
        }
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
                ResponseEntity.accepted().body(releaseRepository.findReleaseByProject(project.get().id))
            } else {
                ResponseEntity.accepted().body(releaseRepository.findReleaseByProject(project.get().id))
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
            ResponseEntity.accepted().body(taskStatusRepository.findAll())
        }
        else{
            lateinit var tasks: List<Task>
            val assignedRoles = assignedRoleRepository.findAllByUser(user)
            assignedRoles.forEach { role ->
                taskRepository.findAllByProject(role.project!!).forEach{task ->
                    tasks.plus(task)
                }
            }
            ResponseEntity.accepted().body(tasks)
        }
    }

    @PostMapping("/task")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @ResponseBody
    fun newTask(authentication: Authentication,@Valid @RequestBody newTask: NewTask): ResponseEntity<*> {
        val user: User = userRepository.findByUsername(authentication.name).get()
        return if ((newTask.assignee != null) and (newTask.summary != null) and (newTask.project != null) ) {
            val project = projectRepository.findProjectById(newTask.project!!).get()
            val status = taskStatusRepository.findById(2).get()
            val assignee = userRepository.findById(newTask.assignee!!).get()
            project.count = project.count?.plus(1)
            projectRepository.save(project)
            val task = Task(
                    0,
                    project,
                    newTask.summary,
                    newTask.description,
                    null,
                    "$project.key-$project.count",
                    status,
                    assignee,
                    user,
                    null
            )
            if (newTask.fixVersion != null){
                val fixVersion = releaseRepository.findReleaseById(newTask.fixVersion!!)
                if (fixVersion.isPresent){
                    task.fixVersion = fixVersion.get()
                }
            }
            if (newTask.affectedVersion != null){
                val affectedVersion = releaseRepository.findReleaseById(newTask.affectedVersion!!)
                if (affectedVersion.isPresent){
                    task.affectedVersion = affectedVersion.get()
                }
            }
            taskRepository.save(task)
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
            if (newTask.status != null) {
                val status = taskStatusRepository.findById(newTask.status!!)
                if(status.isPresent) {
                    task.get().status = status.get()
                }
            }
            if (newTask.assignee != null) {
                val assignee = userRepository.findById(newTask.assignee!!)
                if(assignee.isPresent) {
                    task.get().assignee = assignee.get()
                }
            }
            if (newTask.fixVersion != null){
                val fixVersion = releaseRepository.findReleaseById(newTask.fixVersion!!)
                if (fixVersion.isPresent){
                    task.get().fixVersion = fixVersion.get()
                }
            }
            if (newTask.affectedVersion != null){
                val affectedVersion = releaseRepository.findReleaseById(newTask.affectedVersion!!)
                if (affectedVersion.isPresent){
                    task.get().affectedVersion = affectedVersion.get()
                }
            }
            if ((newTask.summary != null) and (newTask.summary != task.get().summary) ){
                task.get().summary = newTask.summary
            }
            if ((newTask.description != null) and (newTask.description != task.get().description) ){
                task.get().description = newTask.description
            }
            taskRepository.save(task.get())
            ResponseEntity.accepted().body(task.get())
        }
        else{
            ResponseEntity(ResponseMessage("Task is not updated!"),HttpStatus.BAD_REQUEST)
        }
    }

    @GetMapping("/user/all")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @ResponseBody
    fun getUsers(authentication: Authentication): ResponseEntity<*> {
        val user: User = userRepository.findByUsername(authentication.name).get()
        return if (user.admin) {
            ResponseEntity.accepted().body(projectRepository.findAll())
        }
        else{
            ResponseEntity.accepted().body(projectRepository.findAll())
        }
    }

    @PostMapping("/user/{username}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @ResponseBody
    fun updateUser(authentication: Authentication, @PathVariable username: String, @Valid @RequestBody newUser: NewUser): ResponseEntity<*> {
        val user: User = userRepository.findByUsername(authentication.name).get()
        val candidateUser = userRepository.findByUsername(authentication.name)
        return if (candidateUser.isPresent) {
            if (user.admin) {
                updateUserObject(candidateUser,newUser)
                ResponseEntity.accepted().body(userRepository.findByUsername(username).get())
            } else{
                if (user.username == newUser.username){
                    updateUserObject(candidateUser,newUser)
                }
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