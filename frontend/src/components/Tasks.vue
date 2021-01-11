<template>
  <div div="tasks">
    <h2>Tasks</h2>

    <b-container fluid>
      <!-- User Interface controls -->
      <b-row>

        <b-col lg="6" class="my-1">
          <b-form-group
              label="Filter"
              label-cols-sm="3"
              label-align-sm="right"
              label-size="sm"
              label-for="filterInput"
              class="mb-0"
          >
            <b-input-group size="sm">
              <b-form-input
                  v-model="filter"
                  type="search"
                  id="filterInput"
                  placeholder="Type to Search"
              ></b-form-input>
              <b-input-group-append>
                <b-button :disabled="!filter" @click="filter = ''">Clear</b-button>
              </b-input-group-append>
            </b-input-group>
          </b-form-group>
        </b-col>

        <b-col lg="6" class="my-1">
          <b-form-group
              label="Filter On"
              label-cols-sm="3"
              label-align-sm="right"
              label-size="sm"
              description="Leave all unchecked to filter on all data"
              class="mb-0">
            <b-form-checkbox-group v-model="filterOn" class="mt-1">
              <b-form-checkbox value="key">Key</b-form-checkbox>
              <b-form-checkbox value="name">Project Name</b-form-checkbox>
              <b-form-checkbox value="assignee">Assignee</b-form-checkbox>
              <b-form-checkbox value="archived">Archived</b-form-checkbox>
            </b-form-checkbox-group>
          </b-form-group>
        </b-col>

        <b-col sm="5" md="6" class="my-1">
          <b-form-group
              label="Per page"
              label-cols-sm="6"
              label-cols-md="4"
              label-cols-lg="3"
              label-align-sm="right"
              label-size="sm"
              label-for="perPageSelect"
              class="mb-0"
          >
            <b-form-select
                v-model="perPage"
                id="perPageSelect"
                size="sm"
                :options="pageOptions"
            ></b-form-select>
          </b-form-group>
        </b-col>

        <b-col sm="7" md="6" class="my-1">
          <b-pagination
              v-model="currentPage"
              :total-rows="totalRows"
              :per-page="perPage"
              align="fill"
              size="sm"
              class="my-0"
          ></b-pagination>
        </b-col>
      </b-row>

      <!-- Main table element -->
      <b-table
          show-empty
          small
          stacked="md"
          :items="items"
          :fields="fields"
          :current-page="currentPage"
          :per-page="perPage"
          :filter="filter"
          :fixed=true
          :filter-included-fields="filterOn"
          :sort-by.sync="sortBy"
          :sort-desc.sync="sortDesc"
          :sort-direction="sortDirection"
          @filtered="onFiltered"
      >
        <template #cell(key)="row">
          {{ row.value }}
        </template>

        <template #cell(assignee)="row">
          {{ row.value.name }}
        </template>

        <template #cell(status)="row">
          {{ row.value.name }}
        </template>

        <template #cell(actions)="row">
          <b-button v-b-modal.infoModal size="sm" @click="info(row.item)" class="mr-1">
            Info
          </b-button>
          <b-button v-b-modal.editModal size="sm" @click="info(row.item)" class="mr-1">
            Edit
          </b-button>
          <b-button size="sm" @click="setDeleteModal(row.item.id)" class="mr-1" v-if="row.item.role === 'Manager'" variant="danger">
            Delete
          </b-button>
        </template>

      </b-table>

      <b-modal id="infoModal" :title="infoModal.title" ok-only v-on:ok="resetInfoModal"  @hide="resetInfoModal">
        <div>
          <div>
            <div class="mt-2">Project</div>
            <b-form-select disabled placeholder="Project" v-model="infoModal.project" :options="infoModal.projects" v-on:change="loadFieldsContent(infoModal.project)" />

            <div class="mt-2">Status</div>
            <b-form-select disabled placeholder="Status" v-model="infoModal.status" :options="infoModal.statuses" />

            <div class="mt-2">Summary</div>
            <b-form-input disabled type="text" placeholder="Summary" v-model="infoModal.summary" />

            <div class="mt-2">Description</div>
            <b-form-textarea disabled type="text" placeholder="Description" v-model="infoModal.description" rows="3" max-rows="6" />

            <div class="mt-2">Assignee</div>
            <b-form-select disabled placeholder="Assignee" v-model="infoModal.assignee" :options="infoModal.users" />

            <div class="mt-2">Reporter</div>
            <b-form-select disabled placeholder="Assignee" v-model="infoModal.reporter" :options="infoModal.users" />

            <div class="mt-2">Fix Version</div>
            <b-form-select disabled placeholder="Fix Version" v-model="infoModal.fixVersion" :options="infoModal.releases" />

            <div class="mt-2">Affected Version</div>
            <b-form-select disabled placeholder="Affected Version" v-model="infoModal.affectedVersion" :options="infoModal.releases" />
            <br>

            <br>
            <b-table
                show-empty
                small
                stacked="md"
                :items="infoModal.requirements.items"
                :fields="infoModal.requirements.fields"
                :fixed=true
                :sort-by.sync="sortBy"
                :sort-desc.sync="sortDesc"
                :sort-direction="sortDirection"
            >
            </b-table>
          </div>
        </div>
      </b-modal>

      <b-modal id="editModal" :title="infoModal.title" v-on:ok="edit(infoModal.row)" v-on:cancel="resetInfoModal" @hide="resetInfoModal" hide-footer>
        <div>
          <div>
            <b-form @submit="edit()" @reset="resetInfoModal()">
              <div class="mt-2">Project</div>
              <b-form-select disabled placeholder="Project" v-model="infoModal.project" :options="infoModal.projects" v-on:change="loadFieldsContent(infoModal.project)" />

              <div class="mt-2">Status</div>
              <b-form-select placeholder="Status" v-model="infoModal.status" :options="infoModal.statuses" required />

              <div class="mt-2">Summary</div>
              <b-form-input type="text" placeholder="Summary" v-model="infoModal.summary" required />

              <div class="mt-2">Description</div>
              <b-form-textarea type="text" placeholder="Description" v-model="infoModal.description" rows="3" max-rows="6" />

              <div class="mt-2">Assignee</div>
              <b-form-select placeholder="Assignee" v-model="infoModal.assignee" :options="infoModal.users" />

              <div class="mt-2">Reporter</div>
              <b-form-select disabled placeholder="Assignee" v-model="infoModal.reporter" :options="infoModal.users" />

              <div class="mt-2">Fix Version</div>
              <b-form-select placeholder="Fix Version" v-model="infoModal.fixVersion" :options="infoModal.releases" />

              <div class="mt-2">Affected Version</div>
              <b-form-select placeholder="Affected Version" v-model="infoModal.affectedVersion" :options="infoModal.releases" />

              <br>
              <div class="mt-2">
                <b-button v-b-modal.addRequirement size="sm">Add requirement</b-button>
              </div>
              <div class="mt-2">Requirements</div>
              <b-table
                  show-empty
                  small
                  stacked="md"
                  :items="infoModal.requirements.items"
                  :fields="infoModal.requirements.fields"
                  :fixed=true
                  :sort-by.sync="sortBy"
                  :sort-desc.sync="sortDesc"
                  :sort-direction="sortDirection"
              >
                <template #cell(actions)="row">
                  <b-button size="sm" @click="deleteRequirement(row.item.id)" class="mr-1">
                    Delete
                  </b-button>
                </template>
              </b-table>
              <br>

              <b-button type="submit" variant="primary" v-on:click="edit()">Ok</b-button>
              <b-button type="reset" variant="danger">Reset</b-button>
            </b-form>
          </div>
        </div>
      </b-modal>

      <b-modal id="addRequirement" :title="infoModal.requirements.newRequirementTitle" v-on:ok="addRequirement()" @hide="resetRequirementModal()" ok-only>
        <b-form>
          <div class="mt-2">Summary</div>
          <b-form-input type="number" placeholder="Requirement Id" v-model="infoModal.requirements.newRequirementId" required />
        </b-form>
      </b-modal>

      <b-modal id="deleteObject" :title='"Are you sure?"' v-on:ok="deleteTask()" @hide="resetDeleteModal()">
        <div class="mt-2">Are you sure?</div>
      </b-modal>
    </b-container>

  </div>
