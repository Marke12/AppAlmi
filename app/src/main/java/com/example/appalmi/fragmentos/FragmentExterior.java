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
        
        fotos.add(new Foto("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRnWGJYj4Ob4HJJwTI2zsyL7x9cXq-5c0z1o1chlDhywA&s=10", "Fachada"));
        fotos.add(new Foto("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTd6VxATDIWk6si0xi92AEZ90ycofiTZnASEqZlYmM61Q&s=10", "Llegada metro"));
        fotos.add(new Foto("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcToVnOx59zExrrPIpn0CdXD-DmVMrucxrN-uh0VcQV6EA&s=10", "Salida metro "));

        FotosAdapter adapter = new FotosAdapter(fotos);
        rvFotos.setAdapter(adapter);
    }
}
