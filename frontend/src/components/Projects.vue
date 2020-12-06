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
          <b-button size="sm" @click="archive(row.item, true)" class="mr-1" v-if="row.item.archived == false">
            Archive
          </b-button>
          <b-button size="sm" @click="archive(row.item, false)" class="mr-1" v-if="row.item.archived == true">
            Recover
          </b-button>
          <b-button size="sm" @click="info(row.item, row.index, $event.target)" class="mr-1">
            Details
          </b-button>
        </template>

      </b-table>

      <!-- Info modal -->
      <b-modal :id="infoModal.id" :title="infoModal.title" ok-only @hide="resetInfoModal">
        <pre>{{ infoModal.content }}</pre>
      </b-modal>

      <b-row>
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
        { key: 'key', label: 'Project Key', sortable: true, sortDirection: 'desc' },
        { key: 'name', label: 'Project Name', sortable: true, class: 'text-center' },
        { key: 'archived', label: 'Archived', formatter: (value) => {
            return value ? 'Yes' : 'No'
          },
          sortable: true,
          sortByFormatted: true,
          filterByFormatted: true
        },
        { key: 'actions', label: 'Actions' }
      ],
      totalRows: 1,
      currentPage: 1,
      perPage: 5,
      pageOptions: [5, 10, 15, { value: 100, text: "Show a lot" }],
      sortBy: '',
      sortDesc: false,
      sortDirection: 'asc',
      filter: null,
      filterOn: [],
      infoModal: {
        id: 'info-modal',
        title: '',
        content: ''
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
    this.totalRows = this.items.length
  },
  methods: {
    loadUserContent() {
      const header = {'Authorization': 'Bearer ' + this.$store.getters.getToken};
      AXIOS.get('/project/all', { headers: header })
          .then(response => {
            this.$data.items = response.data;
          })
          .catch(error => {
            console.log('ERROR: ' + error.response.data);
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
      console.log(body)
      AXIOS.post('/project/'+item.key, body, { headers: header})
          .then(response => {
            console.log(response.data)
            this.loadUserContent();
          })
          .catch(error => {
            console.log('ERROR: ' + error.response);
          })
    },
    info(item, index, button) {
      this.infoModal.title = `Row index: ${index}`
      this.infoModal.content = JSON.stringify(item, null, 2)
      this.$root.$emit('bv::show::modal', this.infoModal.id, button)
    },
    resetInfoModal() {
      this.infoModal.title = ''
      this.infoModal.content = ''
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