</template>

<script>
import {AXIOS} from '@/components/http-commons'

export default {
  name: "Tasks",
  data() {
    return {
      items: [],
      fields: [
        { key: 'key', label: 'Task Key', sortable: true, sortDirection: 'asc'},
        { key: 'summary', label: 'Summary', sortable: true },
        { key: 'status', label: 'Status', sortable: true },
        { key: 'assignee', label: 'Assignee', sortable: true },
        { key: 'actions', label: 'Actions' }
      ],
      totalRows: 1,
      currentPage: 1,
      perPage: 10,
      pageOptions: [10, 20, 50, { value: 100, text: "Show a lot" }],
      sortBy: 'name',
      sortDesc: false,
      sortDirection: 'asc',
      filter: null,
      filterOn: [],
      currentUser:{
        user: null,
        manager: "",
        admin: false,
        items:[]
      },
      deleteModal:{
        taskId: null
      },
      infoModal: {
        id: 'info-modal',
        title: '',
        content: '',
        task: null,
        taskId:null,
        status:null,
        statuses:[],
        users:[],
        releases:[],
        project:'',
        projects:[],
        assignee:'',
        reporter:'',
        fixVersion:null,
        affectedVersion: null,
        requirements:{
          items:[],
          fields: [
            { key: 'reqId', label: 'Requirement', sortable: true, sortDirection: 'asc'},
            { key: 'actions', label: 'Actions' }
          ],
          newRequirementId: null,
          newRequirementTitle: "Add Requirement"
        }
      }
    }
  },
  computed: {
    sortOptions() {
      // Create an options list from our fields
      return this.fields
          .filter(f => f.sortable)
          .map(f => {
            return { text: f.label, value: f.key }
          })
    }
  },
  mounted() {
    // Set the initial number of items
    this.loadUserContent();
  },
  methods: {
    loadUserContent() {
      AXIOS.get('/user/currentUser')
          .then(response => {
            console.log(response.data);
            this.currentUser.user = response.data;
            this.currentUser.admin = response.data.admin;
          })
          .catch(error => {
            console.log('ERROR: ' + error.response);
          })
      AXIOS.get('/user/currentUserRoles')
          .then(response => {
            console.log(response.data);
            this.currentUser.items = response.data;
            response.data.forEach( object =>{
              if(object.role.name === "Manager"){
                if (this.currentUser.manager === ""){
                  this.currentUser.manager = object.project.key
                }
                else{
                  this.currentUser.manager += ","+object.project.key
                }
              }
            })
          })
          .catch(error => {
            console.log('ERROR: ' + error.response);
          })
      console.log(this.currentUser)
      AXIOS.get('/task')
          .then(response => {
            console.log(response.data);
            this.$data.items = response.data;
            this.totalRows = response.data.length;
            this.$data.items.forEach(object=>{
              if (this.currentUser.manager.match(/.*/+object.project.key+/.*/) || this.currentUser.admin === true){
                object.role = "Manager"
              }
              else{
                object.role = "User"
              }
            })
          })
          .catch(error => {
            console.log('ERROR: ' + error.response);
          })
    },
    checkRoles(pkey){
      return !!(this.currentUser.manager.match(/.*/ + pkey + /.*/) || this.currentUser.admin === true);
    },
    addRequirement(){
      console.log("addRequirement")
      if(this.infoModal.requirements.newRequirementId !== null || this.infoModal.requirements.newRequirementId !== "") {
        let body = {
          task: this.infoModal.task.id,
          reqId: this.infoModal.requirements.newRequirementId
        }
        console.log(body)
        AXIOS.post('/task/' + this.infoModal.taskId + '/requirements', body)
            .then(response => {
              console.log(response)
            })
            .catch(error => {
              console.log('ERROR: ' + error.response.message);
            })
      }
      this.resetRequirementModal()
    },
    resetRequirementModal(){
      this.infoModal.requirements.newRequirementId = null
      this.$bvModal.hide('addRequirement')
      this.loadTaskRequirements(this.infoModal.task.id)
    },
    setDeleteModal(taskId){
      this.deleteModal.taskId = taskId
      this.$bvModal.show('deleteObject')
    },
    resetDeleteModal(){
      this.deleteModal.taskId = null
      this.$bvModal.hide('deleteObject')
    },
    loadFieldsContent(projectKey){
      AXIOS.get('/project/'+projectKey+"/users")
          .then(response => {
            console.log(response.data);
            response.data.forEach(object => {
              const item = {
                value: object.user.username,
                text: object.user.name+" ("+object.user.username+")"
              };
              this.infoModal.users.push(item)
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
      AXIOS.get('/status')
          .then(response => {
            console.log(response.data);
            response.data.forEach(object => {
              const item = {
                value: object.id,
                text: object.name
              };
              this.infoModal.statuses.push(item)
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
                text: object.name
              };
              this.infoModal.releases.push(item)
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
    deleteRequirement(reqId){
      AXIOS.delete('/task/'+this.infoModal.taskId+'/requirements/'+reqId)
          .then(response => {
            console.log("Requirements: "+response.data)
            this.infoModal.requirements.items = response.data
          })
          .catch(error => {
            console.log('ERROR: ' + error.response.data);
          })
    },
    deleteTask(){
      if (this.deleteModal.taskId != null) {
        AXIOS.delete('/task/' + this.deleteModal.taskId)
            .then(response => {
              console.log("task: " + response.data)
              this.infoModal.requirements.items = response.data
            })
            .catch(error => {
              console.log('ERROR: ' + error.response.message);
            })

        setTimeout(this.loadUserContent, 1000)
      }
    },
    info(item) {
      this.loadFieldsContent(item.project.key)
      AXIOS.get('/project/all')
          .then(response => {
            console.log(response.data)
            response.data.forEach(object =>{
              const item = {
                text: object.name + " (" + object.key + ")",
                value: object.key
              }
              this.infoModal.projects.push(item)
            })
          })
          .catch(error => {
            console.log('ERROR: ' + error.response);
          })
      this.loadTaskRequirements(item.id)
      this.infoModal.task = item
      this.infoModal.title = '('+item.key+') '+item.summary
      this.infoModal.taskId = item.id
      this.infoModal.project = item.project.key
      this.infoModal.summary = item.summary
      this.infoModal.status = item.status.id
      if (item.description) {
        this.infoModal.description = item.description
      }
      if (item.assignee) {
        this.infoModal.assignee = item.assignee.username
      }
      this.infoModal.reporter = item.reporter.username
      if (item.fixVersion) {
        this.infoModal.affectedVersion = item.affectedVersion.id
      }
      if(item.affectedVersion) {
        this.infoModal.fixVersion = item.fixVersion.id
      }
    },
    loadTaskRequirements(taskId){
      AXIOS.get('/task/'+taskId+'/requirements')
          .then(response => {
            console.log("Requirements: "+response.data)
            this.infoModal.requirements.items = response.data
          })
          .catch(error => {
            console.log('ERROR: ' + error.response.data);
          })
    },
    newAssignedRole(user, role, project) {
      const body = {
        user: user,
        role: role,
        project: project,
      };
      console.log(body)
      AXIOS.post('/project/'+project+'/users', body)
          .then(response => {
            console.log(response)
          })
          .catch(error => {
            console.log('ERROR: ' + error.response.data);
          })
      this.resetUserModal()
    },
    edit() {
      if (this.infoModal.summary === '' || this.infoModal.summary == null){
        console.log("response")
      }
      else if(this.infoModal.status === '' || this.infoModal.status == null){
        console.log("response")
      }
      else {
        const body = {
          summary: this.infoModal.summary,
          status: this.infoModal.status,
          description: this.infoModal.description,
          assignee: this.infoModal.assignee,
          fixVersion: this.infoModal.fixVersion,
          affectedVersion: this.infoModal.affectedVersion
        };
        AXIOS.put('/task/' + this.infoModal.taskId, body)
            .then(response => {
              console.log(response)
              this.loadUserContent();
            })
            .catch(error => {
              console.log('ERROR: ' + error.response);
            })
        this.resetInfoModal()
      }
    },
    resetInfoModal() {
      this.infoModal.title = ''
      this.infoModal.content = ''
      this.infoModal.project = ''
      this.infoModal.summary = ''
      this.infoModal.description = ''
      this.infoModal.assignee = ''
      this.infoModal.fixVersion = null
      this.infoModal.affectedVersion = null
      this.infoModal.reporter = ''
      this.infoModal.users = []
      this.infoModal.projects = []
      this.infoModal.releases = []
      this.infoModal.taskId = null
      this.infoModal.status = null
      this.infoModal.statuses = []
      this.infoModal.taskId = null
      this.$bvModal.hide('editModal')
    },
    onFiltered(filteredItems) {
      // Trigger pagination to update the number of buttons/pages due to filtering
      this.totalRows = filteredItems.length
      this.currentPage = 1
    }
  }
}
</script>

<style scoped>

</style>