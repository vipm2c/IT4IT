<template>
  <div id="app">
    <b-navbar style="width: 100%" type="dark" variant="dark">
      <b-navbar-brand id="nav-brand" href="/">IT4IT</b-navbar-brand>
      <b-navbar-toggle target="nav-collapse"></b-navbar-toggle>
      <b-collapse id="nav-collapse" is-nav>
        <b-navbar-nav>
          <router-link to="/projects" class="nav-link text-light" v-if="this.$store.getters.isAuthenticated">Projects</router-link>
          <router-link to="/tasks" class="nav-link text-light" v-if="this.$store.getters.isAuthenticated">Tasks</router-link>
          <b-nav-item-dropdown right v-if="this.$store.getters.isAuthenticated">
            <!-- Using 'button-content' slot -->
            <template #button-content>
              <em>Create</em>
            </template>
            <b-dropdown-item to="/newproject" class="nav-link text-light" v-if="this.$store.getters.isAuthenticated && this.$store.getters.isAdmin">Project</b-dropdown-item>
            <b-dropdown-item to="/register" class="nav-link text-light" v-if="this.$store.getters.isAuthenticated && this.$store.getters.isAdmin">User</b-dropdown-item>
            <b-dropdown-item to="/newtask" class="nav-link text-light" v-if="this.$store.getters.isAuthenticated">Task</b-dropdown-item>
          </b-nav-item-dropdown>
        </b-navbar-nav>
        <!-- Right aligned nav items -->
        <b-navbar-nav class="ml-auto">
          <router-link to="/login" class="nav-link text-light" v-if="!this.$store.getters.isAuthenticated">Login</router-link>
          <b-nav-item-dropdown right v-if="this.$store.getters.isAuthenticated">
            <!-- Using 'button-content' slot -->
            <template #button-content>
              <em>User</em>
            </template>
            <b-dropdown-item href="#">Profile</b-dropdown-item>
            <b-dropdown-item href="#" v-if="this.$store.getters.isAuthenticated" v-on:click="logout">Logout</b-dropdown-item>
          </b-nav-item-dropdown>
        </b-navbar-nav>
      </b-collapse>
    </b-navbar>
    <router-view></router-view>
  </div>
</template>


<script>
export default {
  name: 'app',
  methods: {
    logout() {
      this.$store.dispatch('logout');
      this.$router.push('/')
    }
  }
}
</script>

<style>
</style>