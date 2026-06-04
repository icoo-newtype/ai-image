import { defineConfig, loadEnv } from 'vite';
import vue from '@vitejs/plugin-vue';
import { join } from 'path';
import { componentsMapBuilder } from './vite-plugin/componentsMap';

// https://vite.dev/config/
export default defineConfig(({ mode }) => {
  const env = loadEnv(mode, process.cwd());
  return {
    mode: process.env.MODE,
    root: __dirname,
    publicDir: 'public',
    envDir: process.cwd(),
    resolve: {
      alias: {
        '@': join(__dirname, './src'),
        '~@': join(__dirname, './src')
      },
    },
    plugins: [
      vue({
        template: {
          transformAssetUrls: false
        },
      }),
      componentsMapBuilder('/src/views/components/modules')
    ],
    server: {
      port: 8091,
    }
  };
});