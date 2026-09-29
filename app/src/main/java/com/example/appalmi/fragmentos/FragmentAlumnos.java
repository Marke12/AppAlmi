package com.example.appalmi.fragmentos;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appalmi.R;
import com.example.appalmi.adaptadores.AlumnosAdapter;
import com.example.appalmi.db.AppDatabase;
import com.example.appalmi.db.AppExecutors;

import java.util.ArrayList;

public class FragmentAlumnos extends Fragment {

    private String cursoNombre;
    private AppDatabase mDb;
    private RecyclerView rvAlumnos;
    private AlumnosAdapter adapter;

    public static FragmentAlumnos newInstance(String cursoNombre) {
        FragmentAlumnos fragment = new FragmentAlumnos();
        Bundle args = new Bundle();
        args.putString("cursoNombre", cursoNombre);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            cursoNombre = getArguments().getString("cursoNombre");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_alumnos, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mDb = AppDatabase.getInstance(requireContext());

        TextView tvTitulo = view.findViewById(R.id.tvTituloAlumnos);
        tvTitulo.setText("Alumnos de: " + cursoNombre);

        rvAlumnos = view.findViewById(R.id.rvAlumnos);
        rvAlumnos.setLayoutManager(new LinearLayoutManager(requireContext()));
        
        adapter = new AlumnosAdapter(new ArrayList<>(), 
            alumno -> { /* Click normal dummy */ }, 
            alumno -> {
                // CLIC LARGO DUMMY
                new AlertDialog.Builder(requireContext())
                        .setTitle("Borrar alumno")
                        .setMessage("¿Estás seguro de que quieres borrar a " + alumno.getNombre() + "?")
                        .setPositiveButton("Sí", (dialog, which) -> {
                            AppExecutors.getInstance().getDiskIO().execute(() -> {
                                mDb.alumnoDao().borrar(alumno);
                                AppExecutors.getInstance().getMainThread().execute(() -> 
                                    Toast.makeText(requireContext(), "Alumno borrado", Toast.LENGTH_SHORT).show()
                                );
                            });
                        })
                        .setNegativeButton("No", null)
                        .show();
        });
        rvAlumnos.setAdapter(adapter);

        // Cargar alumnos dinámicamente
        mDb.alumnoDao().obtenerAlumnosPorCurso(cursoNombre).observe(getViewLifecycleOwner(), alumnosDB -> {
            adapter = new AlumnosAdapter(alumnosDB, alumno -> {
                // CLIC CORTO -> EDITAR (Abre el nuevo Diálogo de Edición)
                DialogEditAlumnoFrag dialog = new DialogEditAlumnoFrag(alumno);
                dialog.show(getChildFragmentManager(), "EditAlumno");
                
            }, alumno -> {
                // CLIC LARGO -> BORRAR (Existente)
                new AlertDialog.Builder(requireContext())
                        .setTitle("Borrar alumno")
                        .setMessage("¿Estás seguro de que quieres borrar a " + alumno.getNombre() + "?")
                        .setPositiveButton("Sí", (dialog, which) -> {
                            AppExecutors.getInstance().getDiskIO().execute(() -> {
                                mDb.alumnoDao().borrar(alumno);
                                AppExecutors.getInstance().getMainThread().execute(() -> 
                                    Toast.makeText(requireContext(), "Alumno borrado", Toast.LENGTH_SHORT).show()
                                );
                            });
                        })
                        .setNegativeButton("No", null)
                        .show();
            });
            rvAlumnos.setAdapter(adapter);
        });

        Button btnAdd = view.findViewById(R.id.btnAddAlumno);
        btnAdd.setOnClickListener(v -> {
            DialogAddAlumnoFrag dialog = new DialogAddAlumnoFrag(cursoNombre);
            dialog.show(getChildFragmentManager(), "AddAlumno");
        });
    }
}