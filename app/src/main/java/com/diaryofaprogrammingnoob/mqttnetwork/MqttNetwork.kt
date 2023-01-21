package com.diaryofaprogrammingnoob.mqttnetwork

import io.reactivex.Observable

interface MqttNetwork{
    fun publish(message: String)
    fun subscribe()
    fun connect()
    fun getObservable(): Observable<String>
}