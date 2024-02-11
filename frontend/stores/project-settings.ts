export const useProjectSettingsStore = defineStore('projectSettingsStore', {
    state: () => ({
        '_id': -1,
        'project':{
            'users':{

            }
        },
        'api_endpoints':{
            'earthquake.usgs.gov': {
                url: 'https://earthquake.usgs.gov/fdsnws/event/1/query',
                method: 'GET',
                color: '#009b91',
                params: {
                    format: 'format',
                    starttime: 'starttime',
                    endtime: 'endtime',
                    minmagnitude: 'minmagnitude',
                    maxmagnitude: 'maxmagnitude',
                    minlongitude: 'minlongitude',
                    maxlongitude: 'maxlongitude',
                    minlatitude: 'minlatitude',
                    maxlatitude: 'maxlatitude',
                },
                mappingRules: '',
            },
            'earthquake.usgs.gov2': {
                url: 'https://earthquake.usgs.gov/fdsnws/event/1/query',
                method: 'GET',
                color: '#e1967a',
                params: {
                    format: 'format',
                    starttime: 'starttime',
                    endtime: 'endtime',
                    minmagnitude: 'minmagnitude',
                    maxmagnitude: 'maxmagnitude',
                    minlongitude: 'minlongitude',
                    maxlongitude: 'maxlongitude',
                    minlatitude: 'minlatitude',
                    maxlatitude: 'maxlatitude',
                },
                mappingRules: '',
            }

        },
        'theme':{
            'primary_color_light':'#009b91',
            'primary_color_dark':'#009b91',
            'gradient_from_light':"#dde6eb",
            'gradient_to_light':"#dde6eb",
            'gradient_from_dark':"#334152",
            'gradient_to_dark':"#334152",
            'default_theme': 'light',
        },
    }),
    actions: {
        async fetch() {
            const infos = await $fetch('https://api.nuxt.com/modules/pinia')
            const response = await $fetch('/api/project-settings');
            //this = response
            console.log('LOADED SETTINGS: ', response)

        }
    }
})