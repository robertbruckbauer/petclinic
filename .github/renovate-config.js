module.exports = {
  extends: [
    "mergeConfidence:all-badges",
    "config:best-practices",
    "group:allNonMajor",
    "group:monorepos",
    "group:springBoot",
    ":preserveSemverRanges",
    ":disableDependencyDashboard",
    ":disableVulnerabilityAlerts",
  ],
  labels: ["renovate"],
  // https://docs.renovatebot.com/self-hosted-configuration/#onboarding
  onboarding: true,
  // https://docs.renovatebot.com/self-hosted-configuration/#onboardingbranch
  onboardingBranch: "renovate-onboarding",
  // https://docs.renovatebot.com/self-hosted-configuration/#onboardingprtitle
  onboardingPrTitle: "Renovate onboarding",
  // https://docs.renovatebot.com/configuration-options/#branchprefix
  branchPrefix: "renovate-",
  // https://docs.renovatebot.com/configuration-options/#commitmessageprefix
  commitMessagePrefix: "renovate:",
  // https://docs.renovatebot.com/configuration-options/#semanticcommits
  semanticCommits: "disabled",
  // https://docs.renovatebot.com/configuration-options/#prConcurrentLimit
  prConcurrentLimit: 0,
  // https://docs.renovatebot.com/configuration-options/#prhourlylimit
  prHourlyLimit: 0,
  // https://docs.renovatebot.com/configuration-options/#rebasewhen
  rebaseWhen: "behind-base-branch",
  // https://docs.renovatebot.com/modules/manager/
  enabledManagers: ["gradle", "dockerfile", "docker-compose", "npm"],
  packageRules: [
    // https://docs.renovatebot.com/modules/manager/npm/
    {
      matchManagers: ["npm"],
      matchFileNames: ["app/**/package.json"],
    },
    // https://docs.renovatebot.com/modules/manager/gradle/
    {
      matchManagers: ["gradle"],
      matchFileNames: ["settings.gradle", "buildSrc/build.gradle"],
    },
    // https://docs.renovatebot.com/modules/manager/dockerfile/
    {
      matchManagers: ["dockerfile"],
      matchFileNames: ["app/**/Dockerfile"],
      matchUpdateTypes: ["major"],
      pinDigests: true,
    },
    // https://docs.renovatebot.com/modules/manager/docker-compose/
    {
      matchManagers: ["docker-compose"],
      matchFileNames: ["app/**/compose*.yml"],
      matchUpdateTypes: ["major"],
      pinDigests: true,
    },
    // https://docs.renovatebot.com/modules/datasource/docker/
    {
      matchDatasources: ["docker"],
      matchUpdateTypes: ["minor", "patch"],
      enabled: false,
    },
  ],
};
