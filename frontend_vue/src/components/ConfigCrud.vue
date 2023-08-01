<template>
  <div id='app'>
    <div>
      <div class='header'>
        <h1 style='font-family: Arial,serif; font-size: 40px'>Entwicklungstemplate</h1>
      </div>
      <div>
        <h2>Search Config by Key</h2>
      </div>
      <form id='getConfigByKeyForm' @submit='getConfigByKey($event, searchKey)' class='searchConfigContainer'>
        <input id='keyInputSearch' type='text' v-model='searchKey' name='title' placeholder='Key'>
        <span style='align-self: center' id='valSearchRespond'>Value is {{ searchVal }}</span>
      </form>
      <div>
        <h2>Create a new Config</h2>
      </div>
      <form id='configCreateForm' @submit='createConfig' class='createConfig'>
        <input id='keyInputCreate' type='text' v-model='key' name='title' placeholder='Create a Key'>
        <input id='valInputCreate' type='text' v-model='value' name='title' placeholder='Create a Value'>
        <button class='rounded_kb' id='submitInputCreate' type='submit'>
          <i class='bi bi-plus' style='font-size: 3rem; color: white;'></i>
        </button>
      </form>
      <div class='configListContainer'>
        <ul style='list-style-type: none;padding: 0; margin: 0'>
          <li v-bind:key='item.id' v-for='item in configs'>
            <div class='configContainer'>
              <form id='configElementForm' @keydown.enter='updateConfig($event, item)'>
                <input id='keyInputUpdate' ref='configItem' type='text' v-model='item.key' name='title'
                       placeholder='item.key'>
                <input id='valInputUpdate' ref='configItem' type='text' v-model='item.value' name='title'
                       placeholder='item.value'>
              </form>
              <button class='square_kb' id='deleteConfigBtn' type='button' @click='deleteConfig($event, item.id
              )'>
                <i class="bi bi-x" style='font-size: 3rem; color: white;'></i>
              </button>
            </div>
          </li>
        </ul>
      </div>
    </div>
  </div>
</template>

<script lang='ts'>
import { defineComponent } from 'vue'
import ConfigService from '@/service/ConfigService'
import type { Config } from '@/types/Config'

export default defineComponent({
  components: {},
  data(): {
    configs: Config[]
    searchKey: string,
    searchVal: string,
    key: string,
    value: string
  } {
    return {
      configs: [],
      searchKey: '',
      searchVal: '',
      key: '',
      value: ''
    }
  },
  methods: {
    getConfigsByUser: function () {
      ConfigService.getConfigsByUser()
        .then((response => {
          this.configs = response;
        }))
        .catch((error) => {
          ConfigService.handleError(error);
        })
    },
    getConfigByKey: function(e: Event, key: string) {
      e.preventDefault()
      ConfigService.getConfigByKey(key)
        .then((response => {
          this.searchVal = response as string
        }))
        .catch((error) => {
          ConfigService.handleError(error)
        })
    },
    createConfig: function(e: Event) {
      e.preventDefault()
      let newConfig: Config = {
        id: 0,
        key: this.key,
        value: this.value
      }
      ConfigService.createConfig(newConfig).then(() => {
        this.key = ''
        this.value = ''
        this.getConfigsByUser()
      })
      .catch((error) => {
        ConfigService.handleError(error)
      })
    },
    updateConfig: function(e: Event, config: Config) {
      e.preventDefault();
      (this.$refs['configItem'] as any).blur
      ConfigService.updateConfig(config).then(() => {
        this.getConfigsByUser()
      })
      .catch((error) => {
        ConfigService.handleError(error)
      })
    },
    deleteConfig: function(e: Event, id: number) {
      e.preventDefault()
      ConfigService.deleteConfig(id).then(() => {
        this.getConfigsByUser()
      })
      .catch((error) => {
        ConfigService.handleError(error)
      })
    }
  },
  mounted() {
    this.getConfigsByUser()
  }
})
</script>


<style scoped>
@import '@/assets/buttons.css';

input {
  border: 3px solid var(--kb-red);
  border-radius: 8px;
  padding: 10px;
  margin-right: 8px;
}

input[type=text]:focus {
  outline: none;
  border-color: var(--kb-red-dark);
}

.header {
  margin-bottom: 20px;
}

.createConfig {
  display: flex;
  flex-direction: row;
  margin-bottom: 20px;
  margin-top: 20px;
}

.searchConfigContainer {
  display: flex;
  flex-direction: row;
  margin-bottom: 20px;
  margin-top: 20px;
}

.configContainer {
  display: flex;
  flex-direction: row;
  justify-content: space-between;
  margin-top: 20px;
}

.configListContainer{
  margin-top: 50px;
}

</style>
