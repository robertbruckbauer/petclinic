// https://v2.tailwindcss.com/docs/just-in-time-mode
// https://v2.tailwindcss.com/docs/customizing-colors
const colors = require('tailwindcss/colors')
module.exports = {
  mode: "jit",
  purge: [
    './src/**/*.{html,svelte,js,ts}'
  ],
  theme: {
    extend: {
      colors: {
        transparent: 'transparent',
        primary: colors.indigo,
        error: colors.red
      },
      width: {
        "1/7": "14.2857143%",
        "2/7": "28.5714286%",
        "3/7": "42.8571429%",
        "4/7": "57.1428571%",
        "5/7": "71.4285714%",
        "6/7": "85.7142857%"
      }
    },
    fontSize: {
      base: "1rem",
      "5xl": "6rem",
      "4xl": "3.75rem",
      "3xl": "3rem",
      "2xl": "2.125rem",
      "xl": "1.5rem",
      "lg": "1.25rem",
      "sm": "0.875rem",
      "xs": "0.75rem"
    },
    breakpoints: {
      "xl": { max: "1279px" },
      "lg": { max: "1023px" },
      "md": { max: "767px" },
      "sm": { max: "639px" }
    }
  },
  plugins: [
    require('@tailwindcss/forms')],
}
