import {TRANSITIONS as props} from "primevue/passthrough/tailwind/index.esm.js";

const customColor = '[#93c5fd]';

console.log(`bg-${customColor}`)

const PrimevueDesignPreset = {

    inputtext: {
        root: ({ props, context, parent }) => ({
            class: [
                'm-0',
                'font-sans text-gray-600 dark:text-white/80 bg-tile_color_light dark:bg-tile_color_dark border border-gray-300 dark:border-blue-900/40 transition-colors duration-200 appearance-none rounded-lg',
                {
                    'hover:border-primary-1  focus:outline-none focus:outline-offset-0 focus:shadow-[0_0_0_0.2rem_rgba(191,219,254,1)] dark:focus:shadow-[0_0_0_0.2rem_rgba(147,197,253,0.5)]': !context.disabled,
                    'opacity-60 select-none pointer-events-none cursor-default': context.disabled
                },
                {
                    'text-lg px-4 py-4': props.size == 'large',
                    'text-xs px-2 py-2': props.size == 'small',
                    'p-3 text-base': props.size == null
                }
            ]
        })
    },
    panel: {
        header: ({ props: PanelProps }) => ({
            class: [
                'flex items-center justify-between', // flex and alignments
                'border border-gray-300 bg-gray-100 text-gray-700 rounded-tl-lg rounded-tr-lg', // borders and colors
                'dark:bg-gray-900 dark:border-blue-900/40 dark:text-white/80', // Dark mode
                { 'p-5': !props.toggleable, 'py-3 px-5': props.toggleable } // condition
            ]
        }),
        title: {
            class: ['leading-none font-bold']
        },
        toggler: {
            class: [
                'inline-flex items-center justify-center overflow-hidden relative no-underline', // alignments
                'w-8 h-8 text-gray-600 border-0 bg-transparent rounded-full transition duration-200 ease-in-out', // widths, borders, and transitions
                'hover:text-gray-900 hover:border-transparent hover:bg-gray-200 dark:hover:text-white/80 dark:hover:bg-gray-800/80 dark:focus:shadow-[inset_0_0_0_0.2rem_rgba(147,197,253,0.5)]', // hover
                'focus:outline-none focus:outline-offset-0 focus:shadow-[0_0_0_0.2rem_rgba(191,219,254,1)]' // focus
            ]
        },
        togglerIcon: {
            class: ['inline-block']
        },
        content: {
            class: [
                'p-5 border border-gray-300 bg-white text-gray-700 border-t-0 last:rounded-br-lg last:rounded-bl-lg',
                'dark:bg-gray-900 dark:border-blue-900/40 dark:text-white/80' // Dark mode
            ] // padding, borders, and colors
        }
    },
    toolbar:{
        root: ({ props }) => ({
            class: [
                'bg-tile_color_light dark:bg-tile_color_dark',
                'border-b_color_light dark:border-b_color_dark',
            ]
        }),
    },
    menubar:{
        root: ({ props }) => ({
            class: [
                'bg-tile_color_light dark:bg-tile_color_dark',
                'border-b_color_light dark:border-b_color_dark',
                'text-tile_color_light dark:text-tile_color_dark',
            ]
        }),
        content: ({ props }) => ({
            class: [
                'text-tile_color_light dark:text-tile_color_dark',
            ]
        }),
        action: ({ props }) => ({
            class: [
                'text-tile_color_light dark:text-tile_color_dark',
            ]
        }),
    },
    button: {
        root: ({ props, context }) => ({
            class: [
                'bg-prime_color_1',
                'border-prime_color_1',
                /*
                'items-center cursor-pointer inline-flex overflow-hidden relative select-none text-center align-bottom border-none',
                'transition duration-200 ease-in-out',
                'focus:outline-none focus:outline-offset-0',
                {
                    'text-white dark:text-gray-900 bg-blue-500 dark:bg-blue-400 border border-blue-500 dark:border-blue-400 hover:bg-blue-600 dark:hover:bg-blue-500 hover:border-blue-600 dark:hover:border-blue-500 focus:shadow-[0_0_0_2px_rgba(255,255,255,1),0_0_0_4px_rgba(157,193,251,1),0_1px_2px_0_rgba(0,0,0,1)] dark:focus:shadow-[0_0_0_2px_rgba(28,33,39,1),0_0_0_4px_rgba(147,197,253,0.7),0_1px_2px_0_rgba(0,0,0,0)]':
                        !props.link && props.severity === null && !props.text && !props.outlined && !props.plain,
                    'text-blue-600 bg-transparent border-transparent focus:shadow-[0_0_0_2px_rgba(255,255,255,1),0_0_0_4px_rgba(157,193,251,1),0_1px_2px_0_rgba(0,0,0,1)] dark:focus:shadow-[0_0_0_2px_rgba(28,33,39,1),0_0_0_4px_rgba(147,197,253,0.7),0_1px_2px_0_rgba(0,0,0,0)]':
                    props.link
                },
                {
                    'focus:shadow-[0_0_0_2px_rgba(255,255,255,1),0_0_0_4px_rgba(176,185,198,1),0_1px_2px_0_rgba(0,0,0,1)] dark:focus:shadow-[0_0_0_2px_rgba(28,33,39,1),0_0_0_4px_rgba(203,213,225,0.7),0_1px_2px_0_rgba(0,0,0,0)]':
                        props.severity === 'secondary',
                    'focus:shadow-[0_0_0_2px_rgba(255,255,255,1),0_0_0_4px_rgba(136,234,172,1),0_1px_2px_0_rgba(0,0,0,1)] dark:focus:shadow-[0_0_0_2px_rgba(28,33,39,1),0_0_0_4px_rgba(134,239,172,0.7),0_1px_2px_0_rgba(0,0,0,0)]':
                        props.severity === 'success',
                    'focus:shadow-[0_0_0_2px_rgba(255,255,255,1),0_0_0_4px_rgba(157,193,251,1),0_1px_2px_0_rgba(0,0,0,1)] dark:focus:shadow-[0_0_0_2px_rgba(28,33,39,1),0_0_0_4px_rgba(147,197,253,0.7),0_1px_2px_0_rgba(0,0,0,0)]':
                        props.severity === 'info',
                    'focus:shadow-[0_0_0_2px_rgba(255,255,255,1),0_0_0_4px_rgba(250,207,133,1),0_1px_2px_0_rgba(0,0,0,1)] dark:focus:shadow-[0_0_0_2px_rgba(28,33,39,1),0_0_0_4px_rgba(252,211,77,0.7),0_1px_2px_0_rgba(0,0,0,0)]':
                        props.severity === 'warning',
                    'focus:shadow-[0_0_0_2px_rgba(255,255,255,1),0_0_0_4px_rgba(212,170,251,1),0_1px_2px_0_rgba(0,0,0,1)] dark:focus:shadow-[0_0_0_2px_rgba(28,33,39,1),0_0_0_4px_rgba(216,180,254,0.7),0_1px_2px_0_rgba(0,0,0,0)]':
                        props.severity === 'help',
                    'focus:shadow-[0_0_0_2px_rgba(255,255,255,1),0_0_0_4px_rgba(247,162,162,1),0_1px_2px_0_rgba(0,0,0,1)] dark:focus:shadow-[0_0_0_2px_rgba(28,33,39,1),0_0_0_4px_rgba(252,165,165,0.7),0_1px_2px_0_rgba(0,0,0,0)]':
                        props.severity === 'danger'
                },
                {
                    'text-white dark:text-gray-900 bg-gray-500 dark:bg-gray-400 border border-gray-500 dark:border-gray-400 hover:bg-gray-600 dark:hover:bg-gray-500 hover:border-gray-600 dark:hover:border-gray-500':
                        props.severity === 'secondary' && !props.text && !props.outlined && !props.plain,
                    'text-white dark:text-gray-900 bg-green-500 dark:bg-green-400 border border-green-500 dark:border-green-400 hover:bg-green-600 dark:hover:bg-green-500 hover:border-green-600 dark:hover:border-green-500':
                        props.severity === 'success' && !props.text && !props.outlined && !props.plain,
                    'text-white dark:text-gray-900 dark:bg-blue-400 bg-blue-500 dark:bg-blue-400 border border-blue-500 dark:border-blue-400 hover:bg-blue-600 hover:border-blue-600 dark:hover:bg-blue-500 dark:hover:border-blue-500':
                        props.severity === 'info' && !props.text && !props.outlined && !props.plain,
                    'text-white dark:text-gray-900 bg-orange-500 dark:bg-orange-400 border border-orange-500 dark:border-orange-400 hover:bg-orange-600 dark:hover:bg-orange-500 hover:border-orange-600 dark:hover:border-orange-500':
                        props.severity === 'warning' && !props.text && !props.outlined && !props.plain,
                    'text-white dark:text-gray-900 bg-purple-500 dark:bg-purple-400 border border-purple-500 dark:border-purple-400 hover:bg-purple-600 dark:hover:bg-purple-500 hover:border-purple-600 dark:hover:border-purple-500':
                        props.severity === 'help' && !props.text && !props.outlined && !props.plain,
                    'text-white dark:text-gray-900 bg-red-500 dark:bg-red-400 border border-red-500 dark:border-red-400 hover:bg-red-600 dark:hover:bg-red-500 hover:border-red-600 dark:hover:border-red-500':
                        props.severity === 'danger' && !props.text && !props.outlined && !props.plain
                },
                { 'shadow-lg': props.raised },
                { 'rounded-md': !props.rounded, 'rounded-full': props.rounded },
                {
                    'bg-transparent border-transparent': props.text && !props.plain,
                    'text-blue-500 dark:text-blue-400 hover:bg-blue-300/20': props.text && (props.severity === null || props.severity === 'info') && !props.plain,
                    'text-gray-500 dark:text-gray-400 hover:bg-gray-300/20': props.text && props.severity === 'secondary' && !props.plain,
                    'text-green-500 dark:text-green-400 hover:bg-green-300/20': props.text && props.severity === 'success' && !props.plain,
                    'text-orange-500 dark:text-orange-400 hover:bg-orange-300/20': props.text && props.severity === 'warning' && !props.plain,
                    'text-purple-500 dark:text-purple-400 hover:bg-purple-300/20': props.text && props.severity === 'help' && !props.plain,
                    'text-red-500 dark:text-red-400 hover:bg-red-300/20': props.text && props.severity === 'danger' && !props.plain
                },
                { 'shadow-lg': props.raised && props.text },
                {
                    'text-gray-500 hover:bg-gray-300/20': props.plain && props.text,
                    'text-gray-500 border border-gray-500 hover:bg-gray-300/20': props.plain && props.outlined,
                    'text-white bg-gray-500 border border-gray-500 hover:bg-gray-600 hover:border-gray-600': props.plain && !props.outlined && !props.text
                },
                {
                    'bg-transparent border': props.outlined && !props.plain,
                    'text-blue-500 dark:text-blue-400 border border-blue-500 dark:border-blue-400 hover:bg-blue-300/20': props.outlined && (props.severity === null || props.severity === 'info') && !props.plain,
                    'text-gray-500 dark:text-gray-400 border border-gray-500 dark:border-gray-400 hover:bg-gray-300/20': props.outlined && props.severity === 'secondary' && !props.plain,
                    'text-green-500 dark:text-green-400 border border-green-500 dark:border-green-400 hover:bg-green-300/20': props.outlined && props.severity === 'success' && !props.plain,
                    'text-orange-500 dark:text-orange-400 border border-orange-500 dark:border-orange-400 hover:bg-orange-300/20': props.outlined && props.severity === 'warning' && !props.plain,
                    'text-purple-500 dark:text-purple-400 border border-purple-500 dark:border-purple-400 hover:bg-purple-300/20': props.outlined && props.severity === 'help' && !props.plain,
                    'text-red-500 dark:text-red-400 border border-red-500 dark:border-red-400 hover:bg-red-300/20': props.outlined && props.severity === 'danger' && !props.plain
                },
                */

                { 'px-4 py-3 text-base': props.size === null, 'text-xs py-2 px-3': props.size === 'small', 'text-xl py-3 px-4': props.size === 'large' },
                { 'flex-column': props.iconPos == 'top' || props.iconPos == 'bottom' },
                { 'opacity-60 pointer-events-none cursor-default': context.disabled }


            ]
        }),
        label: ({ props }) => ({
            class: [
                'flex-1',
                'duration-200',
                'font-bold',

                {
                    'hover:underline': props.link
                },
                { 'invisible w-0': props.label == null }
            ]
        }),
        icon: ({ props }) => ({
            class: [
                'mx-0',
                {
                    'mr-2': props.iconPos == 'left' && props.label != null,
                    'ml-2 order-1': props.iconPos == 'right' && props.label != null,
                    'mb-2': props.iconPos == 'top' && props.label != null,
                    'mt-2 order-2': props.iconPos == 'bottom' && props.label != null
                }
            ]
        }),
        badge: ({ props }) => ({
            class: [{ 'ml-2 w-4 h-4 leading-none flex items-center justify-center': props.badge }]
        })
    },
    inputswitch: {
        root: ({ props }) => ({
            class: [
            ]
        }),
        slider: ({ props }) => ({
            class: [
                'bg-tile_color_light dark:bg-tile_color_dark',
                'border border-transparent',
                'focus:outline-none focus:outline-offset-0 shadow-none',
                {
                    'bg-gray-200': !props.modelValue,
                    'bg-primary-2': props.modelValue
                }
            ]
        })
    },
    /*
    inputswitch: {
        root: ({ props }) => ({
            class: [
            ]
        }),
        slider: ({ props }) => ({
            class: [
                'border border-transparent',
                'focus:outline-none focus:outline-offset-0',
                {
                    'bg-gray-200': !props.modelValue,
                    'bg-primary-2': props.modelValue
                }
            ]
        })
    },
    inputswitch: {
        root: ({ props }) => ({
            class: [
                'inline-block relative',
                'w-12 h-7',
                {
                    'opacity-60 select-none pointer-events-none cursor-default': props.disabled
                }
            ]
        }),
        slider: ({ props }) => ([
            'absolute cursor-pointer top-0 left-0 right-0 bottom-0 border border-transparent',
            'transition-colors duration-200 rounded-2xl',
            'focus:outline-none focus:outline-offset-0 focus:shadow-[0_0_0_0.2rem_rgba(191,219,254,1)] dark:focus:shadow-[0_0_0_0.2rem_rgba(147,197,253,0.5)]',
            `before:absolute before:content-'' before:top-1/2 before:bg-mainColor_1_1 before:dark:bg-mainColor_1_1 before:w-5 before:h-5 before:left-1 before:-mt-2.5 before:rounded-full before:transition-duration-200`,
            {
                'bg-textColor_dark dark:bg-textColor_dark hover:bg-textColor_dark hover:dark:bg-textColor_light ': !props.modelValue,
                'bg-mainColor_1_2 before:transform before:translate-x-5': props.modelValue
            }])
    },
     */
    slider: {
        root: ({ props }) => ({
            class: [
            ]
        }),
        range: ({ props }) => ({
            class: [
                'bg-textColor_dark',
                'block absolute',
            ]
        }),
        handle: ({ props }) => ({
            class: [ //box-shadow: 0 0 0 1px #c7bbfa;
                'bg-white dark:bg-gray-600 border-2 border-primary-1 rounded-full transition duration-200',
                'hover:bg-primary-1 hover:border hover:border-primary-1',
            ]
        }),
        starthandler: ({ props }) => ({
            class: [
                'bg-white dark:bg-gray-600 border-2 border-primary-1 rounded-full transition duration-200',
                'hover:bg-primary-1 hover:border hover:border-primary-1 focus:shadow-[0_0_0_1px] focus:shadow-primary-1/30 ',
            ],
            'v-tooltip.top': props.modelValue,
            'v-badge.danger':"'5+'"
        }),
        endhandler: ({ props }) => ({
            class: [
                'bg-white dark:bg-gray-600 border-2 border-primary-1 rounded-full transition duration-200',
                'hover:bg-primary-1 hover:border hover:border-primary-1 focus:shadow-[0_0_0_1px] focus:shadow-primary-1/30',

            ]
        })
    },

    grid:{
        content: ({ props }) => ({
            class: [
                'text-white-200'
            ]
        }),
    },

    calendar:{
        input: ({ props }) => ({
            class: [
                'bg-tile_color_light dark:bg-tile_color_dark border-border_color_light dark:border-border_color_dark'
            ]
        }),

    },

    column: {
        columnTitle: ({props}) => ({
            class: [
                'm-0',
                'text-white',
                {},
            ]
        }),
    },

    accordiontab: {
        headerTitle: ({ }) => ({
            class: [
                'text-white dark:text-black',
            ]}),
    },

    accordion:{
        root:{
            class: [
                'text-tile_color_light dark:text-tile_color_dark',
            ]
        },

        accordiontab: {

            toggleableContent: ({ props }) => ({
                class: [
                    'm-0',
                    'bg-tile_color_light dark:bg-tile_color_dark',

                    {
                    },
                ]
            }),

            content: {
                class: [
                    //'bg-red-200 dark:bg-blue-200',
                    'bg-tile_color_light dark:bg-tile_color_dark',
                    'color-white-200',
                    'border-b_color_light dark:border-b_color_dark',
                    'text-textColor_light dark:text-textColor_dark',
                ]
            },

            header: ({ }) => ({
                class: [
                    'm-0',
                    'color-gray-600',
                    'text-textColor_light dark:text-textColor_dark',

                    {
                    },
                ]}),


            headerAction: ({ }) => ({
                class: [
                    'm-0',
                    'bg-tile_color_light dark:bg-tile_color_dark',
                    'border-b_color_light dark:border-b_color_dark',
                    'text-textColor_light dark:text-textColor_dark',
                    {
                    },
                ]}),

            headerTitle: ({ }) => ({
                class: [
                    'text-white dark:text-black',
                ]}),
        },

    },





}

export default PrimevueDesignPreset