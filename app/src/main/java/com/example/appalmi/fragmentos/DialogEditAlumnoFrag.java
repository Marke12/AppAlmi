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
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.DialogFragment;

import com.bumptech.glide.Glide;
import com.example.appalmi.R;
import com.example.appalmi.db.AppDatabase;
import com.example.appalmi.db.AppExecutors;
import com.example.appalmi.modelos.Alumno;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DialogEditAlumnoFrag extends DialogFragment {

    private TextView tvTitulo;
    private EditText etNombre, etApellido;
    private ImageView ivPreview;
    private Button btnSacarFoto, btnActualizar, btnCancelar;
    
    private File fotoActual;
    private ActivityResultLauncher<Uri> tomarFotoLauncher;
    private AppDatabase mDb;
    private Alumno alumnoActual;

    public DialogEditAlumnoFrag(Alumno alumno) {
        this.alumnoActual = alumno;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        tomarFotoLauncher = registerForActivityResult(
                new ActivityResultContracts.TakePicture(),
                exito -> {
                    if (Boolean.TRUE.equals(exito) && fotoActual != null) {
                        Glide.with(requireContext())
                                .load(fotoActual)
                                .centerCrop()
                                .into(ivPreview);
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
        // Reutilizamos el mismo layout de añadir pero le cambiamos los textos en código
        View vista = inflater.inflate(R.layout.dialog_add_alumno, container, false);
        tvTitulo = vista.findViewById(R.id.tvTituloDialogAlumno);
        etNombre = vista.findViewById(R.id.etNombreAlumno);
        etApellido = vista.findViewById(R.id.etApellidoAlumno);
        ivPreview = vista.findViewById(R.id.ivPreviewFotoAlumno);
        btnSacarFoto = vista.findViewById(R.id.btnSacarFotoAlumno);
        btnActualizar = vista.findViewById(R.id.btnGuardarAlumno);
        btnCancelar = vista.findViewById(R.id.btnCancelarAlumno);
        return vista;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mDb = AppDatabase.getInstance(requireContext());

        // Adaptamos el diseño para "Editar"
        tvTitulo.setText("Editar Alumno");
        btnActualizar.setText("Actualizar");

        // Rellenamos con los datos del alumno que hemos pasado por el constructor
        etNombre.setText(alumnoActual.getNombre());
        etApellido.setText(alumnoActual.getApellido());
        
        if (alumnoActual.getRutaFoto() != null && !alumnoActual.getRutaFoto().isEmpty()) {
            Glide.with(requireContext())
                    .load(Uri.parse(alumnoActual.getRutaFoto()))
                    .centerCrop()
                    .into(ivPreview);
        }

        btnCancelar.setOnClickListener(v -> dismiss());
        btnSacarFoto.setOnClickListener(v -> lanzarCamara());

        btnActualizar.setOnClickListener(v -> {
            String nombre = etNombre.getText().toString().trim();
            String apellido = etApellido.getText().toString().trim();
            
            if (nombre.isEmpty() || apellido.isEmpty()) {
                Toast.makeText(requireContext(), "Rellena todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            // Si hemos hecho una foto nueva cogemos la nueva, sino dejamos la que tenía
            if (fotoActual != null) {
                alumnoActual.setRutaFoto(fotoActual.getAbsolutePath());
            }
            alumnoActual.setNombre(nombre);
            alumnoActual.setApellido(apellido);

            AppExecutors.getInstance().getDiskIO().execute(() -> {
                mDb.alumnoDao().actualizar(alumnoActual);

                AppExecutors.getInstance().getMainThread().execute(() -> {
                    Toast.makeText(requireContext(), "Alumno actualizado", Toast.LENGTH_SHORT).show();
                    dismiss();
                });
            });
        });
    }

    private void lanzarCamara() {
        File carpeta = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        String nombre = "alumno_edit_" + new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date()) + ".jpg";
        fotoActual = new File(carpeta, nombre);

        Uri uri = FileProvider.getUriForFile(requireContext(), requireContext().getPackageName() + ".fileprovider", fotoActual);
        tomarFotoLauncher.launch(uri);
    }
}