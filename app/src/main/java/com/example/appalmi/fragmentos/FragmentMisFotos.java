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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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
import com.example.appalmi.db.AppDatabase;
import com.example.appalmi.db.AppExecutors;
import com.example.appalmi.modelos.FotoEntity;
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

public class FragmentMisFotos extends Fragment {

    private RecyclerView rvFotos;
    private FotosAdapter adapter;
    private AppDatabase mDb;

    // Variables para la cámara
    private File fotoActual;
    private ActivityResultLauncher<Uri> tomarFotoLauncher;
    private ActivityResultLauncher<String> permisoLauncher;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        tomarFotoLauncher = registerForActivityResult(
                new ActivityResultContracts.TakePicture(),
                exito -> {
                    if (Boolean.TRUE.equals(exito) && fotoActual != null) {
                        guardarEnGaleria(fotoActual);
                        mostrarDialogoGuardarFoto(fotoActual.getAbsolutePath());
                    }
                });

        permisoLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                concedido -> {
                    if (Boolean.TRUE.equals(concedido)) {
                        lanzarCamara();
                    } else {
                        Toast.makeText(requireContext(), "Sin permiso no se puede guardar", Toast.LENGTH_SHORT).show();
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

        mDb = AppDatabase.getInstance(requireContext());
        rvFotos = view.findViewById(R.id.rvFotos);
        rvFotos.setLayoutManager(new GridLayoutManager(requireContext(), 3));

        adapter = new FotosAdapter(new ArrayList<>());
        rvFotos.setAdapter(adapter);

        mDb.fotoDao().obtenerFotosPorUbicacion("misfotos").observe(getViewLifecycleOwner(), fotosDB -> {
            List<Foto> listaActualizada = new ArrayList<>();
            for (FotoEntity fe : fotosDB) {
                listaActualizada.add(new Foto(fe.getRuta(), fe.getTitulo()));
            }
            adapter = new FotosAdapter(listaActualizada);
            rvFotos.setAdapter(adapter);
        });

        // Este sí tiene los botones visibles para añadir cosas a su pestaña directamente
        Button btnCamara = view.findViewById(R.id.btnCamaraGaleria);
        if (btnCamara != null) btnCamara.setOnClickListener(v -> abrirCamara());

        Button btnUrl = view.findViewById(R.id.btnUrlGaleria);
        if (btnUrl != null) btnUrl.setOnClickListener(v -> abrirDialogoUrl());

        Button btnExamen = view.findViewById(R.id.btnExamenDialog);
        if (btnExamen != null) btnExamen.setVisibility(View.GONE);
    }

    private void mostrarDialogoGuardarFoto(String rutaFoto) {
        View vistaDialog = getLayoutInflater().inflate(R.layout.dialog_add_curso_examen, null);
        EditText etTitulo = vistaDialog.findViewById(R.id.etNombreCursoExamen);
        etTitulo.setHint("Título de Mi Foto");
        vistaDialog.findViewById(R.id.btnSacarFotoCurso).setVisibility(View.GONE);
        
        android.widget.ImageView ivPreview = vistaDialog.findViewById(R.id.ivPreviewFotoCurso);
        // USANDO GLIDE
        com.bumptech.glide.Glide.with(requireContext())
                .load(Uri.fromFile(new File(rutaFoto)))
                .centerCrop()
                .into(ivPreview);

        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setTitle("Guardar Foto")
                .setView(vistaDialog)
                .create();

        // Metemos el Spinner para poder guardarlo en otra parte si nos arrepentimos
        Spinner spUbicacion = new Spinner(requireContext());
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, new String[]{"Interior", "Exterior", "Mis Fotos"});
        spUbicacion.setAdapter(spinnerAdapter);
        spUbicacion.setSelection(2); // Por defecto "Mis Fotos"
        ((ViewGroup) vistaDialog).addView(spUbicacion, 1);

        vistaDialog.findViewById(R.id.btnGuardarCursoExamen).setOnClickListener(v -> {
            String titulo = etTitulo.getText().toString();
            String ubi = spUbicacion.getSelectedItem().toString().replace(" ", "").toLowerCase(); // "Mis Fotos" -> "misfotos"

            if (titulo.isEmpty()) {
                Toast.makeText(requireContext(), "Pon un título", Toast.LENGTH_SHORT).show();
                return;
            }

            AppExecutors.getInstance().getDiskIO().execute(() -> {
                mDb.fotoDao().insertar(new FotoEntity(Uri.fromFile(new File(rutaFoto)).toString(), titulo, ubi));
                AppExecutors.getInstance().getMainThread().execute(() -> {
                    Toast.makeText(requireContext(), "Guardada en " + spUbicacion.getSelectedItem().toString(), Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                });
            });
        });

        vistaDialog.findViewById(R.id.btnCancelarCursoExamen).setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

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
        String nombre = "foto_mis_" + new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date()) + ".jpg";
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

    private void abrirDialogoUrl() {
        View vistaDialog = getLayoutInflater().inflate(R.layout.dialog_add_curso_examen, null);
        EditText etUrl = vistaDialog.findViewById(R.id.etNombreCursoExamen);
        etUrl.setHint("Pega aquí el enlace (http...)");
        vistaDialog.findViewById(R.id.btnSacarFotoCurso).setVisibility(View.GONE);
        vistaDialog.findViewById(R.id.ivPreviewFotoCurso).setVisibility(View.GONE);
        
        Spinner spUbicacion = new Spinner(requireContext());
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, new String[]{"Interior", "Exterior", "Mis Fotos"});
        spUbicacion.setAdapter(spinnerAdapter);
        spUbicacion.setSelection(2); // "Mis Fotos"
        ((ViewGroup) vistaDialog).addView(spUbicacion, 1); 
        
        EditText etTitulo = new EditText(requireContext());
        etTitulo.setHint("Título de la foto");
        ((ViewGroup) vistaDialog).addView(etTitulo, 2);

        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setTitle("Añadir Foto Internet")
                .setView(vistaDialog)
                .create();

        vistaDialog.findViewById(R.id.btnGuardarCursoExamen).setOnClickListener(v -> {
            String url = etUrl.getText().toString();
            String titulo = etTitulo.getText().toString();
            String ubi = spUbicacion.getSelectedItem().toString().replace(" ", "").toLowerCase(); // "Mis Fotos" -> "misfotos"

            if (url.isEmpty() || titulo.isEmpty()) return;

            AppExecutors.getInstance().getDiskIO().execute(() -> {
                mDb.fotoDao().insertar(new FotoEntity(url, titulo, ubi));
                AppExecutors.getInstance().getMainThread().execute(dialog::dismiss);
            });
        });

        vistaDialog.findViewById(R.id.btnCancelarCursoExamen).setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }
}