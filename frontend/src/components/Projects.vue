<template>
  <div div="projects">
    <h2>Projects</h2>

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

        <template #cell(actions)="row">
          <b-button v-b-modal.infoModal size="sm" @click="info(row.item)" class="mr-1">
            Info
          </b-button>
          <b-button size="sm" @click="archive(row.item, true)" class="mr-1" v-if="row.item.archived === false && row.item.role.match(/.*(admin|manager).*/)">
            Archive
          </b-button>
          <b-button size="sm" @click="archive(row.item, false)" class="mr-1" v-if="row.item.archived === true && row.item.role.match(/.*(admin|manager).*/)">
            Recover
          </b-button>
          <b-button v-b-modal.editModal size="sm" @click="info(row.item)" class="mr-1" v-if="row.item.role.match(/.*(admin|manager).*/)">
            Edit
          </b-button>
          <b-button v-b-modal.userModal size="sm" @click="user(row.item)" class="mr-1" v-if="row.item.role.match(/.*(admin|manager).*/)">
            Add user
          </b-button>
          <b-button v-b-modal.userModal size="sm" @click="user(row.item)" class="mr-1" v-if="row.item.role.match(/.*(admin).*/)" variant="danger">
            Delete
          </b-button>
        </template>

      </b-table>

      <b-modal id="infoModal" :title="infoModal.title" ok-only v-on:ok="resetInfoModal"  @hide="resetInfoModal">
        <div>
          <b-form>
            <div>
              <b-form-input type="text" disabled placeholder="Project Name" v-model="infoModal.row.name" />
              <div class="mt-2"></div>
              <b-form-input type="text" disabled placeholder="Project Key" v-model="infoModal.row.key" />
              <div class="mt-2"></div>
              <b-form-input type="text" disabled placeholder="Description" v-model="infoModal.row.description" />
              <div class="mt-2"></div>
              <b-form-input type="text" disabled placeholder="Spec" v-model="infoModal.row.spec" />
              <div class="mt-2"></div>
              <b-form-checkbox id="checkbox-1" disabled v-model="infoModal.row.archived" name="checkbox-1" >Archived</b-form-checkbox>
              <div class="mt-2"></div>
            </div>
            <div class="mt-2">Users</div>
            <b-table
                show-empty
                small
                stacked="md"
                :items="infoModal.users"
                :fields="infoModal.fields"
                :fixed=true
                :sort-by.sync="sortBy"
                :sort-desc.sync="sortDesc"
                :sort-direction="sortDirection"
            >
            </b-table>
            <div class="mt-2">Releases</div>
            <b-table
                show-empty
                small
                stacked="md"
                :items="infoModal.release.items"
                :fields="infoModal.release.fields"
                :fixed=true
                :sort-by.sync="sortBy"
                :sort-desc.sync="sortDesc"
                :sort-direction="sortDirection"
            >
            </b-table>
          </b-form>
        </div>
      </b-modal>

      <b-modal id="editModal" :title="infoModal.title" v-on:ok="edit(infoModal.row)" v-on:cancel="resetInfoModal" @hide="resetInfoModal">
          <div>
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
                <b-form-input type="text" required placeholder="Project Name" v-model="infoModal.row.name" />
                <div class="mt-2"></div>
                <b-form-input type="text" placeholder="Description" v-model="infoModal.row.description" />
                <div class="mt-2"></div>
                <b-form-input type="text" placeholder="Spec" v-model="infoModal.row.spec" />
                <div class="mt-2"></div>
              </div>

              <b-table
                  show-empty
                  small
                  stacked="md"
                  :items="infoModal.users"
                  :fields="infoModal.fields"
                  :fixed=true
                  :sort-by.sync="sortBy"
                  :sort-desc.sync="sortDesc"
                  :sort-direction="sortDirection"
              >
                <template #cell(actions)="row">
                  <b-button size="sm" @click="deleteUser(row.item.id)" class="mr-1" variant="danger">
                    Delete
                  </b-button>
                </template>
              </b-table>
              <br>
              <div class="mt-2">
                <b-button v-b-modal.newReleaseModal v-on:click="fillNewReleaseModal()" size="sm">New Release</b-button>
              </div>
              <div class="mt-2">Releases</div>
              <b-table
                  show-empty
                  small
                  stacked="md"
                  :items="infoModal.release.items"
                  :fields="infoModal.release.fields"
                  :fixed=true
                  :sort-by.sync="sortBy"
                  :sort-desc.sync="sortDesc"
                  :sort-direction="sortDirection"
              >
                <template #cell(actions)="row">
                  <b-button v-b-modal.editReleaseModal size="sm" @click="fillEditReleaseModal(row.item)" class="mr-1">
                    Edit
                  </b-button>
                </template>
              </b-table>
            </b-form>
        </div>
      </b-modal>

      <b-modal id="userModal" :title="userModal.title" v-on:ok="newAssignedRole(userModal.user, userModal.role, userModal.project)" v-on:cancel="resetUserModal" @hide="resetUserModal">
        <div>
          <div>
            <b-form-select placeholder="User" v-model="userModal.user" :options="userModal.users" />
            <div class="mt-2"></div>
            <b-form-select placeholder="Role" v-model="userModal.role" :options="userModal.roles" />
            <div class="mt-2"></div>
          </div>
        </div>
      </b-modal>

      <b-modal id="editReleaseModal" :title="releaseModal.title" @hide="resetReleaseModal" hide-footer>
        <div>
          <div>
            <b-form @submit="edit()" @reset="resetInfoModal()">
              <div class="mt-2">Version</div>
              <b-form-input type="text" placeholder="Version" v-model="releaseModal.version" required />

              <div class="mt-2">Description</div>
              <b-form-input type="text" placeholder="Description" v-model="releaseModal.description" />

              <div class="mt-2">Released</div>
              <b-form-checkbox id="checkbox-1" v-model="releaseModal.released" name="checkbox-1" >Released</b-form-checkbox>

              <div class="mt-2">Specification</div>
              <b-form-input type="text" placeholder="Specification" v-model="releaseModal.spec" />

              <b-button variant="primary" v-on:click="editRelease()">Ok</b-button>
              <b-button variant="danger" v-on:click="resetReleaseModal">Reset</b-button>
            </b-form>
          </div>
        </div>
      </b-modal>

      <b-modal id="newReleaseModal" :title="releaseModal.title" @hide="resetReleaseModal" hide-footer>
        <div>
          <div>
            <b-form @submit="edit()" @reset="resetInfoModal()">
              <div class="mt-2">Version</div>
              <b-form-input type="text" placeholder="Version" v-model="releaseModal.version" required />

              <div class="mt-2">Description</div>
              <b-form-input type="text" placeholder="Description" v-model="releaseModal.description" />

              <div class="mt-2">Released</div>
              <b-form-checkbox id="checkbox-1" v-model="releaseModal.released" name="checkbox-1" >Released</b-form-checkbox>

              <div class="mt-2">Specification</div>
              <b-form-input type="text" placeholder="Specification" v-model="releaseModal.spec" />

              <b-button variant="primary" v-on:click="newRelease()">Ok</b-button>
            </b-form>
          </div>
        </div>
      </b-modal>

      <b-modal id="deleteObject" :title='"Are you sure?"' v-on:ok="deleteProject()" @hide="resetDeleteModal()">
        <div class="mt-2">This is permanent action</div>
      </b-modal>

    </b-container>

  </div>
