package com.example.transmitter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MainActivity extends AppCompatActivity {
    FusedLocationProviderClient fusedLocationProviderClient;
    final static int REQUEST_CODE = 100;
    static double Lat,Lng;
    MQTTHelper mqttHelper;
    static final String getLocationTopic = "havanduc/feeds/send-location";
    static final String cmd = "execute";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        getLastLocation();
    }

    /**
     * function         Get last available location
     * param            None
     * retval           None
     * */
    private void getLastLocation(){
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if(location != null){
                        Lat = location.getLatitude();
                        Lng = location.getLongitude();

                         /** Initialize MQTT, connect to broker with your account and subscribe to command topic to get request */
                        mqttHelper = new MQTTHelper(MainActivity.this);
                        mqttHelper.setCallback(new MqttCallbackExtended() {
                            @Override
                            public void connectComplete(boolean reconnect, String serverURI) {

                            }

                            @Override
                            public void connectionLost(Throwable cause) {

                            }

                            /** If get correct command, publish location to broker */
                            @Override
                            public void messageArrived(String topic, MqttMessage message) throws Exception {
                                String command = new String(message.getPayload());
                                if(command.equals(cmd)){
                                    mqttHelper.publishMessage(getLocationTopic,Lat+"-"+Lng);
                                }
                            }

                            @Override
                            public void deliveryComplete(IMqttDeliveryToken token) {

                            }
                        });
                    }
                }
            });
        }else{
            askPermission();
        }
    }

    /**
     * function         Request permission to access your location
     * param            None
     * retval           None
     * */
    private void askPermission() {
        ActivityCompat.requestPermissions(MainActivity.this,new String[] {Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_CODE );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == REQUEST_CODE){
            if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                getLastLocation();
            }
            else {
                Toast.makeText(this,"Require permission!",Toast.LENGTH_SHORT).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}