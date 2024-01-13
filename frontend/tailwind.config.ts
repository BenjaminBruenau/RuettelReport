import type { Config } from 'tailwindcss'
// Default are on https://tailwindcss.nuxtjs.org/tailwind/config#default-configuration
export default <Partial<Config>>{
    darkMode: 'class',
    theme: {
        extend: {
            colors: {

                prime_color_1: "#cc4736",

                mainColor_1_1_dark: "#020202", //Gradient - Start
                mainColor_1_2_dark: "#020202", //Gradient - End

                mainColor_1_1_light: "#d8d8d8", //Gradient - Start
                mainColor_1_2_light: "#d8d8d8", //Gradient - End

                tile_color_light:"rgba(170, 170, 170, 0.15)",
                tile_color_dark: "rgba(255, 255, 255, 0.04)",

                b_color_light:"rgb(216, 216, 216)",
                b_color_dark:"rgba(2,2,2)",

                mainColor_2_1: "#EFF0F2", //Gradient - Start
                mainColor_2_2: "#E6E7E9", //Gradient - End

                textColor_dark: "#b4b4b4", //Für hellen Hintergrund
                textColor_light: "#3b3c3e",  //Für dunklen Hintergrund

                //Akzentfarben muss ich noch schauen
                //#FF9F1C
                //#C86FC9
                //#FF7E6B
                //#9BC969


                primary: 'rgb(var(--color-primary) / <alpha-value>)',
                secondary: 'rgb(var(--color-secondary) / <alpha-value>)',


                'primary-1': 'rgb(var(--color-primary-1) / <alpha-value>)',
                'primary-2': 'rgb(var(--color-primary-2) / <alpha-value>)',

                'secondary-1': 'rgb(var(--color-secondary-1) / <alpha-value>)',
                'secondary-2': 'rgb(var(--color-secondary-2) / <alpha-value>)',

                'text-dark': 'rgb(var(--color-text-dark) / <alpha-value>)',
                'text-light': 'rgb(var(--color-text-light) / <alpha-value>)'
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
