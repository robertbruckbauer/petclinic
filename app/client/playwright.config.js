import { devices } from "@playwright/test";

const config = {
  /* See https://playwright.dev/docs/api/class-testconfig#test-config-test-dir */
  testDir: "./src/test",
  /* See https://playwright.dev/docs/api/class-testconfig#test-config-test-match */
  testMatch: /.+\.e2e\.js/,
  /* See https://playwright.dev/docs/api/class-testconfig#test-config-test-ignore */
  testIgnore: "**/*Page.js",
  /* Maximum time one test can run for. */
  timeout: 30 * 1000,
  /* See https://playwright.dev/docs/api/class-testconfig#test-config-expect */
  expect: {
    /* Maximum time expect() should wait for the condition to be met. */
    timeout: 5000,
  },
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
    ["html", { outputFolder: "build/test-reports/html" }],
    ["junit", { outputFile: "build/test-results/test/TEST-playwright.xml" }],
  ],
  /* See https://playwright.dev/docs/api/class-testoptions. */
  use: {
    /* See https://playwright.dev/docs/api/class-testoptions#test-options-action-timeout */
    actionTimeout: 0,
    /* See https://playwright.dev/docs/api/class-testoptions#test-options-base-url */
    baseURL: process.env.CI
      ? process.env.CI_ENVIRONMENT_URL
      : "http://localhost:5000",
    /* See https://playwright.dev/docs/api/class-testoptions#test-options-trace */
    trace: "on",
    /* See https://playwright.dev/docs/api/class-testoptions#test-options-screenshot */
    screenshot: "on",
  },
  /* See https://playwright.dev/docs/api/class-testconfig#test-config-projects */
  projects: [
    {
      name: "chromium",
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
      name: "firefox",
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
      name: "webkit",
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
};

export default config;
