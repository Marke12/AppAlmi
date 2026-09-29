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
import com.example.appalmi.modelos.Alumno;
import com.example.appalmi.db.AppDatabase;
import com.example.appalmi.db.AppExecutors;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DialogAddAlumnoFrag extends DialogFragment {

    private EditText etNombre, etApellido;
    private ImageView ivPreview;
    private Button btnSacarFoto, btnGuardar, btnCancelar;
    
    private File fotoActual;
    private ActivityResultLauncher<Uri> tomarFotoLauncher;
    private AppDatabase mDb;
    private String cursoNombre;

    public DialogAddAlumnoFrag(String cursoNombre) {
        this.cursoNombre = cursoNombre;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        tomarFotoLauncher = registerForActivityResult(
                new ActivityResultContracts.TakePicture(),
                exito -> {
                    if (Boolean.TRUE.equals(exito) && fotoActual != null) {
                        // USANDO GLIDE
                        com.bumptech.glide.Glide.with(requireContext())
                                .load(Uri.fromFile(fotoActual))
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
        View vista = inflater.inflate(R.layout.dialog_add_alumno, container, false);
        etNombre = vista.findViewById(R.id.etNombreAlumno);
        etApellido = vista.findViewById(R.id.etApellidoAlumno);
        ivPreview = vista.findViewById(R.id.ivPreviewFotoAlumno);
        btnSacarFoto = vista.findViewById(R.id.btnSacarFotoAlumno);
        btnGuardar = vista.findViewById(R.id.btnGuardarAlumno);
        btnCancelar = vista.findViewById(R.id.btnCancelarAlumno);
        return vista;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mDb = AppDatabase.getInstance(requireContext());

        btnCancelar.setOnClickListener(v -> dismiss());
        btnSacarFoto.setOnClickListener(v -> lanzarCamara());

        btnGuardar.setOnClickListener(v -> {
            String nombre = etNombre.getText().toString().trim();
            String apellido = etApellido.getText().toString().trim();
            if (nombre.isEmpty() || apellido.isEmpty()) {
                Toast.makeText(requireContext(), "Rellena todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            String ruta = fotoActual != null ? fotoActual.getAbsolutePath() : "";

            AppExecutors.getInstance().getDiskIO().execute(() -> {
                Alumno nuevoAlumno = new Alumno(cursoNombre, nombre, apellido, ruta);
                mDb.alumnoDao().insertar(nuevoAlumno);

                AppExecutors.getInstance().getMainThread().execute(() -> {
                    Toast.makeText(requireContext(), "Alumno guardado", Toast.LENGTH_SHORT).show();
                    dismiss();
                });
            });
        });
    }

    private void lanzarCamara() {
        File carpeta = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        String nombre = "alumno_" + new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date()) + ".jpg";
        fotoActual = new File(carpeta, nombre);

        Uri uri = FileProvider.getUriForFile(requireContext(), requireContext().getPackageName() + ".fileprovider", fotoActual);
        tomarFotoLauncher.launch(uri);
    }
}