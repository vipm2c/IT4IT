<template>
  <div div="editusers">
    <h2>Users</h2>

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
              <b-form-checkbox value="name">Name</b-form-checkbox>
              <b-form-checkbox value="username">Username</b-form-checkbox>
              <b-form-checkbox value="email">Email</b-form-checkbox>
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
        <template #cell(name)="row">
          {{ row.value }}
        </template>

        <template #cell(actions)="row">
          <b-button v-b-modal.infoModal size="sm" @click="info(row.item)" class="mr-1">
            Info
          </b-button>
          <b-button size="sm" @click="archive(row.item, false)" class="mr-1" v-if="row.item.active === true">
            Disable
          </b-button>
          <b-button size="sm" @click="archive(row.item, true)" class="mr-1" v-if="row.item.active === false">
            Enable
          </b-button>
          <b-button v-b-modal.editModal size="sm" @click="info(row.item)" class="mr-1">
            Edit
          </b-button>
        </template>

      </b-table>

      <b-modal id="infoModal" :title="infoModal.title" ok-only v-on:ok="resetInfoModal"  @hide="resetInfoModal">
        <div>
          <div>
            <b-form-input type="text" disabled placeholder="Name" v-model="infoModal.row.name" />
            <div class="mt-2"></div>
            <b-form-input type="text" disabled placeholder="Username" v-model="infoModal.row.username" />
            <div class="mt-2"></div>
            <b-form-input type="text" disabled placeholder="Email" v-model="infoModal.row.email" />
            <div class="mt-2"></div>
            <b-form-checkbox id="checkbox-1" disabled v-model="infoModal.row.admin" name="checkbox-1" >Admin</b-form-checkbox>
            <div class="mt-2"></div>
            <b-form-checkbox id="checkbox-1" disabled v-model="infoModal.row.active" name="checkbox-1" >Active</b-form-checkbox>
            <div class="mt-2"></div>
          </div>
          <b-table
              show-empty
              small
              stacked="md"
              :items="infoModal.roles"
              :fields="infoModal.fields"
              :fixed=true
              :sort-by.sync="sortBy"
              :sort-desc.sync="sortDesc"
              :sort-direction="sortDirection"
          >
          </b-table>
        </div>
      </b-modal>

      <b-modal id="editModal" :title="infoModal.title" v-on:ok="edit(infoModal.row)" v-on:cancel="resetInfoModal">
        <div>
          <div>
            <b-form-input required type="text" placeholder="Name" v-model="infoModal.row.name" />
            <div class="mt-2"></div>
            <b-form-input required type="text" placeholder="Username" v-model="infoModal.row.username" />
            <div class="mt-2"></div>
            <b-form-input type="text" placeholder="Email" v-model="infoModal.row.email" />
            <div class="mt-2"></div>
            <b-form-checkbox id="checkbox-1" v-model="infoModal.row.admin" name="checkbox-1" >Admin</b-form-checkbox>
            <div class="mt-2"></div>
            <b-form-checkbox id="checkbox-1" v-model="infoModal.row.active" name="checkbox-1" >Active</b-form-checkbox>
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
  name: "EditUsers",
  data() {
    return {
      items: [],
      fields: [
        { key: 'name', label: 'Name', sortable: true, sortDirection: 'asc'},
        { key: 'username', label: 'Username', sortable: true },
        { key: 'email', label: 'Email', sortable: true },
        { key: 'active', label: 'Active', formatter: (value) => {return value ? 'Yes' : 'No'}},
        { key: 'admin', label: 'Admin', formatter: (value) => {return value ? 'Yes' : 'No'}},

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
        fields: [
          { key: 'project', label: 'Project', sortable: true, sortDirection: 'asc'},
          { key: 'role', label: 'Role', sortable: true }
        ],
        row:{
          name: '',
          username: '',
          email: '',
          active: false,
          admin: false,
        },
        roles:[]
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
      AXIOS.get('/user/all')
          .then(response => {
            this.$data.items = response.data;
            this.totalRows = response.data.length;
          })
          .catch(error => {
            console.log('ERROR: ' + error.response);
          })
    },
    archive(item,action) {
      item.archived = action
      const body = {
        name: item.name,
        username: item.username,
        active: action
      };
      console.log()
      AXIOS.put('/user/'+item.username, body)
          .then(response => {
            console.log(response)
            this.loadUserContent();
          })
          .catch(error => {
            console.log('ERROR: ' + error.response);
          })
    },
    info(item) {
      this.infoModal.title = item.name
      //this.$root.$emit('bv::show::modal', this.infoModal.id, button)
      this.infoModal.row.name = item.name
      this.infoModal.row.username = item.username
      this.infoModal.row.email = item.email
      this.infoModal.row.admin = item.admin
      this.infoModal.row.active = item.active
    },
    edit(item) {
      const body = {
        name: item.name,
        username: item.username,
        email: item.email,
        admin: item.admin,
        active: item.active
      };
      console.log(body)
      AXIOS.put('/user/'+item.username, body)
          .then(response => {
            console.log(response)
            this.loadUserContent();
          })
          .catch(error => {
            console.log('ERROR: ' + error.response);
          })
      this.resetInfoModal()
    },
    resetInfoModal() {
      this.infoModal.title = ''
      this.infoModal.content = ''
      this.infoModal.row.name = ''
      this.infoModal.row.username = ''
      this.infoModal.row.email = ''
      this.infoModal.row.active = false
      this.infoModal.row.admin = false
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