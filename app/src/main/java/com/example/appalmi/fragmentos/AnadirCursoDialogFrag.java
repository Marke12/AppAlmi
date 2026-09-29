package com.example.appalmi.fragmentos;

import android.app.Dialog;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.DialogFragment;

import com.example.appalmi.R;
import com.example.appalmi.db.AppDatabase;
import com.example.appalmi.db.AppExecutors;
import com.example.appalmi.modelos.Curso;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AnadirCursoDialogFrag extends DialogFragment {

    private EditText etNombreCurso;
    private ImageView ivPreview;
    private Button btnSacarFoto, btnGuardar, btnCancelar;
    
    private File fotoActual;
    private ActivityResultLauncher<Uri> tomarFotoLauncher;
    private AppDatabase mDb;
    private OnCursoAnadidoListener listener;

    public interface OnCursoAnadidoListener {
        void onCursoAnadido(String nombreCurso);
    }

    public void setOnCursoAnadidoListener(OnCursoAnadidoListener listener) {
        this.listener = listener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Registramos el launcher para abrir la cámara de forma nativa
        tomarFotoLauncher = registerForActivityResult(
                new ActivityResultContracts.TakePicture(),
                exito -> {
                    if (Boolean.TRUE.equals(exito) && fotoActual != null) {
                        // Si hace la foto, la pintamos en el ImageView del propio Dialog
                        ivPreview.setImageURI(Uri.fromFile(fotoActual));
                    }
                });
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return super.onCreateDialog(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View vista = inflater.inflate(R.layout.dialog_add_curso_examen, container, false);
        etNombreCurso = vista.findViewById(R.id.etNombreCursoExamen);
        ivPreview = vista.findViewById(R.id.ivPreviewFotoCurso);
        btnSacarFoto = vista.findViewById(R.id.btnSacarFotoCurso);
        btnGuardar = vista.findViewById(R.id.btnGuardarCursoExamen);
        btnCancelar = vista.findViewById(R.id.btnCancelarCursoExamen);
        return vista;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mDb = AppDatabase.getInstance(requireContext());

        btnCancelar.setOnClickListener(v -> dismiss());

        btnSacarFoto.setOnClickListener(v -> lanzarCamara());

        btnGuardar.setOnClickListener(v -> {
            String nombre = etNombreCurso.getText().toString().trim();
            if (nombre.isEmpty()) {
                Toast.makeText(requireContext(), "Pon un nombre al curso", Toast.LENGTH_SHORT).show();
                return;
            }

            // Si ha sacado foto cogemos su ruta, si no, lo dejamos vacío
            String ruta = fotoActual != null ? fotoActual.getAbsolutePath() : "";

            // Guardamos en la base de datos en un hilo secundario
            AppExecutors.getInstance().getDiskIO().execute(() -> {
                Curso nuevoCurso = new Curso(nombre, ruta);
                mDb.cursoDao().insertar(nuevoCurso);

                // Volvemos al hilo principal para avisar, actualizar la lista y cerrar el diálogo
                AppExecutors.getInstance().getMainThread().execute(() -> {
                    Toast.makeText(requireContext(), "Curso guardado en BD", Toast.LENGTH_SHORT).show();
                    if(listener != null) {
                        listener.onCursoAnadido(nombre);
                    }
                    dismiss();
                });
            });
        });
    }

    private void lanzarCamara() {
        File carpeta = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        String nombre = "curso_" + new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date()) + ".jpg";
        fotoActual = new File(carpeta, nombre);

        // Usamos el FileProvider para que no crashee por seguridad en Android 10+
        Uri uri = FileProvider.getUriForFile(requireContext(), requireContext().getPackageName() + ".fileprovider", fotoActual);
        tomarFotoLauncher.launch(uri);
    }
}