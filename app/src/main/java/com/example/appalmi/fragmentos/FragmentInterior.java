package com.example.appalmi.fragmentos;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appalmi.R;
import com.example.appalmi.adaptadores.FotosAdapter;
import com.example.appalmi.modelos.Foto;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class FragmentInterior extends Fragment {

    private RecyclerView rvFotos;
    private List<Foto> fotos;
    private FotosAdapter adapter;

    // Variables para la cámara
    private File fotoActual;
    private ActivityResultLauncher<Uri> tomarFotoLauncher;
    private ActivityResultLauncher<String> permisoLauncher;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 1. Lanzador de la Cámara
        tomarFotoLauncher = registerForActivityResult(
                new ActivityResultContracts.TakePicture(),
                exito -> {
                    if (Boolean.TRUE.equals(exito) && fotoActual != null) {
                        // Lo copiamos al MediaStore (Galería del teléfono)
                        guardarEnGaleria(fotoActual);

                        // Lo añadimos al principio de nuestro RecyclerView local para verlo al instante
                        Uri uriLocal = Uri.fromFile(fotoActual);
                        fotos.add(0, new Foto(uriLocal.toString(), "Foto Reciente"));
                        adapter.notifyItemInserted(0);
                        rvFotos.scrollToPosition(0);
                    }
                });

        // 2. Lanzador de Permisos (para Android 9 o inferior)
        permisoLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                concedido -> {
                    if (Boolean.TRUE.equals(concedido)) {
                        lanzarCamara();
                    } else {
                        Toast.makeText(requireContext(), "Sin permiso no se puede guardar en la galería", Toast.LENGTH_SHORT).show();
                    }
                });
    }

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

        // Inicializar Lista
        fotos = new ArrayList<>();
        
        // CARGAMOS TODAS LAS FOTOS DEL DISPOSITIVO CREADAS POR LA APP
        cargarFotosDesdeCarpeta(fotos);

        fotos.add(new Foto("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQ6oH3mEKl9xkM4sj0xj_mNKzjDurWj60AXahw-egYWcw&s=10", "Secretaria"));
        fotos.add(new Foto("https://almi.eus/wp-content/uploads/2016/09/06-Aula-Ordenadores-1024x576.jpg", "Aulas"));
        fotos.add(new Foto("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcR-HBM3mGx8GRabGIhAgYjqrYubz4GMiTlN-N8oHwEHjg&s=10", "Profesor GOAT"));
        fotos.add(new Foto("https://almi.eus/wp-content/uploads/2016/09/11Trabajos-en-grupo-1024x576.jpg", "Alumnos en clase"));
        fotos.add(new Foto(R.drawable.almi_logo, "Logo Almi"));

        adapter = new FotosAdapter(fotos);
        rvFotos.setAdapter(adapter);

        // CONFIGURAR LOS DOS BOTONES NUEVOS
        Button btnCamara = view.findViewById(R.id.btnCamaraGaleria);
        if (btnCamara != null) btnCamara.setOnClickListener(v -> abrirCamara());

        Button btnUrl = view.findViewById(R.id.btnUrlGaleria);
        if (btnUrl != null) btnUrl.setOnClickListener(v -> abrirDialogoUrl());
    }

    // --- MÉTODOS DE LA CÁMARA ---

    private void abrirCamara() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q &&
                ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permisoLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            return;
        }
        lanzarCamara();
    }

    private void lanzarCamara() {
        File carpeta = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        String nombre = "foto_" + new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date()) + ".jpg";
        fotoActual = new File(carpeta, nombre);

        Uri uri = FileProvider.getUriForFile(requireContext(), requireContext().getPackageName() + ".fileprovider", fotoActual);
        tomarFotoLauncher.launch(uri);
    }

    private void guardarEnGaleria(File archivo) {
        ContentValues valores = new ContentValues();
        valores.put(MediaStore.Images.Media.DISPLAY_NAME, archivo.getName());
        valores.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            valores.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/AppAlmi");
            valores.put(MediaStore.Images.Media.IS_PENDING, 1);
        }

        Uri destino = requireContext().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, valores);
        if (destino == null) return;

        try (InputStream in = new FileInputStream(archivo);
             OutputStream out = requireContext().getContentResolver().openOutputStream(destino)) {
            byte[] buffer = new byte[8192];
            int leidos;
            while ((leidos = in.read(buffer)) > 0) {
                out.write(buffer, 0, leidos);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContentValues v = new ContentValues();
            v.put(MediaStore.Images.Media.IS_PENDING, 0);
            requireContext().getContentResolver().update(destino, v, null, null);
        }
    }

    // --- MÉTODOS DE URL ---

    private void abrirDialogoUrl() {
        EditText inputUrl = new EditText(requireContext());
        inputUrl.setHint("Pega aquí el enlace a la imagen (http...)");

        new AlertDialog.Builder(requireContext())
                .setTitle("Añadir Foto desde Internet")
                .setView(inputUrl)
                .setPositiveButton("Añadir", (dialog, which) -> {
                    String urlStr = inputUrl.getText().toString();
                    if (!urlStr.isEmpty()) {
                        fotos.add(0, new Foto(urlStr, "Foto Internet"));
                        adapter.notifyItemInserted(0);
                        rvFotos.scrollToPosition(0);
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    // --- CARGA DE FOTOS GUARDADAS EN EL TELÉFONO ---

    private void cargarFotosDesdeCarpeta(List<Foto> lista) {
        File carpeta = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if (carpeta != null) {
            File[] archivos = carpeta.listFiles();
            if (archivos != null) {
                for (File archivo : archivos) {
                    if (archivo.getName().startsWith("foto_")) {
                        Uri uriLocal = Uri.fromFile(archivo);
                        lista.add(new Foto(uriLocal.toString(), "Foto Propia"));
                    }
                }
            }
        }
    }
}
