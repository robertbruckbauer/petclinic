import { defineConfig, devices } from '@playwright/test';

export default defineConfig({
  /* https://playwright.dev/docs/api/class-testconfig#test-config-global-setup */
  globalSetup: './src/test/playwright/global-setup.ts',
  /* https://playwright.dev/docs/api/class-testconfig#test-config-global-teardown */
  globalTeardown: './src/test/playwright/global-teardown.ts',
  /* See https://playwright.dev/docs/api/class-testconfig#test-config-test-dir */
  testDir: './src/test/playwright',
  /* Fail the build on CI if you accidentally left test.only in the source code. */
  forbidOnly: !!process.env.CI,
  /* Retry on CI only */
  retries: process.env.CI ? 2 : 0,
  /* Opt out of parallel tests on CI. */
  workers: process.env.CI ? 1 : undefined,
  /* See https://playwright.dev/docs/api/class-testconfig#test-config-output-dir */
  outputDir: "build/test-results/",
  /* See https://playwright.dev/docs/test-reporters */
  reporter: [
    ["list"],
    ["junit", { outputFile: "build/test-results/test/TEST-playwright.xml" }],
  ],
  /* See https://playwright.dev/docs/api/class-testconfig#test-config-projects */
  projects: [
    {
      name: 'tester',
      testMatch: '**/*.test.ts',
      use: { ...devices['Desktop Chrome'] },
    },
    {
      name: 'assert',
      testMatch: '**/*.spec.ts',
      use: { ...devices['Desktop Chrome'] },
    },
  ],
});
