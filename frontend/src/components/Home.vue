<template>
  <div div="home">
    <b-jumbotron>
      <template slot="header">IT4IT</template>

      <template slot="lead">
        Это домашняя страница проекта IT4IT
      </template>

      <hr class="my-4" />

      <p v-if="!this.$store.getters.isAuthenticated">
        Необходимо залогиниться
      </p>

      <router-link to="/login" v-if="!this.$store.getters.isAuthenticated">
        <b-button variant="primary">Login</b-button>
      </router-link>

    </b-jumbotron>
  </div>
</template>

<script>
export default {
  name: "Home"
}
</script>

<style scoped>

</style>