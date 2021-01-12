<template>
  <div div="newtask">
    <div class="login-form">
      <b-card
          title="New Task"
          tag="article"
          style="max-width: 20rem;"
          class="mb-2"
      >
        <b-form>
          <div>
            <b-alert
                :show="dismissCountDown"
                dismissible
                variant="danger"
                @dismissed="dismissCountDown=0"
                @dismiss-count-down="countDownChanged"
            > {{ alertMessage }}
            </b-alert>
          </div>
          <div>
            <b-alert variant="success" :show="successfullyCreated">
              You have been successfully create task!
              <hr />
              <router-link to="/tasks">
                <b-button variant="primary">View Tasks</b-button>
              </router-link>
            </b-alert>
          </div>
          <div>
            <div class="mt-2">Project</div>
            <b-form-select required placeholder="Project" v-model="project" :options="projects" v-on:change="loadFieldContent(project)" />

            <div class="mt-2">Summary</div>
            <b-form-input required type="text" placeholder="Summary" v-model="summary" />

            <div class="mt-2">Description</div>
            <b-form-textarea type="text" placeholder="Description" v-model="description" rows="3" max-rows="6" />

            <div class="mt-2">Assignee</div>
            <b-form-select placeholder="Assignee" v-model="assignee" :options="users" />

            <div class="mt-2">Fix Version</div>
            <b-form-select placeholder="Fix Version" v-model="fixVersion" :options="releases" />

            <div class="mt-2">Affected Version</div>
            <b-form-select placeholder="Affected Version" v-model="affectedVersion" :options="releases" />
            <br>
            <br>

          </div>
          <b-button v-on:click="create" variant="primary">Create</b-button>
        </b-form>
      </b-card>
    </div>
  </div>
</template>

<script>
import {AXIOS} from '@/components/http-commons'

export default {
  name: 'NewTask',
  data () {
    return {
      project: '',
      projects: [],
      users: [],
      releases: [],
      summary: '',
      status: 2,
      description: '',
      fixVersion: null,
      assignee: '',
      affectedVersion: null,
      dismissSecs: 5,
      dismissCountDown: 0,
      alertMessage: '',
      successfullyCreated: false
    }
  },
  mounted() {
    // Set the initial number of items
    this.loadProjects();
  },
  methods: {
    create: function () {
      if (this.$data.project === '' || this.$data.project == null) {
        this.$data.alertMessage = 'Please, fill "Project" field';
        this.showAlert();
      } else if (this.$data.summary === '' || this.$data.summary == null) {
        this.$data.alertMessage = 'Please, fill "Summary" field';
        this.showAlert();
      } else if (this.$data.assignee === '' || this.$data.assignee == null) {
        this.$data.alertMessage = 'Please, fill "Assignee" field';
        this.showAlert();
      } else {
        const newTask = {
          'project': this.$data.project,
          'summary': this.$data.summary,
          'status': this.$data.status,
          'description': this.$data.description,
          'fixVersion': this.$data.fixVersion,
          'assignee': this.$data.assignee,
          'affectedVersion': this.$data.affectedVersion
        };
        console.log(newTask)
        AXIOS.post('/task', newTask)
            .then(response => {
              console.log(response);
              this.successAlert();
            }, error => {
              this.$data.alertMessage = (error.response.data.message.length < 150) ? error.response.data.message : 'Request error. Please, report this error website owners'
              this.showAlert();
            })
            .catch(error => {
              console.log(error);
              this.$data.alertMessage = 'Request error. Please, report this error website owners';
              this.showAlert();
            });
      }
    },
    loadFieldContent(projectKey){
      AXIOS.get('/project/'+projectKey+"/users")
          .then(response => {
            console.log(response.data);
            response.data.forEach(object => {
              const item = {
                value: object.user.username,
                text: object.user.name+" ("+object.user.username+")"
              };
              this.users.push(item)
            })
          }, error => {
            this.$data.alertMessage = (error.response.data.message.length < 150) ? error.response.data.message : 'Request error. Please, report this error website owners'
            this.showAlert();
          })
          .catch(error => {
            console.log(error);
            this.$data.alertMessage = 'Request error. Please, report this error website owners';
            this.showAlert();
          });
      AXIOS.get('/project/'+projectKey+"/release/all")
          .then(response => {
            console.log(response.data);
            response.data.forEach(object => {
              const item = {
                value: object.id,
                text: object.version
              };
              this.releases.push(item)
            })
          }, error => {
            this.$data.alertMessage = (error.response.data.message.length < 150) ? error.response.data.message : 'Request error. Please, report this error website owners'
            this.showAlert();
          })
          .catch(error => {
            console.log(error);
            this.$data.alertMessage = 'Request error. Please, report this error website owners';
            this.showAlert();
          });
    },
    loadProjects(){
      AXIOS.get('/project/all')
          .then(response => {
            console.log(response.data);
            response.data.forEach(object => {
            if (object.role !== "admin"){
              const item = {
                value: object.key,
                text: object.name + " (" + object.key + ")"
              };
              this.projects.push(item)
            }
            })
          }, error => {
            this.$data.alertMessage = (error.response.data.message.length < 150) ? error.response.data.message : 'Request error. Please, report this error website owners'
            this.showAlert();
          })
          .catch(error => {
            console.log(error);
            this.$data.alertMessage = 'Request error. Please, report this error website owners';
            this.showAlert();
          });
    },
    countDownChanged(dismissCountDown) {
      this.dismissCountDown = dismissCountDown
    },
    showAlert() {
      this.dismissCountDown = this.dismissSecs
    },
    successAlert() {
      this.project = ''
      this.summary = ''
      this.status = 2
      this.description = ''
      this.fixVersion =  ''
      this.assignee = ''
      this.affectedVersion = ''
      this.successfullyCreated = true
    }
  }
}
</script>

<style scoped>

</style>