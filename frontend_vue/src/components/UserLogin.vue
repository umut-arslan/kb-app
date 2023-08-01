<template>
  <a v-if='!isLoggedIn' class='navbar-nav nav-item nav-link'>
    <a class='nav-link' href='/authorization/google'>Login</a>
  </a>
  <a v-if='isLoggedIn' class='navbar-nav nav-item nav-link'>
    <a class='nav-link' href='/logout'>Logout</a>
  </a>
  <ul v-if='isLoggedIn' class='navbar-nav ml-auto flex-row gap' style='gap: 50px'>
    <li class='nav-item'>
      <a class='navbar-brand'>
        {{ name }}
        <img :src=avatarUrl width='50' height='50' alt='' style='margin-left: 10px'>
      </a>
    </li>
  </ul>
</template>

<script lang='ts'>
import { defineComponent } from 'vue'
import UserService from '@/service/UserService'
import { useToast } from 'vue-toast-notification'

export default defineComponent({
  components: {},
  data(): {
    name: string,
    email: string,
    avatarUrl: string,
    isLoggedIn: boolean
  } {
    return {
      name: '',
      email: '',
      avatarUrl: '',
      isLoggedIn: false
    }
  },
  methods: {
    getUserInfo: function () {
      UserService.getUserDetails()
        .then((response => {
          this.name = response.name;
          this.email = response.email;
          this.avatarUrl = response.avatarUrl;
          this.isLoggedIn = true;
          const $toast = useToast()
          $toast.open({
            message: 'Successfully logged in!',
            type: 'success',
          });
        }))
        .catch((error) => {
          console.log(error);
          this.isLoggedIn = false;
        })
    },
  },
  mounted() {
    this.getUserInfo();
  }
})
</script>

<style scoped>

img {
  border-radius: 50%;
}

.navbar-brand{
  font-size: 1rem;
}

</style>
