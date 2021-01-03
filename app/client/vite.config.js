import { defineConfig } from 'vite'
import { svelte } from '@sveltejs/vite-plugin-svelte';
import preprocess from 'svelte-preprocess';
import path from "path";

export default defineConfig({
  plugins: [
    svelte({
      preprocess: preprocess({
        postcss: true
      })
    })
  ],
  rollupDedupe: ['svelte'],
  build: {
    outDir: path.join(__dirname, "build"),
    emptyOutDir: true
  },
  cacheDir: path.join(__dirname, ".vite"),
  publicDir: path.join(__dirname, "public"),
  root: path.join(__dirname, "src/main/svelte"),
  server: {
    port: 5000
  }
});
