import type { Config } from 'tailwindcss'
// Default are on https://tailwindcss.nuxtjs.org/tailwind/config#default-configuration
export default <Partial<Config>>{
    darkMode: 'class',
    theme: {
        extend: {
            colors: {

                primary: 'var(--color-primary)',
                secondary: 'rgb(var(--color-secondary) / <alpha-value>)',

                contrastText: 'var(--contrast-text)',

                prime_color_1: 'rgb(var(--color-primary)',//"rgba(203,71,54,0.8)",

                mainColor_1_1_dark: "#020202", //Gradient - Start
                mainColor_1_2_dark: "#020202", //Gradient - End

                mainColor_1_1_light: "#d8d8d8", //Gradient - Start
                mainColor_1_2_light: "#d8d8d8", //Gradient - End

                tile_color_light:"rgba(255, 255, 255, 0.25)",//tile_color_light:"rgba(170, 170, 170, 0.15)",
                tile_color_dark: "rgba(255, 255, 255, 0.04)",

                b_color_light:"rgb(216, 216, 216)",
                b_color_dark:"rgba(2,2,2)",

                mainColor_2_1: "#EFF0F2", //Gradient - Start
                mainColor_2_2: "#E6E7E9", //Gradient - End

                textColor_dark: "#b4b4b4", //Für hellen Hintergrund
                textColor_light: "#3b3c3e",  //Für dunklen Hintergrund

                'primary-1': 'rgb(var(--color-primary-1) / <alpha-value>)',
                'primary-2': 'rgb(var(--color-primary-2) / <alpha-value>)',

                'secondary-1': 'rgb(var(--color-secondary-1) / <alpha-value>)',
                'secondary-2': 'rgb(var(--color-secondary-2) / <alpha-value>)',

                'text-dark': 'rgb(var(--color-text-dark) / <alpha-value>)',
                'text-light': 'rgb(var(--color-text-light) / <alpha-value>)',


                api_color_1 : 'var(--api-color-1)',
                api_color_2 : 'var(--api-color-2)',
                api_color_3 : 'var(--api-color-3)',
                api_color_4 : 'var(--api-color-4)',
                api_color_5 : 'var(--api-color-5)',
                api_color_6 : 'var(--api-color-6)',
                api_color_7 : 'var(--api-color-7)',
                api_color_8 : 'var(--api-color-8)',
                api_color_9 : 'var(--api-color-9)',
                api_color_10 : 'var(--api-color-10)',
            }
        }

    },
    plugins: [],
    content: [
        "./index.html",
        "./src/**/*.{vue,js,ts,jsx,tsx}",
        "./node_modules/primevue/**/*.{vue,js,ts,jsx,tsx}",
        "./assets/presets/**/*.{vue,js,ts,jsx,tsx}"
    ],
}
