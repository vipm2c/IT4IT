<template>
  <div div="newproject">
    <div class="login-form">
      <b-card
          title="New Project"
          tag="article"
          style="max-width: 20rem;"
          class="mb-2"
      >
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
            You have been successfully create project!
            <hr />
            <router-link to="/projects">
              <b-button variant="primary">View projects</b-button>
            </router-link>
          </b-alert>
        </div>
        <div>
          <b-form-input type="text" placeholder="Project Name" v-model="name" />
          <div class="mt-2"></div>

          <b-form-input type="text" placeholder="Project Key" v-model="key" />
          <div class="mt-2"></div>

          <b-form-input type="text" placeholder="Description" v-model="description" />
          <div class="mt-2"></div>

          <b-form-input type="text" placeholder="Spec" v-model="spec" />
          <div class="mt-2"></div>

        </div>

        <b-button v-on:click="create" variant="primary">Create</b-button>

      </b-card>
    </div>
  </div>
</template>

<script>
import {AXIOS} from '@/components/http-commons'

export default {
  name: 'NewProject',
  data () {
    return {
      name: '',
      key: '',
      description: '',
      spec: '',
      successfullyCreated: false,
      dismissSecs: 0
    }
  },
  methods: {
    create: function () {
      if (this.$data.name === '' || this.$data.name == null) {
        this.$data.alertMessage = 'Please, fill "Project Name" field';
        this.showAlert();
      } else if (this.$data.key === '' || this.$data.key == null) {
        this.$data.alertMessage = 'Please, fill "Project Key" field';
        this.showAlert();
      } else {
        var newProject = {
          'name': this.$data.name,
          'key': this.$data.key,
          'description': this.$data.description,
          'spec': this.$data.credential,
          'archived': false
        };
        AXIOS.post('/project', newProject)
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
    countDownChanged(dismissCountDown) {
      this.dismissCountDown = dismissCountDown
    },
    showAlert() {
      this.dismissCountDown = this.dismissSecs
    },
    successAlert() {
      this.name = '';
      this.key = '';
      this.description = '';
      this.spec = '';
      this.successfullyCreated = true;
    }
  }
}
</script>

<style>
.login-form {
  margin-left: 38%;
  margin-top: 50px;
}
</style>