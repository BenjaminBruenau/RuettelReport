import {TRANSITIONS as props} from "primevue/passthrough/tailwind/index.esm.js";

const customColor = '[#93c5fd]';

console.log(`bg-${customColor}`)

const PrimevueDesignPreset = {

    inputtext: {
        root: ({ props, context, parent }) => ({
            class: [
                'm-0',
                'border-b_color_light dark:border-b_color_dark',
                'font-sans text-gray-600 dark:text-white/80 bg-tile_color_light dark:bg-tile_color_dark transition-colors duration-200 appearance-none rounded-lg',
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
    tabview:{
        nav: ({ props }) => ({
            class: [
                'bg-tile_color_light dark:bg-tile_color_dark',
                'border-b_color_light dark:border-b_color_dark',
                'text-tile_color_light dark:text-tile_color_dark',
            ]
        }),
        panelContainer: ({ props }) => ({
            class: [
                'bg-tile_color_light dark:bg-tile_color_dark',
                'border-b_color_light dark:border-b_color_dark',
                'text-tile_color_light dark:text-tile_color_dark',
            ]
        }),
    },


    tabpanel:{
        content: ({ props, state }) => ({

            class: [
                'bg-tile_color_light dark:bg-tile_color_dark',
                'border-b_color_light dark:border-b_color_dark',
                'text-textColor_light dark:text-textColor_dark',
            ],


        }),
    },


    button: {
        root: ({ props, context }) => ({
            class: [

                'bg-primary_light dark:bg-primary_dark',
                'border-primary_light dark:border-primary_dark',

                'shadow-[3px_3px_0px_0_rgba(0,0,0,0.2)] dark:shadow-[3px_3px_0px_0_rgba(255,255,255,0.4)]',
                'text-contrastText_light dark:contrastText_dark',

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
                    'bg-grey-500': !props.modelValue,
                    'bg-grey-400': props.modelValue
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
                'bg-tile_color_light dark:tile_color_dark',
                //'block absolute',
            ]
        }),
        /*
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
        })*/
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
                'bg-tile_color_light dark:bg-tile_color_dark border-border_color_light dark:border-border_color_dark',
                'border-b_color_light dark:border-b_color_dark',
                'text-textColor_light dark:text-textColor_dark',
            ]
        }),
        root: ({ props }) => ({
            class: [
                'bg-tile_color_light dark:bg-tile_color_dark border-border_color_light dark:border-border_color_dark',
                'border-b_color_light dark:border-b_color_dark',
                'text-textColor_light dark:text-textColor_dark',
            ]
        }),
        dropdownButton: ({ props }) => ({
            class: [
                'bg-tile_color_light dark:bg-tile_color_dark border-border_color_light dark:border-border_color_dark',
                'border-b_color_light dark:border-b_color_dark',
                'text-textColor_light dark:text-textColor_dark',
            ]
        }),
        panel: ({ props }) => ({
            class: [
                'bg-tile_color_light dark:bg-tile_color_dark border-border_color_light dark:border-border_color_dark',
                'border-b_color_light dark:border-b_color_dark',
                'text-textColor_light dark:text-textColor_dark',
            ]
        }),
        container: ({ props }) => ({
            class: [
                'bg-tile_color_light dark:bg-tile_color_dark border-border_color_light dark:border-border_color_dark',
                'border-b_color_light dark:border-b_color_dark',
                'text-textColor_light dark:text-textColor_dark',
            ]
        }),
        group: ({ props }) => ({
            class: [
                'bg-tile_color_light dark:bg-tile_color_dark border-border_color_light dark:border-border_color_dark',
                'border-b_color_light dark:border-b_color_dark',
                'text-textColor_light dark:text-textColor_dark',
            ]
        }),
        groupcontainer: ({ props }) => ({
            class: [
                'bg-tile_color_light dark:bg-tile_color_dark border-border_color_light dark:border-border_color_dark',
                'border-b_color_light dark:border-b_color_dark',
                'text-textColor_light dark:text-textColor_dark',
            ]
        }),
        table: ({ props }) => ({
            class: [
                'bg-tile_color_light dark:bg-tile_color_dark border-border_color_light dark:border-border_color_dark',
                'border-b_color_light dark:border-b_color_dark',
                'text-textColor_light dark:text-textColor_dark',
            ]
        }),
        hiddenSelectedDay: ({ props }) => ({
            class: [
                'bg-tile_color_light dark:bg-tile_color_dark border-border_color_light dark:border-border_color_dark',
                'border-b_color_light dark:border-b_color_dark',
                'text-textColor_light dark:text-textColor_dark',
            ]
        }),
        component : ({ props }) => ({
            class: [
                'bg-tile_color_light dark:bg-tile_color_dark border-border_color_light dark:border-border_color_dark',
                'border-b_color_light dark:border-b_color_dark',
                'text-textColor_light dark:text-textColor_dark',
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

    card:{
        root: ({ }) => ({
            class: [
                'bg-tile_color_light dark:bg-tile_color_dark',
                'border-b_color_light dark:border-b_color_dark',
                'text-textColor_light dark:text-textColor_dark',
            ]}),
        header: ({ }) => ({
            class: [
                'bg-tile_color_light dark:bg-tile_color_dark',
                'border-b_color_light dark:border-b_color_dark',
                'text-textColor_light dark:text-textColor_dark',
            ]}),
        body: ({ }) => ({
            class: [
                'bg-tile_color_light dark:bg-tile_color_dark',
                'border-b_color_light dark:border-b_color_dark',
                'text-textColor_light dark:text-textColor_dark',
            ]}),
        subtitle: ({ }) => ({
            class: [
                'text-textColor_light dark:text-textColor_dark',
            ]}),
    },

    dialog:{
        root: ({ }) => ({
            class: [
                'bg-tile_color_light dark:bg-tile_color_dark',
                'border-b_color_light dark:border-b_color_dark',
                'text-textColor_light dark:text-textColor_dark',
            ]}),
        content: ({ }) => ({
            class: [
                'bg-tile_color_light dark:bg-tile_color_dark',
                'border-b_color_light dark:border-b_color_dark',
                'text-textColor_light dark:text-textColor_dark',
            ]}),
        header: ({ }) => ({
            class: [
                'bg-tile_color_light dark:bg-tile_color_dark',
                'border-b_color_light dark:border-b_color_dark',
                'text-textColor_light dark:text-textColor_dark',
            ]}),
        footer: ({ }) => ({
            class: [
                'bg-tile_color_light dark:bg-tile_color_dark',
                'border-b_color_light dark:border-b_color_dark',
                'text-textColor_light dark:text-textColor_dark',
            ]}),
    },

    datatable:{

        bodycell: ({ }) => ({
            class: [
                'border-b_color_light dark:border-b_color_dark',
                'bg-tile_color_light dark:bg-tile_color_dark',
            ]
        }),
        emptyMessage: ({ }) => ({
            class: [
                'border-b_color_light dark:border-b_color_dark',
                'bg-tile_color_light dark:bg-tile_color_dark',
            ]
        }),
        emptyMessageCell: ({ }) => ({
            class: [
                'border-b_color_light dark:border-b_color_dark',
                'bg-tile_color_light dark:bg-tile_color_dark',
            ]
        }),
        root: ({ }) => ({
            class: [
                'bg-tile_color_light dark:bg-tile_color_dark',
                'border-b_color_light dark:border-b_color_dark',
                'text-textColor_light dark:text-textColor_dark',
            ]}),
        header: ({ }) => ({
            class: [
                'bg-tile_color_light dark:bg-tile_color_dark',
                'border-b_color_light dark:border-b_color_dark',
                'text-textColor_light dark:text-textColor_dark',
            ]}),
        bodyrow: ({ }) => ({
            class: [
                'bg-tile_color_light dark:bg-tile_color_dark',
                'border-b_color_light dark:border-b_color_dark',
                'text-textColor_light dark:text-textColor_dark',
            ]}),
        paginatorBottom: ({ }) => ({
            class: [
                'bg-tile_color_light dark:bg-tile_color_dark',
                'border-b_color_light dark:border-b_color_dark',
                'text-textColor_light dark:text-textColor_dark',
            ]}),

        virtualScrollerSpacer: ({ }) => ({
            class: [
                'bg-tile_color_light dark:bg-tile_color_dark',
                'border-b_color_light dark:border-b_color_dark',
                'text-textColor_light dark:text-textColor_dark',
            ]}),
        table: ({ }) => ({
            class: [
                'border-b_color_light dark:border-b_color_dark'
            ]
        }),
    },

    column: {
        root: ({props}) => ({
            class: [
                'm-0',
                'text-textColor_light dark:text-textColor_dark',
                'bg-tile_color_light dark:bg-tile_color_dark',
                'border-b_color_light dark:border-b_color_dark'
            ]
        }),
    },


    row:{
        root: ({props}) => ({
            class: [
                'm-0',
                'text-textColor_light dark:text-textColor_dark',
                'bg-tile_color_light dark:bg-tile_color_dark',
                'border-b_color_light dark:border-b_color_dark',
            ]
        }),
    },

    splitter:{
        root: ({ }) => ({
            class: [
                'bg-tile_color_light dark:bg-tile_color_dark',
                'border-b_color_light dark:border-b_color_dark',
                'text-textColor_light dark:text-textColor_dark',
            ]}),
        gutter: ({ }) => ({
            class: [
                'bg-tile_color_light dark:bg-tile_color_dark',
                'border-b_color_light dark:border-b_color_dark',
                'text-textColor_light dark:text-textColor_dark',
            ]}),
        gutterhandler: ({ }) => ({
            class: [
                'bg-tile_color_dark dark:bg-tile_color_dark',
                'border-b_color_dark dark:border-b_color_dark',
                'text-textColor_light dark:text-textColor_dark',
            ]}),
    },

    checkbox: {
        root: ({ props, state }) => (
            'bg-tile_color_dark dark:bg-tile_color_dark',
                'border-b_color_dark dark:border-b_color_dark',
                'text-textColor_light dark:text-textColor_dark',
            {
        }),
        input: ({ props, state }) => (
            'bg-tile_color_dark dark:bg-tile_color_dark',
                'border-b_color_dark dark:border-b_color_dark',
                'text-textColor_light dark:text-textColor_dark',
                {
                }),
        icon: ({ props, state }) => (
            'bg-tile_color_dark dark:bg-tile_color_dark',
                'border-b_color_dark dark:border-b_color_dark',
                'text-textColor_light dark:text-textColor_dark',
                {
                }),
        hiddenInputWrapper: ({ props, state }) => (
            'bg-tile_color_dark dark:bg-tile_color_dark',
                'border-b_color_dark dark:border-b_color_dark',
                'text-textColor_light dark:text-textColor_dark',
                {
                }),
        hiddenInput: ({ props, state }) => (
            'bg-tile_color_dark dark:bg-tile_color_dark',
                'border-b_color_dark dark:border-b_color_dark',
                'text-textColor_light dark:text-textColor_dark',
                {
                }),
        component: ({ props, state }) => (
            'bg-tile_color_dark dark:bg-tile_color_dark',
                'border-b_color_dark dark:border-b_color_dark',
                'text-textColor_light dark:text-textColor_dark',
                {
                }),
    },

    fileupload:{
        buttonbar: ({ }) => ({
            class: [
                'bg-tile_color_light dark:bg-tile_color_dark',
                'border-b_color_light dark:border-b_color_dark',
                'text-textColor_light dark:text-textColor_dark',
            ]}),
        content: ({ }) => ({
            class: [
                'bg-tile_color_light dark:bg-tile_color_dark',
                'border-b_color_light dark:border-b_color_dark',
                'text-textColor_light dark:text-textColor_dark',
            ]}),

        chooseButton: ({ }) => ({
            class: [
                'bg-tile_color_light dark:bg-tile_color_dark',
                'border-b_color_light dark:border-b_color_dark',
                'text-textColor_light dark:text-textColor_dark',
            ]}),
        uploadButton: ({ }) => ({
            class: [
                'bg-tile_color_light dark:bg-tile_color_dark',
                'border-b_color_light dark:border-b_color_dark',
                'text-textColor_light dark:text-textColor_dark',
            ]}),
        cancelButton: ({ }) => ({
            class: [
                'bg-tile_color_light dark:bg-tile_color_dark',
                'border-b_color_light dark:border-b_color_dark',
                'text-textColor_light dark:text-textColor_dark',
            ]}),
    },
    colorpicker: {
        input: ({ props, state }) => ({
            class: [
                'border-b_color_light dark:border-b_color_dark',
            ]
        }),
    },
    inputgroupaddon:{
        root: ({ }) => ({
            class: [
                'bg-tile_color_light dark:bg-tile_color_dark',
                'border-b_color_light dark:border-b_color_dark',
                'text-textColor_light dark:text-textColor_dark',
            ]}),
    },

}

export default PrimevueDesignPreset