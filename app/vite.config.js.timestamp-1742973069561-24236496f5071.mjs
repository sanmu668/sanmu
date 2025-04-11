// vite.config.js
import { defineConfig } from "file:///E:/DevelopmentProject/AI%20%E6%99%BA%E8%83%BD%E5%AE%A2%E6%9C%8D%20SaaS%E7%B3%BB%E7%BB%9F/app/node_modules/vite/dist/node/index.js";
import vue from "file:///E:/DevelopmentProject/AI%20%E6%99%BA%E8%83%BD%E5%AE%A2%E6%9C%8D%20SaaS%E7%B3%BB%E7%BB%9F/app/node_modules/@vitejs/plugin-vue/dist/index.mjs";
import { resolve } from "path";
var __vite_injected_original_dirname = "E:\\DevelopmentProject\\AI \u667A\u80FD\u5BA2\u670D SaaS\u7CFB\u7EDF\\app";
var vite_config_default = defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      "@": resolve(__vite_injected_original_dirname, "src"),
      "components": resolve(__vite_injected_original_dirname, "src/components"),
      "views": resolve(__vite_injected_original_dirname, "src/views"),
      "utils": resolve(__vite_injected_original_dirname, "src/utils"),
      "api": resolve(__vite_injected_original_dirname, "src/api"),
      "assets": resolve(__vite_injected_original_dirname, "src/assets"),
      "store": resolve(__vite_injected_original_dirname, "src/store")
    }
  },
  server: {
    port: 3e3,
    open: true,
    proxy: {
      "/api": {
        target: "http://localhost:8080",
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api/, ""),
        configure: (proxy, options) => {
          proxy.on("proxyReq", (proxyReq, req, res) => {
            proxyReq.setHeader("Access-Control-Allow-Origin", "*");
            proxyReq.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            proxyReq.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
          });
        }
      }
    }
  }
});
export {
  vite_config_default as default
};
//# sourceMappingURL=data:application/json;base64,ewogICJ2ZXJzaW9uIjogMywKICAic291cmNlcyI6IFsidml0ZS5jb25maWcuanMiXSwKICAic291cmNlc0NvbnRlbnQiOiBbImNvbnN0IF9fdml0ZV9pbmplY3RlZF9vcmlnaW5hbF9kaXJuYW1lID0gXCJFOlxcXFxEZXZlbG9wbWVudFByb2plY3RcXFxcQUkgXHU2NjdBXHU4MEZEXHU1QkEyXHU2NzBEIFNhYVNcdTdDRkJcdTdFREZcXFxcYXBwXCI7Y29uc3QgX192aXRlX2luamVjdGVkX29yaWdpbmFsX2ZpbGVuYW1lID0gXCJFOlxcXFxEZXZlbG9wbWVudFByb2plY3RcXFxcQUkgXHU2NjdBXHU4MEZEXHU1QkEyXHU2NzBEIFNhYVNcdTdDRkJcdTdFREZcXFxcYXBwXFxcXHZpdGUuY29uZmlnLmpzXCI7Y29uc3QgX192aXRlX2luamVjdGVkX29yaWdpbmFsX2ltcG9ydF9tZXRhX3VybCA9IFwiZmlsZTovLy9FOi9EZXZlbG9wbWVudFByb2plY3QvQUklMjAlRTYlOTklQkElRTglODMlQkQlRTUlQUUlQTIlRTYlOUMlOEQlMjBTYWFTJUU3JUIzJUJCJUU3JUJCJTlGL2FwcC92aXRlLmNvbmZpZy5qc1wiO2ltcG9ydCB7IGRlZmluZUNvbmZpZyB9IGZyb20gJ3ZpdGUnXG5pbXBvcnQgdnVlIGZyb20gJ0B2aXRlanMvcGx1Z2luLXZ1ZSdcbmltcG9ydCB7IHJlc29sdmUgfSBmcm9tICdwYXRoJ1xuXG5leHBvcnQgZGVmYXVsdCBkZWZpbmVDb25maWcoe1xuICBwbHVnaW5zOiBbdnVlKCldLFxuICByZXNvbHZlOiB7XG4gICAgYWxpYXM6IHtcbiAgICAgICdAJzogcmVzb2x2ZShfX2Rpcm5hbWUsICdzcmMnKSxcbiAgICAgICdjb21wb25lbnRzJzogcmVzb2x2ZShfX2Rpcm5hbWUsICdzcmMvY29tcG9uZW50cycpLFxuICAgICAgJ3ZpZXdzJzogcmVzb2x2ZShfX2Rpcm5hbWUsICdzcmMvdmlld3MnKSxcbiAgICAgICd1dGlscyc6IHJlc29sdmUoX19kaXJuYW1lLCAnc3JjL3V0aWxzJyksXG4gICAgICAnYXBpJzogcmVzb2x2ZShfX2Rpcm5hbWUsICdzcmMvYXBpJyksXG4gICAgICAnYXNzZXRzJzogcmVzb2x2ZShfX2Rpcm5hbWUsICdzcmMvYXNzZXRzJyksXG4gICAgICAnc3RvcmUnOiByZXNvbHZlKF9fZGlybmFtZSwgJ3NyYy9zdG9yZScpXG4gICAgfVxuICB9LFxuICBzZXJ2ZXI6IHtcbiAgICBwb3J0OiAzMDAwLFxuICAgIG9wZW46IHRydWUsXG4gICAgcHJveHk6IHtcbiAgICAgICcvYXBpJzoge1xuICAgICAgICB0YXJnZXQ6ICdodHRwOi8vbG9jYWxob3N0OjgwODAnLFxuICAgICAgICBjaGFuZ2VPcmlnaW46IHRydWUsXG4gICAgICAgIHJld3JpdGU6IChwYXRoKSA9PiBwYXRoLnJlcGxhY2UoL15cXC9hcGkvLCAnJyksXG4gICAgICAgIGNvbmZpZ3VyZTogKHByb3h5LCBvcHRpb25zKSA9PiB7XG4gICAgICAgICAgcHJveHkub24oJ3Byb3h5UmVxJywgKHByb3h5UmVxLCByZXEsIHJlcykgPT4ge1xuICAgICAgICAgICAgcHJveHlSZXEuc2V0SGVhZGVyKCdBY2Nlc3MtQ29udHJvbC1BbGxvdy1PcmlnaW4nLCAnKicpXG4gICAgICAgICAgICBwcm94eVJlcS5zZXRIZWFkZXIoJ0FjY2Vzcy1Db250cm9sLUFsbG93LU1ldGhvZHMnLCAnR0VULCBQT1NULCBQVVQsIERFTEVURSwgT1BUSU9OUycpXG4gICAgICAgICAgICBwcm94eVJlcS5zZXRIZWFkZXIoJ0FjY2Vzcy1Db250cm9sLUFsbG93LUhlYWRlcnMnLCAnQ29udGVudC1UeXBlLCBBdXRob3JpemF0aW9uJylcbiAgICAgICAgICB9KVxuICAgICAgICB9XG4gICAgICB9XG4gICAgfVxuICB9XG59KSJdLAogICJtYXBwaW5ncyI6ICI7QUFBc1csU0FBUyxvQkFBb0I7QUFDblksT0FBTyxTQUFTO0FBQ2hCLFNBQVMsZUFBZTtBQUZ4QixJQUFNLG1DQUFtQztBQUl6QyxJQUFPLHNCQUFRLGFBQWE7QUFBQSxFQUMxQixTQUFTLENBQUMsSUFBSSxDQUFDO0FBQUEsRUFDZixTQUFTO0FBQUEsSUFDUCxPQUFPO0FBQUEsTUFDTCxLQUFLLFFBQVEsa0NBQVcsS0FBSztBQUFBLE1BQzdCLGNBQWMsUUFBUSxrQ0FBVyxnQkFBZ0I7QUFBQSxNQUNqRCxTQUFTLFFBQVEsa0NBQVcsV0FBVztBQUFBLE1BQ3ZDLFNBQVMsUUFBUSxrQ0FBVyxXQUFXO0FBQUEsTUFDdkMsT0FBTyxRQUFRLGtDQUFXLFNBQVM7QUFBQSxNQUNuQyxVQUFVLFFBQVEsa0NBQVcsWUFBWTtBQUFBLE1BQ3pDLFNBQVMsUUFBUSxrQ0FBVyxXQUFXO0FBQUEsSUFDekM7QUFBQSxFQUNGO0FBQUEsRUFDQSxRQUFRO0FBQUEsSUFDTixNQUFNO0FBQUEsSUFDTixNQUFNO0FBQUEsSUFDTixPQUFPO0FBQUEsTUFDTCxRQUFRO0FBQUEsUUFDTixRQUFRO0FBQUEsUUFDUixjQUFjO0FBQUEsUUFDZCxTQUFTLENBQUMsU0FBUyxLQUFLLFFBQVEsVUFBVSxFQUFFO0FBQUEsUUFDNUMsV0FBVyxDQUFDLE9BQU8sWUFBWTtBQUM3QixnQkFBTSxHQUFHLFlBQVksQ0FBQyxVQUFVLEtBQUssUUFBUTtBQUMzQyxxQkFBUyxVQUFVLCtCQUErQixHQUFHO0FBQ3JELHFCQUFTLFVBQVUsZ0NBQWdDLGlDQUFpQztBQUNwRixxQkFBUyxVQUFVLGdDQUFnQyw2QkFBNkI7QUFBQSxVQUNsRixDQUFDO0FBQUEsUUFDSDtBQUFBLE1BQ0Y7QUFBQSxJQUNGO0FBQUEsRUFDRjtBQUNGLENBQUM7IiwKICAibmFtZXMiOiBbXQp9Cg==
