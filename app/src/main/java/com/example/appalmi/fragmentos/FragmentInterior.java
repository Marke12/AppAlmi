package com.example.appalmi.fragmentos;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appalmi.R;
import com.example.appalmi.adaptadores.FotosAdapter;
import com.example.appalmi.modelos.Foto;

import java.util.ArrayList;
import java.util.List;

public class FragmentInterior extends Fragment {

    private RecyclerView rvFotos;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                              @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_grid_fotos, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvFotos = view.findViewById(R.id.rvFotos);

        // TODO 1: Fijar un GridLayoutManager con 3 columnas (como en el enunciado)
        // rvFotos.setLayoutManager(new GridLayoutManager(requireContext(), 3));

        // TODO 2: Rellenar la lista de fotos del INTERIOR de Almi.
        // Puedes usar el logo de momento (@drawable/logo_almi) y luego cambiarlo
        // por fotos reales que metas en res/drawable.
        List<Foto> fotos = new ArrayList<>();
        // Ejemplo (descomenta y añade tantas como necesites):
        // fotos.add(new Foto(R.drawable.logo_almi, "Texto 1"));
        // fotos.add(new Foto(R.drawable.logo_almi, "Texto 2"));
        // fotos.add(new Foto(R.drawable.logo_almi, "Texto 3"));

        // TODO 3: Crear el adaptador y asignarlo al RecyclerView
        // FotosAdapter adapter = new FotosAdapter(fotos);
        // rvFotos.setAdapter(adapter);
    }
}
