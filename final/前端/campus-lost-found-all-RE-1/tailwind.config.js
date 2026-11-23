// tailwind.config.js
module.exports = {
  content: [
    "./index.html",
    "./src/**/*.{vue,js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        // 核心配色方案（紫色偏蓝，现代可信赖）
        primary: "#5B5DE6",
        "primary-dim": "#4B4ED4",
        "primary-container": "#E6E6FF",
        "on-primary": "#ffffff",
        "on-primary-container": "#2B2D8A",

        secondary: "#4A7DFF",
        "secondary-container": "#DBE8FF",
        "on-secondary-container": "#0B2A6F",
        "secondary-fixed": "#e0e1f9",

        tertiary: "#22C7A9",
        "tertiary-container": "#CFFAF2",
        "on-tertiary-container": "#083344",
        "on-tertiary-fixed-variant": "#0F766E",
        "tertiary-fixed": "#CFFAF2",

        error: "#a8364b",
        "error-container": "#f97386",
        "on-error": "#fff7f7",

        surface: "#fbf8fe",
        "surface-container": "#efedf6",
        "surface-container-low": "#f5f2fb",
        "surface-container-lowest": "#ffffff", // 补充定义
        "surface-container-high": "#e9e7f1",
        "surface-container-highest": "#e3e1ed",
        "surface-variant": "#e3e1ed",
        "surface-tint": "#5B5DE6",
        "surface-bright": "#fbf8fe",

        "on-surface": "#31323b",
        "on-surface-variant": "#5e5e68",
        outline: "#7a7a84",
        "outline-variant": "#b2b1bc",
        success: "#16a34a",
        warning: "#f59e0b",
      },
      fontFamily: {
        headline: ["'Plus Jakarta Sans'", "sans-serif"],
        body: ["'Inter'", "sans-serif"],
      },
      borderRadius: {
        'xl': '1rem',
        '2xl': '1.5rem',
        '3xl': '2rem',
      }
    },
  },
  plugins: [],
}