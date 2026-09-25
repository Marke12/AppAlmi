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

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.util.MapTileIndex;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

public class FragmentMapa extends Fragment {

    private MapView mapa;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        // 1. Inicializar la configuración de osmdroid ANTES de inflar la vista
        Context ctx = requireActivity().getApplicationContext();
        Configuration.getInstance().load(ctx, ctx.getSharedPreferences("osmdroid", Context.MODE_PRIVATE));
        // Muy bien hecho añadir el UserAgent, evita que bloqueen las peticiones
        Configuration.getInstance().setUserAgentValue("AppAlmi2/1.0");

        return inflater.inflate(R.layout.fragment_mapa, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mapa = view.findViewById(R.id.mvMapa);

        if (mapa != null) {
            // 2. Fuente de mapas personalizada (Esri World Street Map)
            OnlineTileSourceBase esriCalles = new OnlineTileSourceBase(
                    "EsriCalles", 0, 19, 256, ".png",
                    new String[]{"https://server.arcgisonline.com/ArcGIS/rest/services/World_Street_Map/MapServer/tile/"},
                    "")
            {
                @Override
                public String getTileURLString(long pMapTileIndex) {
                    // Corrección del formato de URL obligatorio para Esri en osmdroid 6: Zoom/Y/X
                    return getBaseUrl()
                            + MapTileIndex.getZoom(pMapTileIndex) + "/"
                            + MapTileIndex.getY(pMapTileIndex) + "/"
                            + MapTileIndex.getX(pMapTileIndex);
                }
            };

            mapa.setTileSource(esriCalles);
            mapa.setMultiTouchControls(true);

            // 3. Ubicación y controles del mapa (Centro de Formación Almi)
            GeoPoint almiPoint = new GeoPoint(43.271461, -2.948372);

            IMapController mapController = mapa.getController();
            mapController.setZoom(18.0);
            mapController.setCenter(almiPoint);

            // 4. Configurar el marcador (Pin)
            Marker markerAlmi = new Marker(mapa);
            markerAlmi.setPosition(almiPoint);
            // El anclaje inferior asegura que la punta del pin apunte a la coordenada exacta
            markerAlmi.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            markerAlmi.setTitle("Centro Almi");
            markerAlmi.setSnippet("¡Bienvenidos!");

            // Añadir el marcador a las capas del mapa
            mapa.getOverlays().add(markerAlmi);

            // Refrescar el mapa de forma asíncrona para pintar los cambios
            mapa.postInvalidate();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // Carga la configuración del almacenamiento al volver al Fragment
        Context context = requireActivity().getApplicationContext();
        Configuration.getInstance().load(context, context.getSharedPreferences("osmdroid", Context.MODE_PRIVATE));
        if (mapa != null) {
            mapa.onResume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        // Guarda el estado del mapa en caché antes de pausar
        Context contexto = requireActivity().getApplicationContext();
        Configuration.getInstance().save(contexto, contexto.getSharedPreferences("osmdroid", Context.MODE_PRIVATE));
        if (mapa != null) {
            mapa.onPause();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Liberar la referencia del mapa para evitar fugas de memoria en Fragments
        mapa = null;
    }
}
