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
        </template>

      </b-table>

      <b-modal id="infoModal" :title="infoModal.title" ok-only v-on:ok="resetInfoModal"  @hide="resetInfoModal">
        <div>
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
        </div>
      </b-modal>

      <b-modal id="editModal" :title="infoModal.title" v-on:ok="edit(infoModal.row)" v-on:cancel="resetInfoModal" @hide="resetInfoModal">
          <div>
              <div>
                <b-form-input type="text" placeholder="Project Name" v-model="infoModal.row.name" />
                <div class="mt-2"></div>
                <b-form-input type="text" placeholder="Description" v-model="infoModal.row.description" />
                <div class="mt-2"></div>
                <b-form-input type="text" placeholder="Spec" v-model="infoModal.row.spec" />
                <div class="mt-2"></div>
          </div>
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
      infoModal: {
        id: 'info-modal',
        title: '',
        content: '',
        fields: [
          { key: 'user', label: 'User', sortable: true, sortDirection: 'asc'},
          { key: 'role', label: 'Role', sortable: true }
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
      AXIOS.get('/project/all')
          .then(response => {
            this.$data.items = response.data;
            this.totalRows = response.data.length;
          })
          .catch(error => {
            console.log('ERROR: ' + error.response);
          })
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
      AXIOS.get('/project/' + item.key + '/users')
          .then(response => {
            console.log(response.data)
            response.data.forEach(userObject =>{
              const user = {
                user: userObject.user.name + " (" + userObject.user.username + ")",
                role: userObject.role.name
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
      const body = {
        name: item.name,
        description: item.description,
        key: item.key,
        spec: item.spec,
        archived: item.archived
      };
      AXIOS.put('/project/'+item.key, body)
          .then(response => {
            console.log(response)
            this.loadUserContent();
          })
          .catch(error => {
            console.log('ERROR: ' + error.response);
          })
      console.log("Редактируем проект "+item.key)
      this.resetInfoModal()
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
    }
  }
}
</script>

<style scoped>

</style>