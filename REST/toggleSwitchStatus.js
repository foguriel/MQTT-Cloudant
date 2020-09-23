const axios = require('axios')

const logError = error => console.log(error.response)

axios.defaults.baseURL = "https://6relw0.internetofthings.ibmcloud.com/api/v0002/"

const toggleSwitchStatus = () => axios.post("https://6relw0.messaging.internetofthings.ibmcloud.com/api/v0002/application/types/Switch/devices/LIGHTSWITCH1/commands/switch_request", {
    request: "toggle"
}, {
    headers: {
        accept: "application/json"
    },
    auth: {
        username: "a-6relw0-qcs1tfgehi",
        password: "oOHaAYApM8YXYKIObq"
    }
}).then(
    res => console.log(res.data)
).catch(logError)

toggleSwitchStatus()
