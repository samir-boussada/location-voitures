// @ts-check
const { defineConfig } = require('@playwright/test');

module.exports = defineConfig({
  testDir: './tests',
  timeout: 30000,
  retries: 0,
  use: {
    baseURL: 'http://localhost:8080/location-voitures/',
    headless: false,
    launchOptions: {
      args: ['--no-proxy-server'],
      slowMo: 300,
      env: {
        ...process.env,
        NO_PROXY: 'localhost,127.0.0.1',
        no_proxy: 'localhost,127.0.0.1',
      },
    },
  },
  projects: [
    { name: 'chromium', use: { browserName: 'chromium' } },
  ],
});