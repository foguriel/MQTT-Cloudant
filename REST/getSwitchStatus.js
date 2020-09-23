const axios = require('axios')

const logError = error => console.log(error.response)

axios.defaults.baseURL = "https://6relw0.internetofthings.ibmcloud.com/api/v0002/"

const getSwitchStatus = () => axios.get("device/types/Switch/devices/LIGHTSWITCH1/state/5f66bdc53be333000929d6d3", {
    
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


getSwitchStatus()