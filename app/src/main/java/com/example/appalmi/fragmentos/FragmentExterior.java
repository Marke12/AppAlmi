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

public class FragmentExterior extends Fragment {

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

        rvFotos.setLayoutManager(new GridLayoutManager(requireContext(), 3));

        List<Foto> fotos = new ArrayList<>();
        
        fotos.add(new Foto("https://images.unsplash.com/photo-1523050854058-8df90110c9f1?w=600", "Fachada"));
        fotos.add(new Foto("https://images.unsplash.com/photo-1562774053-701939374585?w=600", "Campus Exterior"));
        fotos.add(new Foto("https://images.unsplash.com/photo-1541829070764-84a5004ca03a?w=600", "Patio"));

        FotosAdapter adapter = new FotosAdapter(fotos);
        rvFotos.setAdapter(adapter);
    }
}
