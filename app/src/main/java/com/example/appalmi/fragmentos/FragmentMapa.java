package com.example.appalmi.fragmentos;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

public class FragmentMapa extends Fragment implements OnMapReadyCallback {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_mapa, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Buscar el SupportMapFragment en el layout
        SupportMapFragment mapaFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.mvMapa);

        // Llamar asíncronamente a Google Maps (requiere API KEY)
        if (mapaFragment != null) {
            mapaFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        
        // Coordenadas de Almi
        LatLng almiPoint = new LatLng(43.271461, -2.948372);

        // Añadir el marcador de Almi
        googleMap.addMarker(new MarkerOptions()
                .position(almiPoint)
                .title("Centro Almi")
                .snippet("¡Bienvenidos!"));

        // Mover la cámara a Almi con Zoom
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(almiPoint, 18f));
    }
}
