package it4it.backend.task

import it4it.backend.project.ProjectRepository
import it4it.backend.project.release.ReleaseRepository
import it4it.backend.repository.UserRepository
import it4it.backend.user.User
import it4it.backend.web.response.ResponseMessage
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.RequestBody
import java.util.*
import javax.validation.Valid

@Service
class TaskService {

    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var taskRepository: TaskRepository

    @Autowired
    lateinit var taskStatusRepository: TaskStatusRepository

    @Autowired
    lateinit var releaseRepository: ReleaseRepository

    @Autowired
    lateinit var projectRepository: ProjectRepository

    fun newTask(user: User, newTask: NewTask): Task{
        print(user.username)
        val reporter = userRepository.findByUsername(user.username!!).get()
        val project = projectRepository.findProjectByKey(newTask.project!!).get()
        val status = taskStatusRepository.findById(2).get()
        val assignee = userRepository.findByUsername(newTask.assignee!!).get()
        project.count = project.count?.plus(1)
        projectRepository.save(project)
        val task = Task(
                0,
                project,
                newTask.summary,
                newTask.description,
                null,
                "${project.key}-${project.count}",
                status,
                assignee,
                reporter,
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
        return task
    }

    fun updateTask(user: User, task: Task, newTask: NewTask): Task{
        if (newTask.status != null) {
            val status = taskStatusRepository.findById(newTask.status!!)
            if(status.isPresent) {
                task.status = status.get()
            }
        }
        if (newTask.assignee != null) {
            val assignee = userRepository.findByUsername(newTask.assignee!!)
            if(assignee.isPresent) {
                task.assignee = assignee.get()
            }
        }
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
        if ((newTask.summary != null) and (newTask.summary != task.summary) ){
            task.summary = newTask.summary
        }
        if ((newTask.description != null) and (newTask.description != task.description) ){
            task.description = newTask.description
        }
        taskRepository.save(task)
        return task
    }

    fun save(task: Task){
        taskRepository.save(task)
    }

    fun getTaskByKey(taskKey: String): Optional<Task>{
        return taskRepository.findByKey(taskKey)
    }

    fun getTaskById(taskId: Long): Optional<Task> {
        return taskRepository.findById(taskId)
    }


}