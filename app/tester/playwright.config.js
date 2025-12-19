import { devices } from "@playwright/test";

const config = {
  testDir: "./src/test/playwright",
  testMatch: /.+\.suite\.js/,
  globalSetup: "./src/test/playwright/tester.setup.js",
  globalTeardown: "./src/test/playwright/tester.teardown.js",
  timeout: process.env.PWDEBUG ? 0 : 30000,
  expect: {
    timeout: 5000,
  },
  retries: 0,
  workers: 1,
  outputDir: "build/test-results/",
  reporter: [
    ["list"]
  ],
  use: {
    actionTimeout: 0,
    baseURL: process.env.BASE_URL || "http://localhost",
    trace: "off",
    video: "off",
    screenshot: "off",
  },
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
  ],
};

export default config;
