import { defineConfig } from "vite";
import { svelte, vitePreprocess } from "@sveltejs/vite-plugin-svelte";
import tailwindcss from "@tailwindcss/vite";
import path from "path";

export default defineConfig({
  plugins: [
    svelte({
      preprocess: vitePreprocess({ script: true }),
    }),
    tailwindcss(),
  ],
  build: {
    outDir: path.join(__dirname, "build/generated"),
    emptyOutDir: true,
  },
  cacheDir: path.join(__dirname, ".vite"),
  publicDir: path.join(__dirname, "public"),
  root: path.join(__dirname, "src/main/svelte"),
});
