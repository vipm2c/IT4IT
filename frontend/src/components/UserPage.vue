<template>
  <div div="userpage">
    <h2>{{ pageContent }}</h2>
  </div>
</template>

<script>
import {AXIOS} from '@/components/http-commons'
export default {
  name: 'UserPage',
  data() {
    return {
      pageContent: ''
    }
  },
  methods: {
    loadUserContent() {
      const header = {'Authorization': 'Bearer ' + this.$store.getters.getToken};
      AXIOS.get('/usercontent', { headers: header })
          .then(response => {
            console.log(response.data)
            this.$data.pageContent = response.data;
          })
          .catch(error => {
            console.log('ERROR: ' + error.data);
          })
    }
  },
  mounted() {
    this.loadUserContent();
  }
}
</script>

<style>
</style>