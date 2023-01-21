package com.diaryofaprogrammingnoob.mqttnetwork.data

data class MqttConnectionOptions(
    val username: String = "solace-cloud-client",
    val password: String = "fkjlar5b756khs19qlo97061b8",
    val host: String = "wss://mr-connection-dk2x3vdosvt.messaging.solace.cloud:8443",
    val timeout: Int = 3,
    val keep_alive_interval: Int = 60,
    val cleanSession: Boolean = true,
    val reconnect: Boolean = true
)