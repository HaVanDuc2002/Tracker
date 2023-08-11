package com.example.transmitter;

import android.content.Context;

import android.util.Log;

import info.mqtt.android.service.Ack;
import info.mqtt.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.DisconnectedBufferOptions;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MQTTHelper {
    public MqttAndroidClient mqttAndroidClient;

    final String clientID = "havanduc";
    final String username = "havanduc";
    final String password = "aio_taHR20sq4HoBMalMJzBOm0llbvqw";
    final String serverUri = "tcp://io.adafruit.com:1883";
    final String cmdTopic = "havanduc/feeds/command";
    final String getLocationTopic = "havanduc/feeds/location";
    static boolean retained = false;
    static int qos = 0;
    final String cmd = "execute";

    public MQTTHelper(Context context) {
        mqttAndroidClient = new MqttAndroidClient(context, serverUri, clientID, Ack.AUTO_ACK);
        connect();
        mqttAndroidClient.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean b, String s) {
                Log.w("mqtt", s);
            }

            @Override
            public void connectionLost(Throwable throwable) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage mqttMessage) {
                String command = new String(mqttMessage.getPayload());
                Log.d("mqtt1",command);
                if(command.equals(cmd)){
                    publishMessage(getLocationTopic,MainActivity.Lat+"-"+MainActivity.Lng);
                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

            }
        });
    }

    public void setCallback(MqttCallbackExtended callback) {
        mqttAndroidClient.setCallback(callback);
    }

    private void disconnect() {
        mqttAndroidClient.disconnect();
    }
    /**
     *funtion    Connect to broker with your account and receive message
     *param      None
     *retval     None
     */
    private void connect() {
        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setAutomaticReconnect(true);
        mqttConnectOptions.setCleanSession(false);
        mqttConnectOptions.setUserName(username);
        mqttConnectOptions.setPassword(password.toCharArray());

        mqttAndroidClient.connect(mqttConnectOptions, null, new IMqttActionListener() {
            @Override
            public void onSuccess(IMqttToken asyncActionToken) {

                DisconnectedBufferOptions disconnectedBufferOptions = new DisconnectedBufferOptions();
                disconnectedBufferOptions.setBufferEnabled(true);
                disconnectedBufferOptions.setBufferSize(100);
                disconnectedBufferOptions.setPersistBuffer(false);
                disconnectedBufferOptions.setDeleteOldestMessages(false);
                mqttAndroidClient.setBufferOpts(disconnectedBufferOptions);
                subscribeToTopic(cmdTopic);
            }

            @Override
            public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                Log.d("mqtt1","Can not connect");
            }
        });

    }
    /**
     *funtion    Subscribe to broker with your wished topic
     *param      Topic to subscribe
     *retval     None
     */
    public void subscribeToTopic(String topic) {
        mqttAndroidClient.subscribe(topic, qos, null, new IMqttActionListener() {
            @Override
            public void onSuccess(IMqttToken asyncActionToken) {
                Log.d("mqtt1", "Subscribed!");
            }

            @Override
            public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                Log.d("mqtt1", "Failed to subscribe!");
            }
        });

    }

    /**
     * fuction      Publish message to broker
     * param        Topic and message you want to send
     * retval       None
     */

    public void publishMessage(String topic, String message) {
        mqttAndroidClient.publish(topic, message.getBytes(),qos,retained,null,new IMqttActionListener() {
            @Override
            public void onSuccess(IMqttToken asyncActionToken) {
                Log.d("mqtt1", "Published!");
            }

            @Override
            public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                Log.d("mqtt1", "Failed to publish!");
            }
        });
    }
    }
