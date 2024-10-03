package com.example.threads02oct;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class MainActivity extends AppCompatActivity {
    private TextView sensorDataTextView;
    private ImageView mImageView;
    private Bitmap currentImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Se inicializan las vistas
        sensorDataTextView = findViewById(R.id.sensorDataTextView);
        mImageView = findViewById(R.id.mImageView);
        Button downloadButton = findViewById(R.id.downloadButton);

        //Se inicializan los sensores
        SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Sensor rotationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);

        //Se le agrega un listener al sensor para que registre el cambio de orientación
        sensorManager.registerListener(new SensorEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                float[] rotationMatrix = new float[9];
                SensorManager.getRotationMatrixFromVector(rotationMatrix, sensorEvent.values);
                sensorDataTextView.setText("Fachero Facherito Descargado");
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {

            }
        }, rotationSensor, SensorManager.SENSOR_DELAY_NORMAL);

        if(savedInstanceState != null){
            currentImage = savedInstanceState.getParcelable("imageBitmap");
            if(currentImage != null){
                mImageView.setImageBitmap(currentImage);
            }
        }

        //Se configura el botón para descargar la imagen
        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        final Bitmap bitmap = loadImageFromNetwork("https://i.pinimg.com/736x/6c/69/1f/6c691f3da80e90b5f891a2b936af190e.jpg");
                        if(bitmap != null){
                            Log.d("Download", "Iniciando la descarga de la imagen");
                            currentImage = bitmap;
                            mImageView.post(new Runnable(){
                                @Override
                                public void run() {
                                    mImageView.setImageBitmap(bitmap);
                                }
                            });
                        } else {
                            //Mensaje por si la descarga falla
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(MainActivity.this, "Error descargando la imagen", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                }).start();
            }
        });
    }
    //Método que permite descargar una imagen desde una URL
    private Bitmap loadImageFromNetwork(String url){
        Bitmap bitmap = null;
        try {
            java.net.URL imageURL = new java.net.URL(url);
            HttpURLConnection connection = (HttpURLConnection) imageURL.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            bitmap = BitmapFactory.decodeStream(input);
            } catch (IOException e){
            e.printStackTrace();
        }
        return bitmap;
    }

    //Se guarda el estado de la actividad
    protected void onSaveInstanceState(@NonNull Bundle outState){
        super.onSaveInstanceState(outState);
        if(currentImage != null){
            outState.putParcelable("imageBitmap", currentImage);
        }
    }
}