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


         //CARGAR FOTOS DE INTERNET
         List<Foto> fotos = new ArrayList<>();

         fotos.add(new Foto("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQ6oH3mEKl9xkM4sj0xj_mNKzjDurWj60AXahw-egYWcw&s=10", "Secretaria"));
         fotos.add(new Foto("https://almi.eus/wp-content/uploads/2016/09/06-Aula-Ordenadores-1024x576.jpg", "Aulas"));
         fotos.add(new Foto("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcR-HBM3mGx8GRabGIhAgYjqrYubz4GMiTlN-N8oHwEHjg&s=10", "Profesor GOAT"));
         fotos.add(new Foto("https://almi.eus/wp-content/uploads/2016/09/11Trabajos-en-grupo-1024x576.jpg", "Alumnos en clase"));
         //CARGAR FOTOS LOCALES
         fotos.add(new Foto(R.drawable.almi_logo, "Logo Almi"));
         FotosAdapter adapter = new FotosAdapter(fotos);
         rvFotos.setAdapter(adapter);
    }
}
