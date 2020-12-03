<template>
  <div div="signup">
    <div class="login-form">
      <b-card
          title="Register"
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
          <b-alert variant="success" :show="successfullyRegistered">
            You have been successfully registered! Now you can login with your credentials
            <hr />
            <router-link to="/login">
              <b-button variant="primary">Login</b-button>
            </router-link>
          </b-alert>
        </div>
        <div>
          <b-form-input type="text" placeholder="Username" v-model="username" />
          <div class="mt-2"></div>

          <b-form-input type="text" placeholder="Name" v-model="name" />
          <div class="mt-2"></div>

          <b-form-input type="text" placeholder="Email" v-model="email" />
          <div class="mt-2"></div>

          <b-form-input type="password" placeholder="Password" v-model="credential" />
          <div class="mt-2"></div>

          <b-form-input type="password" placeholder="Confirm Password" v-model="confirmcredential" />
          <div class="mt-2"></div>
        </div>

        <b-button v-on:click="register" variant="primary">Register</b-button>

      </b-card>
    </div>
  </div>
</template>

<script>
import {AXIOS} from '@/components/http-commons'

export default {
  name: 'SignUp',
  data () {
    return {
      username: '',
      name: '',
      email: '',
      credential: '',
      confirmcredential: '',
      dismissSecs: 5,
      dismissCountDown: 0,
      alertMessage: '',
      successfullyRegistered: false
    }
  },
  methods: {
    register: function () {
      if (this.$data.username === '' || this.$data.username == null) {
        this.$data.alertMessage = 'Please, fill "Username" field';
        this.showAlert();
      } else if (this.$data.name === '' || this.$data.name == null) {
        this.$data.alertMessage = 'Please, fill "Name" field';
        this.showAlert();
      } else if (this.$data.email === '' || this.$data.email == null) {
        this.$data.alertMessage = 'Please, fill "Email" field';
        this.showAlert();
      } else if (!this.$data.email.includes('@')) {
        this.$data.alertMessage = 'Email is incorrect';
        this.showAlert();
      } else if (this.$data.credential === '' || this.$data.credential == null) {
        this.$data.alertMessage = 'Please, fill "Password" field';
        this.showAlert();
      } else if (this.$data.confirmcredential === '' || this.$data.confirmcredential == null) {
        this.$data.alertMessage = 'Please, confirm password';
        this.showAlert();
      } else if (this.$data.confirmcredential !== this.$data.credential) {
        this.$data.alertMessage = 'Passwords are not match';
        this.showAlert();
      } else {
        var newUser = {
          'username': this.$data.username,
          'name': this.$data.name,
          'email': this.$data.email,
          'credential': this.$data.credential
        };
        AXIOS.post('/auth/signup', newUser)
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
      this.username = '';
      this.name = '';
      this.email = '';
      this.credential = '';
      this.confirmcredential = '';
      this.successfullyRegistered = true;
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