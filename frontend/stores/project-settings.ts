const project_settings = {
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
    /*
    'theme':{
      'primary_color_light':'#ffffff',
      'primary_color_dark':'#9e9e9e',
      'gradient_from_light':"#d8d8d8",
      'gradient_to_light':"#d8d8d8",
      'gradient_from_dark':"#1f1f1f",
      'gradient_to_dark':"#1f1f1f",
      'default_theme': 'light',
    },

     */

};

export const useProjectSettingsStore = defineStore('projectSettingsStore', {
    state: () => ({
        _id: project_settings._id,
        project: project_settings.project,
        api_endpoints: project_settings.api_endpoints,
        theme: project_settings.theme,
    }),
    actions: {
        async fetch() {
            const {
                pending,
                data: projectSettings,
                error,
                refresh
            } = await useFetch('/api/project-settings', {
                key: 'projectSettings',
                onResponseError({ request, response, options }) {
                    console.error('ERROR while loading project settings: ', response);
                    //ToDo: Proper Error Handling, maybe display toast
                },
                onResponse({ request, response, options }) {
                    if (response && response._data && response._data._id) {
                        //console.debug('Loaded ProjectSettings: ', response._data);
                    }
                    if (response && response._data && !response._data._id) {
                        //console.debug('Fallback to default settings')
                    }
                },
            })

            if (projectSettings.value && projectSettings.value._id) {
                console.debug('Loaded ProjectSettings: ', projectSettings.value)
                this._id = projectSettings.value._id
                this.project = projectSettings.value.project
                this.api_endpoints = projectSettings.value.api_endpoints
                this.theme = projectSettings.value.theme
            } else {
                console.debug('Fallback to default settings')
                this._id = -1
                this.project = structuredClone(project_settings.project)
                this.api_endpoints = structuredClone(project_settings.api_endpoints)
                this.theme = structuredClone(project_settings.theme)
                //this.theme.primary_color_light = '#009b91'
            }
            console.debug("THEME: ", this.theme)
            this.setupTheme()
        },
        async save() {
            const response = await $fetch('/api/project-settings', {
                method: 'post',
                body: {
                    _id: this._id,
                    project: this.project,
                    api_endpoints: this.api_endpoints,
                    theme: this.theme
                }
            });

            if (!response.acknowledged) {
                console.error('Error while updating project settings')
            }
        },
        setupTheme(themeSettings?: {
            primary_color_light: string,
            primary_color_dark: string,
            gradient_from_light: string,
            gradient_to_light: string,
            gradient_from_dark: string,
            gradient_to_dark: string,
            default_theme: string,
        }) {
            if (themeSettings) {
                console.log('SETTING UP THEME VIA PARAM: ', themeSettings)
                //const themeSettings = projectSettings.value.theme;
                document.documentElement.style.setProperty('--color-primary', this.unifyHex(themeSettings.primary_color_light));
                document.documentElement.style.setProperty('--color-primary_light', this.unifyHex(themeSettings.primary_color_light));
                document.documentElement.style.setProperty('--color-primary_dark', this.unifyHex(themeSettings.primary_color_dark));
                document.documentElement.style.setProperty('--gradient_to_light', this.unifyHex(themeSettings.primary_color_dark));
                document.documentElement.style.setProperty('--gradient_from_light', this.unifyHex(themeSettings.gradient_from_light));
                document.documentElement.style.setProperty('--gradient_to_light', this.unifyHex(themeSettings.gradient_to_light));
                document.documentElement.style.setProperty('--gradient_from_dark', this.unifyHex(themeSettings.gradient_from_dark));
                document.documentElement.style.setProperty('--gradient_to_dark', this.unifyHex(themeSettings.gradient_to_dark));
                document.documentElement.style.setProperty('--b_color_light', this.adjustColorBrightness(themeSettings.gradient_from_light,1.1));
                document.documentElement.style.setProperty('--b_color_dark', this.adjustColorBrightness(themeSettings.gradient_from_dark, 0.9));
            } else {
                console.log('SETTING UP THEME: ', this.theme)
                //const themeSettings = projectSettings.value.theme;
                document.documentElement.style.setProperty('--color-primary', this.unifyHex(this.theme.primary_color_light));
                document.documentElement.style.setProperty('--color-primary_light', this.unifyHex(this.theme.primary_color_light));
                document.documentElement.style.setProperty('--color-primary_dark', this.unifyHex(this.theme.primary_color_dark));
                document.documentElement.style.setProperty('--gradient_to_light', this.unifyHex(this.theme.primary_color_dark));
                document.documentElement.style.setProperty('--gradient_from_light', this.unifyHex(this.theme.gradient_from_light));
                document.documentElement.style.setProperty('--gradient_to_light', this.unifyHex(this.theme.gradient_to_light));
                document.documentElement.style.setProperty('--gradient_from_dark', this.unifyHex(this.theme.gradient_from_dark));
                document.documentElement.style.setProperty('--gradient_to_dark', this.unifyHex(this.theme.gradient_to_dark));
                document.documentElement.style.setProperty('--b_color_light', this.adjustColorBrightness(this.theme.gradient_from_light,1.1));
                document.documentElement.style.setProperty('--b_color_dark', this.adjustColorBrightness(this.theme.gradient_from_dark, 0.9));
            }

        },
        unifyHex(text: string){
            return '#'+text.replace("#", "")
        },
        adjustColorBrightness(hexColor: string, factor: number): string {
            if (!/^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$/.test(hexColor)) {
                throw new Error('Ungültiger Hex-Farbwert');
            }

            let r: number = parseInt(hexColor.substring(1, 3), 16);
            let g: number = parseInt(hexColor.substring(3, 5), 16);
            let b: number = parseInt(hexColor.substring(5, 7), 16);

            r = Math.min(255, Math.max(0, r * factor));
            g = Math.min(255, Math.max(0, g * factor));
            b = Math.min(255, Math.max(0, b * factor));

            return `#${Math.round(r).toString(16).padStart(2, '0')}${Math.round(g).toString(16).padStart(2, '0')}${Math.round(b).toString(16).padStart(2, '0')}`;
        }
    }
})