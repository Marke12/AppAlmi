package com.example.appalmi.fragmentos;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.appalmi.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FragmentMapa extends Fragment implements OnMapReadyCallback {

    private GoogleMap miMapa;
    private EditText etBusqueda;

    // EXAMEN
    private ActivityResultLauncher<Intent> vozLauncher;
    private SensorManager sensorManager;
    private Sensor proximitySensor;
    private SensorEventListener proximityListener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Inicializamos el Launcher de voz antes de crear la vista
        vozLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                resultado -> {
                    if (resultado.getResultCode() == android.app.Activity.RESULT_OK && resultado.getData() != null) {
                        ArrayList<String> textos = resultado.getData().getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                        if (textos != null && !textos.isEmpty()) {
                            // Ponemos lo que ha escuchado en el cajetín y lanzamos la búsqueda en el mapa
                            etBusqueda.setText(textos.get(0));
                            buscarLugar();
                        }
                    }
                });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_mapa, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        etBusqueda = view.findViewById(R.id.etBusquedaMapa);

        // -------------------------------------------------------------
        // EXAMEN (Manual "Voz" y "Sensores")
        // -------------------------------------------------------------
        Button btnVoz = view.findViewById(R.id.btnVozMapa);
        if (btnVoz != null) {
            btnVoz.setOnClickListener(v -> invocarReconocimientoDeVoz());
        }

        // Búsqueda al pulsar el botón "Buscar" (Manual normal)
        Button btnBuscar = view.findViewById(R.id.btnBuscarMapa);
        if (btnBuscar != null) {
            btnBuscar.setOnClickListener(v -> buscarLugar());
        }

        // --- SENSOR ---
        sensorManager = (SensorManager) requireActivity().getSystemService(Context.SENSOR_SERVICE);
        proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);

        proximityListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                if (event.values[0] < proximitySensor.getMaximumRange()) {
                    // Si tapa el sensor, borramos el cuadro de búsqueda y reseteamos el mapa a Almi
                    etBusqueda.setText("");
                    if(miMapa != null) {
                        miMapa.clear();
                        LatLng almiPoint = new LatLng(43.271461, -2.948372);
                        miMapa.addMarker(new MarkerOptions().position(almiPoint).title("Centro Almi"));
                        miMapa.animateCamera(CameraUpdateFactory.newLatLngZoom(almiPoint, 18f));
                    }
                }
            }
            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) { }
        };
        // -------------------------------------------------------------

        SupportMapFragment mapaFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.mvMapa);

        if (mapaFragment != null) {
            mapaFragment.getMapAsync(this);
        }
    }

    private void invocarReconocimientoDeVoz() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Dime dónde quieres ir...");
        vozLauncher.launch(intent);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        miMapa = googleMap;
        LatLng almiPoint = new LatLng(43.271461, -2.948372);
        miMapa.addMarker(new MarkerOptions().position(almiPoint).title("Centro Almi"));
        miMapa.moveCamera(CameraUpdateFactory.newLatLngZoom(almiPoint, 18f));
    }

    private void buscarLugar() {
        String texto = etBusqueda.getText().toString();
        if (texto.isEmpty() || miMapa == null) return;

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            Geocoder geocoder = new Geocoder(requireContext());
            try {
                List<Address> resultados = geocoder.getFromLocationName(texto, 1);
                requireActivity().runOnUiThread(() -> {
                    if (resultados != null && !resultados.isEmpty()) {
                        Address direccion = resultados.get(0);
                        LatLng nuevaPosicion = new LatLng(direccion.getLatitude(), direccion.getLongitude());
                        miMapa.clear();
                        miMapa.addMarker(new MarkerOptions().position(nuevaPosicion).title(texto));
                        miMapa.animateCamera(CameraUpdateFactory.newLatLngZoom(nuevaPosicion, 15f));
                    } else {
                        Toast.makeText(requireContext(), "No se ha encontrado el lugar", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (IOException e) {
                requireActivity().runOnUiThread(() -> Toast.makeText(requireContext(), "Error al buscar", Toast.LENGTH_SHORT).show());
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (proximitySensor != null) {
            sensorManager.registerListener(proximityListener, proximitySensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (sensorManager != null) {
            sensorManager.unregisterListener(proximityListener);
        }
    }
}