</template>

<script>
import {AXIOS} from '@/components/http-commons'

export default {
  name: "Projects",
  data() {
    return {
      items: [],
      fields: [
        { key: 'name', label: 'Project Name', sortable: true, sortDirection: 'asc'},
        { key: 'key', label: 'Project Key', sortable: true },
        { key: 'archived', label: 'Archived', formatter: (value) => {return value ? 'Yes' : 'No'}},
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
      deleteModal:{
        pid:null
      },
      releaseModal:{
        title:"Release",
        item:null,
        version:"",
        description:"",
        released:false,
        spec:""
      },
      infoModal: {
        id: 'info-modal',
        title: '',
        content: '',
        item:'',
        release:{
          items:[],
          fields:[
            { key: 'version', label: 'Release version', sortable: true, sortDirection: 'asc'},
            { key: 'released', label: 'Released', sortable: true },
            { key: 'actions', label: 'Actions' }
          ]
        },
        fields: [
          { key: 'user', label: 'User', sortable: true, sortDirection: 'asc'},
          { key: 'role', label: 'Role', sortable: true },
          { key: 'actions', label: 'Actions' }
        ],
        users:[],
        row:{
          name:'',
          key:'',
          description:'',
          spec:'',
          archived:false
        }
      },
      userModal: {
        id: 'info-modal',
        title: '',
        content: '',
        user: '',
        role: '',
        project: '',
        users: [],
        roles: []
      },
      dismissSecs: 5,
      dismissCountDown: 0,
      alertMessage: '',
      successfullyCreated: false
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
      AXIOS.get('/project/all')
          .then(response => {
            console.log(response.data)
            this.items = response.data;
            this.totalRows = response.data.length;
          })
          .catch(error => {
            console.log('ERROR: ' + error.response);
          })
    },
    deleteProject(){
      if (this.deleteModal.pid != null) {
        AXIOS.delete('/project/' + this.deleteModal.pid)
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
    deleteUser(assignedRoleId){
      AXIOS.delete('/assignedRole/' + assignedRoleId)
          .then(response => {
            console.log(response.data)
          })
          .catch(error => {
            console.log('ERROR: ' + error.response.message);
          })
      setTimeout(this.reloadUserList, 2000)
    },
    deleteRelease(releaseId){
      AXIOS.delete('/release/' + releaseId)
          .then(response => {
            console.log(response.data)
          })
          .catch(error => {
            console.log('ERROR: ' + error.response.message);
          })

      setTimeout(this.getReleases, 2000)
    },
    setDeleteModal(taskId){
      this.deleteModal.taskId = taskId
      this.$bvModal.show('deleteObject')
    },
    resetDeleteModal(){
      this.deleteModal.pid = null
    },
    resetReleaseModal(){
      this.releaseModal.spec = ""
      this.releaseModal.version = ""
      this.releaseModal.spec = ""
      this.releaseModal.item = null
      this.releaseModal.description = ""
      this.releaseModal.title = ""
      this.$bvModal.hide('newReleaseModal')
      this.$bvModal.hide('editReleaseModal')
    },
    fillNewReleaseModal(){
      this.releaseModal.title = "New Release"
    },
    fillEditReleaseModal(item){
      this.releaseModal.title = "Edit release "+item.version
      this.releaseModal.item = item
      this.releaseModal.version = item.version
      this.releaseModal.description = item.description
      this.releaseModal.spec = item.spec
      this.releaseModal.released = item.released
    },
    newRelease() {
      const body = {
        version: this.releaseModal.version,
        description: this.releaseModal.description,
        project: this.infoModal.item.id,
        released: this.releaseModal.released,
        spec: this.releaseModal.spec
      };
      console.log(body)
      AXIOS.post('/project/'+this.infoModal.item.key+'/release', body)
          .then(response => {
            console.log(response)
          })
          .catch(error => {
            console.log('ERROR: ' + error.response.data);
          })
      this.resetReleaseModal()
      setTimeout(this.getReleases, 1000)
    },
    editRelease(){
      if (this.releaseModal.version === '' || this.releaseModal.version == null) {
        this.$data.alertMessage = 'Please, fill "Project Name" field';
        this.showAlert()
        this.infoModal.row.archived = true
      } else {
        const body = {
          version: this.releaseModal.version,
          description: this.releaseModal.description,
          released: this.releaseModal.released,
          spec: this.releaseModal.spec
        };
        AXIOS.put('/project/' + this.infoModal.item.key+'/release/'+this.releaseModal.item.id, body)
            .then(response => {
              console.log(response)
              this.loadUserContent();
            })
            .catch(error => {
              console.log('ERROR: ' + error.response);
            })
        this.resetReleaseModal()
        setTimeout(this.getReleases, 1000)
      }
    },
    archive(item,action) {
      const header = {'Authorization': 'Bearer ' + this.$store.getters.getToken};
      item.archived = action
      const body = {
        name: item.name,
        description: item.description,
        key: item.key,
        spec: item.spec,
        archived: action
      };
      AXIOS.put('/project/'+item.key, body, { headers: header})
          .then(response => {
            console.log(response)
            this.loadUserContent();
          })
          .catch(error => {
            console.log('ERROR: ' + error.response);
          })
    },
    info(item) {
      this.infoModal.users = []
      this.infoModal.item = item
      AXIOS.get('/project/' + item.key + '/users')
          .then(response => {
            console.log(response.data)
            response.data.forEach(userObject =>{
              const user = {
                user: userObject.user.name + " (" + userObject.user.username + ")",
                role: userObject.role.name,
                id: userObject.id
              }
              this.infoModal.users.push(user)
            })
          })
          .catch(error => {
            console.log('ERROR: ' + error.response);
          })
      this.infoModal.title = item.name
      //this.$root.$emit('bv::show::modal', this.infoModal.id, button)
      this.infoModal.row.name = item.name
      this.infoModal.row.key = item.key
      this.infoModal.row.description = item.description
      this.infoModal.row.spec = item.spec
      this.infoModal.row.archived = item.archived
      this.getReleases()
    },
    getReleases(){
      AXIOS.get('/project/' + this.infoModal.row.key + '/release/all')
          .then(response => {
            console.log(response.data)
            this.infoModal.release.items = response.data
          })
          .catch(error => {
            console.log('ERROR: ' + error.response);
          })
    },
    reloadUserList(){
      this.infoModal.users = []
      AXIOS.get('/project/' + this.infoModal.item.key + '/users')
          .then(response => {
            console.log(response.data)
            response.data.forEach(userObject =>{
              const user = {
                user: userObject.user.name + " (" + userObject.user.username + ")",
                role: userObject.role.name,
                id: userObject.id
              }
              this.infoModal.users.push(user)
            })
          })
          .catch(error => {
            console.log('ERROR: ' + error.response);
          })
    },
    user(item) {
      this.userModal.users = []
      this.userModal.roles = []
      AXIOS.get('/user/all')
          .then(response => {
            console.log(response.data)
            response.data.forEach(userObject =>{
              const user = {
                value: userObject.username,
                text: userObject.name
              };
              console.log(user)
              this.userModal.users.push(user)
            })
            console.log(this.userModal.users)
          })
          .catch(error => {
            console.log('ERROR: ' + error.response);
          })
      AXIOS.get('/role/all')
          .then(response => {
            console.log(response.data)
            response.data.forEach(roleObject => {
              const role = {
                value: roleObject.id,
                text: roleObject.name
              };
              console.log(role)
              this.userModal.roles.push(role)
            })
            console.log(this.userModal.roles)
          })
          .catch(error => {
            console.log('ERROR: ' + error.response);
          })
      this.userModal.title = "Добавление пользователей к проекту "+item.key
      this.userModal.project = item.key
      //this.$root.$emit('bv::show::modal', this.userModal.id, button)
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
    edit(item) {
      if (this.$data.infoModal.row.name === '' || this.$data.infoModal.row.name == null) {
        this.$data.alertMessage = 'Please, fill "Project Name" field';
        this.showAlert()
        this.infoModal.row.archived = true
      } else {
        const body = {
          name: item.name,
          description: item.description,
          key: item.key,
          spec: item.spec,
          archived: item.archived
        };
        AXIOS.put('/project/' + item.key, body)
            .then(response => {
              console.log(response)
              this.loadUserContent();
            })
            .catch(error => {
              console.log('ERROR: ' + error.response);
            })
        console.log("Редактируем проект " + item.key)
        this.resetInfoModal()
      }
    },
    resetInfoModal() {
      this.infoModal.title = ''
      this.infoModal.content = ''
      this.infoModal.row.name = ''
      this.infoModal.row.key = ''
      this.infoModal.row.description = ''
      this.infoModal.row.spec = ''
      this.infoModal.users = []
      this.infoModal.row.archived = false
    },
    resetUserModal() {
      this.userModal.title = ''
      this.userModal.users = []
      this.userModal.roles = []
      this.userModal.user = ''
      this.userModal.role = ''
      this.userModal.project = ''
    },
    onFiltered(filteredItems) {
      // Trigger pagination to update the number of buttons/pages due to filtering
      this.totalRows = filteredItems.length
      this.currentPage = 1
    },
    countDownChanged(dismissCountDown) {
      this.dismissCountDown = dismissCountDown
    },
    showAlert() {
      this.dismissCountDown = this.dismissSecs
    }
  }
}
</script>

<style scoped>

</style>