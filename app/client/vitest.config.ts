import { defineConfig } from 'vitest/config'

export default defineConfig({
  test: {
    include: ['src/main/svelte/**/*.{test,spec}.{js,ts}'],
    environment: 'node',
    reporters: ['default', 'junit'],
    outputFile: {
      junit: './build/test-results/test/TEST-vitest.xml'
    }
  },
})
