import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { resolve } from 'path'

export default defineConfig({
  base: '/',
  plugins: [vue()],
  resolve: {
    alias: {
      '@': resolve(__dirname, 'src'),
      'components': resolve(__dirname, 'src/components'),
      'views': resolve(__dirname, 'src/views'),
      'utils': resolve(__dirname, 'src/utils'),
      'api': resolve(__dirname, 'src/api'),
      'assets': resolve(__dirname, 'src/assets'),
      'store': resolve(__dirname, 'src/store')
    }
  },
  server: {
    port: 3000,
    open: true,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        headers: {
          'Connection': 'keep-alive'
        },
        changeOrigin: true,
        secure: false,
        ws: true,
        rewrite: (path) => path.replace(/^\/api/, ''),
        configure: (proxy) => {
          proxy.on('proxyReq', (proxyReq) => {
            proxyReq.setHeader('Connection', 'keep-alive');
            proxyReq.setHeader('Cache-Control', 'no-cache');
          });
          proxy.on('proxyRes', (proxyRes) => {
            proxyRes.headers['X-Accel-Buffering'] = 'no';
          });
        }
      }
    }
  }
})