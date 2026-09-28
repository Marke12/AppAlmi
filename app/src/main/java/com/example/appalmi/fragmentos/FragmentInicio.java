package com.example.appalmi.fragmentos;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.appalmi.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FragmentInicio extends Fragment {

    private List<String> listaCursos;
    private ArrayAdapter<String> adapter;
    
    private long ultimoTiempoClick = 0;
    private int ultimaPosicionClick = -1;
    private static final long TIEMPO_DOBLE_CLICK = 300; 

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                              @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_inicio, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ListView lvCursos = view.findViewById(R.id.lvCursos);
        EditText etNuevoCurso = view.findViewById(R.id.etNuevoCurso);
        Button btnAddCurso = view.findViewById(R.id.btnAddCurso);

        String[] cursosIniciales = getResources().getStringArray(R.array.cursos);
        listaCursos = new ArrayList<>(Arrays.asList(cursosIniciales));

        adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, listaCursos);
        lvCursos.setAdapter(adapter);

        btnAddCurso.setOnClickListener(v -> {
            String nuevoCurso = etNuevoCurso.getText().toString().trim();
            if (!nuevoCurso.isEmpty()) {
                listaCursos.add(nuevoCurso);
                adapter.notifyDataSetChanged();
                etNuevoCurso.setText("");
                lvCursos.smoothScrollToPosition(listaCursos.size() - 1);
            } else {
                Toast.makeText(requireContext(), "Escribe un curso primero", Toast.LENGTH_SHORT).show();
            }
        });

        lvCursos.setOnItemClickListener((parent, view1, position, id) -> {
            long tiempoActual = System.currentTimeMillis();
            if (ultimaPosicionClick == position && (tiempoActual - ultimoTiempoClick) < TIEMPO_DOBLE_CLICK) {
                String cursoSeleccionado = listaCursos.get(position);
                new android.app.AlertDialog.Builder(requireContext())
                        .setTitle("Borrar curso")
                        .setMessage("¿Estás seguro de que quieres borrar el curso '" + cursoSeleccionado + "'?")
                        .setPositiveButton("Sí, borrar", (dialog, which) -> {
                            listaCursos.remove(position);
                            adapter.notifyDataSetChanged();
                            Toast.makeText(requireContext(), "Curso borrado", Toast.LENGTH_SHORT).show();
                        })
                        .setNegativeButton("Cancelar", null)
                        .show();
                ultimoTiempoClick = 0;
                ultimaPosicionClick = -1;
            } else {
                ultimoTiempoClick = tiempoActual;
                ultimaPosicionClick = position;
            }
        });
    }
}
