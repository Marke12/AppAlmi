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


         rvFotos.setLayoutManager(new GridLayoutManager(requireContext(), 3));
        List<Foto> fotos = new ArrayList<>();

         fotos.add(new Foto("https://images.unsplash.com/photo-1541339907198-e08756dedf3f?w=600", "Entrada principal"));
         fotos.add(new Foto("https://images.unsplash.com/photo-1524178232363-1fb2b075b655?w=600", "Aulas"));
         fotos.add(new Foto("https://images.unsplash.com/photo-1531482615713-2afd69097998?w=600", "Profesores"));
         
         fotos.add(new Foto(R.drawable.almi_logo, "Logo Almi"));

         FotosAdapter adapter = new FotosAdapter(fotos);
         rvFotos.setAdapter(adapter);
    }
}
