import {fileURLToPath, URL} from 'node:url'

import {defineConfig} from 'vite'
import Vue from '@vitejs/plugin-vue'
import Markdown from 'vite-plugin-vue-markdown'
import {VitePWA} from "vite-plugin-pwa";

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [
    VitePWA({
      registerType: 'autoUpdate',
      devOptions: {
        enabled: false
      },
      injectRegister: "inline",
      useCredentials: true,
      includeAssets: ['favicon.ico', 'large.png', 'small.png', 'robots.txt'],
      manifest: {
        name: 'kb-app',
        short_name: 'kbapp',
        description: 'klose brothers GmbH internal App',
        theme_color: '#AA0A37',
        start_url: "/",
        icons: [
          {
            src: '/small.png',
            sizes: '192x192',
            type: 'image/png'
          },
          {
            src: '/large.png',
            sizes: '512x512',
            type: 'image/png'
          }
        ]
      }
    }),
    Vue({
      include: [/\.vue$/, /\.md$/]
    }),
    Markdown()
  ],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    }
  },
  server: {
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        secure: false
      }, '/logout': {
        target: 'http://localhost:8080',
        hostRewrite: 'http://localhost:8080',
        changeOrigin: true,
        secure: false
      }, '/login': {
        target: 'http://localhost:8080',
        hostRewrite: 'http://localhost:8080',
        changeOrigin: true,
        secure: false
      }, '/authorization/google': {
        target: 'http://localhost:8080',
        hostRewrite: 'http://localhost:8080',
        changeOrigin: true,
        secure: false
      }
    }
  }
})
