import { defineConfig } from 'vitest/config'

export default defineConfig({
  test: {
    include: ['src/main/svelte/**/*.test.{js,ts}'],
    environment: 'node',
    globals: true,
    reporters: ['default', 'junit'],
    outputFile: {
      junit: './build/test-results/test/TEST-vitest.xml'
    },
    setupFiles: ['./vitest.setup.js'],
    typecheck: {
      enabled: true
    }
  },
})
