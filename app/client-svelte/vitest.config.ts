import { defineConfig } from "vitest/config";

export default defineConfig({
  test: {
    include: ["src/main/svelte/**/*.{test,spec}.{js,ts}"],
    environment: "node",
    globals: true,
    reporters: ["default", "junit"],
    outputFile: {
      junit: "./build/test-results/test/TEST-vitest.xml",
    },
    setupFiles: ["./vitest.setup.js"],
    typecheck: {
      enabled: true,
    },
    coverage: {
      provider: "v8",
      reporter: ["text", "html"],
      include: [
        "src/main/svelte/router/*.ts",
        "src/main/svelte/services/*.ts",
        "src/main/svelte/stores/*.ts",
      ],
      reportsDirectory: "./build/coverage",
    },
  },
});
