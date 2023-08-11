package com.example.testmap;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MainActivity extends AppCompatActivity {
    MQTTHelper mqttHelper;
    Button btnGetLocation,btnConnect,btnDisconnect;
    double Lat,Lng;
    final String cmdTopic = "havanduc/feeds/command";
    final String command = "execute";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /** Initialize MQTT and connect to your broker URI*/
        mqttHelper = new MQTTHelper(this);
        mqttHelper.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean reconnect, String serverURI) {

            }

            @Override
            public void connectionLost(Throwable cause) {

            }

            @Override
            /** Convert message from bytes to String and then to double*/
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                String msg = new String(message.getPayload());
                String[] latlng = msg.split("-");
                Lat = Double.parseDouble(latlng[0]);
                Lng = Double.parseDouble(latlng[1]);
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });

        btnGetLocation =(Button) findViewById(R.id.btnGetLocation);

        /** Action when button "Get device location" is click */
        btnGetLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /** Move beginning layout to Google map layout */
                Intent intent = new Intent(MainActivity.this,MapsActivity.class);
                intent.putExtra("Lat", Lat);    /** Send data "Latitude" to MapsActivity */
                intent.putExtra("Long", Lng);   /** Send data "Longitude" to MapsActivity */
                startActivity(intent);
            }
        });

        btnDisconnect = (Button) findViewById(R.id.btnConnect);

        /** Disconnect to MQTT when click button "Disconnect"*/
        btnDisconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mqttHelper.disconnect();
                Toast.makeText(MainActivity.this,"Disconnected!",Toast.LENGTH_LONG).show();
            }
        });

        btnConnect = (Button) findViewById(R.id.btnConnect);

        /** Send request to broker to get device location */
        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /** Connect to broker, publish message "execute" to request other device to send its location
                  and subscribe to get that location message */
                mqttHelper.publishMessage(cmdTopic,command);


                if(mqttHelper.mqttAndroidClient.isConnected()){
                    Toast.makeText(MainActivity.this,"Connected!",Toast.LENGTH_LONG).show();
                }
                else{
                    Toast.makeText(MainActivity.this,"Can not connect!",Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}

