package com.example.appalmi.fragmentos;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.appalmi.R;

import org.osmdroid.config.Configuration;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

public class FragmentMapa extends Fragment {

    private MapView mapa;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                              @Nullable Bundle savedInstanceState) {
        
        Context ctx = requireActivity().getApplicationContext();
        Configuration.getInstance().load(ctx, ctx.getSharedPreferences("osmdroid", Context.MODE_PRIVATE));
        Configuration.getInstance().setUserAgentValue("AppAlmi2/1.0");
        
        return inflater.inflate(R.layout.fragment_mapa, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mapa = view.findViewById(R.id.mapaFragment);
        
        if (mapa != null) {
            org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase esriCalles = new org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase(
                    "EsriCalles", 0, 19, 256, ".png",
                    new String[]{"https://server.arcgisonline.com/ArcGIS/rest/services/World_Street_Map/MapServer/tile/"},
                    "Tiles © Esri")
            {
                @Override
                public String getTileURLString(long pMapTileIndex)
                {
                    return getBaseUrl()
                            + org.osmdroid.util.MapTileIndex.getZoom(pMapTileIndex) + "/"
                            + org.osmdroid.util.MapTileIndex.getY(pMapTileIndex) + "/"
                            + org.osmdroid.util.MapTileIndex.getX(pMapTileIndex);
                }
            };
            mapa.setTileSource(esriCalles);
            mapa.setMultiTouchControls(true);

            GeoPoint almiPoint = new GeoPoint(43.271461, -2.948372);
            
            mapa.getController().setZoom(19.0);
            mapa.getController().setCenter(almiPoint);

            Marker markerAlmi = new Marker(mapa);
            markerAlmi.setPosition(almiPoint);
            markerAlmi.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            markerAlmi.setTitle("Almi zzzz");
            mapa.getOverlays().add(markerAlmi);
            
            mapa.invalidate();
        }
    }
    
    @Override
    public void onResume() {
        super.onResume();
        if (mapa != null) {
            mapa.onResume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mapa != null) {
            mapa.onPause();
        }
    }
}
