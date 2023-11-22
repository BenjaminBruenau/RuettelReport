import type { Config } from 'tailwindcss'
// Default are on https://tailwindcss.nuxtjs.org/tailwind/config#default-configuration
export default <Partial<Config>>{
    darkMode: 'class',
    theme: {
        extend: {
            colors: {
                mainColor_1_1: "#4B5471", //Gradient - Start
                mainColor_1_2: "#394253", //Gradient - End

                mainColor_2_1: "#EFF0F2", //Gradient - Start
                mainColor_2_2: "#E6E7E9", //Gradient - End

                textColor_dark: "#3B3C3E", //Für hellen Hintergrund
                textColor_light: "#EFF0F2",  //Für dunklen Hintergrund

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
