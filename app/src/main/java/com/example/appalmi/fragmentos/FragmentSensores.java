package com.example.appalmi.fragmentos;

import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.appalmi.R;

public class FragmentSensores extends Fragment implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor proximitySensor;

    private TextView tvProximidad;
    private LinearLayout llFondo;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sensores, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvProximidad = view.findViewById(R.id.tvProximidad);
        llFondo = view.findViewById(R.id.llSensoresFondo);

        sensorManager = (SensorManager) requireActivity().getSystemService(Context.SENSOR_SERVICE);

        if (sensorManager != null) {
            proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        }

        if (proximitySensor == null) tvProximidad.setText("Sensor de proximidad NO disponible");
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
            float distancia = event.values[0];
            if (distancia < proximitySensor.getMaximumRange()) {
                tvProximidad.setText("¡Pantalla Tapada! (Distancia: " + distancia + ")");
                tvProximidad.setTextColor(Color.RED);
            } else {
                tvProximidad.setText("Libre (Distancia: " + distancia + ")");
                tvProximidad.setTextColor(Color.BLACK);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // No necesario
    }

    @Override
    public void onResume() {
        super.onResume();
        if (sensorManager != null) {
            if (proximitySensor != null) sensorManager.registerListener(this, proximitySensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
    }
}