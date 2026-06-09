import { defineConfig, devices } from "@playwright/test";

const env =
  (
    globalThis as {
      process?: { env?: Record<string, string | undefined> };
    }
  ).process?.env ?? {};

export default defineConfig({
  /* See https://playwright.dev/docs/api/class-testconfig#test-config-test-dir */
  testDir: "./src/test/playwright",
  /* See https://playwright.dev/docs/api/class-testconfig#test-config-test-match */
  testMatch: /.+\.e2e\.js/,
  /* See https://playwright.dev/docs/api/class-testconfig#test-config-test-ignore */
  testIgnore: "**/*Page.js",
  /* See https://playwright.dev/docs/api/class-testconfig#test-config-timeout */
  timeout: env.PWDEBUG ? 0 : 30000,
  /* See https://playwright.dev/docs/api/class-testconfig#test-config-expect */
  expect: {
    /* Maximum time expect() should wait for the condition to be met. */
    timeout: 5000,
  },
  /* Fail the build on CI if you accidentally left test.only in the source code. */
  forbidOnly: !!env.CI,
  /* Retry on CI only */
  retries: env.CI ? 2 : 0,
  /* Opt out of parallel tests on CI. */
  workers: env.CI ? 1 : undefined,
  /* See https://playwright.dev/docs/api/class-testconfig#test-config-output-dir */
  outputDir: "build/test-results/",
  /* See https://playwright.dev/docs/test-reporters */
  reporter: [
    ["list"],
    ["junit", { outputFile: "build/test-results/test/TEST-playwright.xml" }],
  ],
  /* See https://playwright.dev/docs/api/class-testoptions. */
  use: {
    /* See https://playwright.dev/docs/api/class-testoptions#test-options-action-timeout */
    actionTimeout: 0,
    /* See https://playwright.dev/docs/api/class-testoptions#test-options-base-url */
    baseURL: "http://localhost:5052",
    /* See https://playwright.dev/docs/api/class-testoptions#test-options-trace */
    trace: "retain-on-failure",
    /* See https://playwright.dev/docs/api/class-testoptions#test-options-video */
    video: "retain-on-failure",
    /* See https://playwright.dev/docs/api/class-testoptions#test-options-screenshot */
    screenshot: "on",
  },
  /* See https://playwright.dev/docs/api/class-testconfig#test-config-projects */
  projects: [
    {
      name: "client-angular-chromium",
      use: {
        ...devices["Desktop Chrome"],
        viewport: {
          width: 1280,
          height: 1080,
        },
      },
    },
    /*
    {
      name: "client-angular-firefox",
      use: {
        ...devices["Desktop Firefox"],
        viewport: {
          width: 1280,
          height: 1080,
        },
      },
    },
    */
    /*
    {
      name: "client-angular-safari",
      use: {
        ...devices["Desktop Safari"],
        viewport: {
          width: 1280,
          height: 1080,
        },
      },
    },
    */
  ],
});
