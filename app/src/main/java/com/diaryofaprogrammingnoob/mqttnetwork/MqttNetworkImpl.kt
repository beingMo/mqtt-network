package com.diaryofaprogrammingnoob.mqttnetwork

import android.content.Context
import com.diaryofaprogrammingnoob.mqttnetwork.data.MqttConnectionOptions
import io.reactivex.Observable
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.*
import timber.log.Timber

class MqttNetworkImpl(context: Context) : MqttNetwork {

    companion object {
        const val TAG = "MqttNetworkImpl"
    }

    var mqttAndroidClient: MqttAndroidClient
    private val connectionOptions = MqttConnectionOptions()

    val serverUri = connectionOptions.host
    private val clientId: String = MqttClient.generateClientId()

    init {
        mqttAndroidClient = MqttAndroidClient(context, serverUri, clientId)
        connect()
    }

    override fun connect() {
        val mqttConnectOptions = MqttConnectOptions()

        mqttConnectOptions.isAutomaticReconnect = connectionOptions.reconnect
        mqttConnectOptions.isCleanSession = connectionOptions.cleanSession
        mqttConnectOptions.userName = connectionOptions.username
        mqttConnectOptions.password = connectionOptions.password.toCharArray()
        mqttConnectOptions.connectionTimeout = connectionOptions.timeout
        mqttConnectOptions.keepAliveInterval = connectionOptions.keep_alive_interval

        try {
            mqttAndroidClient.connect(
                mqttConnectOptions,
                null,
                object : IMqttActionListener {
                    override fun onSuccess(asyncActionToken: IMqttToken?) {
                        val disconnectedBufferOptions = DisconnectedBufferOptions()

                        disconnectedBufferOptions.isBufferEnabled = true
                        disconnectedBufferOptions.bufferSize = 100
                        disconnectedBufferOptions.isPersistBuffer = false
                        disconnectedBufferOptions.isDeleteOldestMessages = false
                        mqttAndroidClient.setBufferOpts(disconnectedBufferOptions)
                    }

                    override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                        Timber.d("Failed to connect - $exception")
                    }
                }
            )
        } catch (exception: MqttException) {
            exception.printStackTrace()
        }
    }

    override fun getObservable(): Observable<String> {
        return Observable.create { emitter ->
            mqttAndroidClient.setCallback(
                object : MqttCallbackExtended {
                    override fun connectionLost(cause: Throwable?) {
                        TODO("Not yet implemented")
                    }

                    override fun messageArrived(topic: String?, message: MqttMessage?) {
                        emitter.onNext(message.toString())
                    }

                    override fun deliveryComplete(token: IMqttDeliveryToken?) {
                        TODO("Not yet implemented")
                    }

                    override fun connectComplete(reconnect: Boolean, serverURI: String?) {
                        TODO("Not yet implemented")
                    }

                }
            )
        }
    }

    override fun subscribe() {
        try {
            mqttAndroidClient.subscribe(
                "send/message",
                0,
                null,
                object : IMqttActionListener {
                    override fun onSuccess(asyncActionToken: IMqttToken?) {
                        Timber.d("Subscribed")
                    }

                    override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                        Timber.d("Subscribe failed")
                    }

                }
            )
        } catch (exception: MqttException) {
            Timber.d("Could not subscribe.")
        }
    }

    override fun publish(message: String) {
        try {
            val mMessage = MqttMessage()
            mMessage.payload = message.toByteArray()
            mqttAndroidClient.publish(
                "send/message",
                mMessage.payload,
                0,
                false
            )
        } catch (exception: MqttException) {
            Timber.d("Error publishing - ${exception.message}")
        }
    }

    private fun destroy() {
        mqttAndroidClient.unregisterResources()
        mqttAndroidClient.disconnect()
    }
}