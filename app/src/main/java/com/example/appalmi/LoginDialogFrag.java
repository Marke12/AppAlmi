package com.example.appalmi;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import com.example.appalmi.db.AppDatabase;
import com.example.appalmi.db.AppExecutors;
import com.example.appalmi.db.Usuario;

public class LoginDialogFrag extends DialogFragment {

    private EditText etNombre, etPassword;
    private Button btnAceptar, btnCancelar;
    private AppDatabase mDb;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setTitle("Login");
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View vista = inflater.inflate(R.layout.dialog_personalizado, container, false);
        etNombre = vista.findViewById(R.id.etUser);
        etPassword = vista.findViewById(R.id.etPassword);
        btnAceptar = vista.findViewById(R.id.btnAceptar);
        btnCancelar = vista.findViewById(R.id.btnCancelar);
        return vista;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mDb = AppDatabase.getInstance(getContext());

        btnCancelar.setOnClickListener(v -> dismiss());

        btnAceptar.setOnClickListener(v -> {
            final String nombre = etNombre.getText().toString();
            final String password = etPassword.getText().toString();

            AppExecutors.getInstance().getDiskIO().execute(() -> {
                final Usuario usu = mDb.usuarioDao().login(nombre, password);

                AppExecutors.getInstance().getMainThread().execute(() -> {
                    if (usu != null) {
                        Toast.makeText(getContext(), "Bienvenido", Toast.LENGTH_SHORT).show();
                        
                        // -------------------------------------------------------------
                        // EXAMEN (Manual "Toast Personalizado") - ACTUALMENTE COMENTADO
                        // Descomenta este bloque (y borra el Toast de arriba y el Intent de abajo) 
                        // si te piden usar el Toast personalizado en el examen.
                        /*
                        View toastVista = getLayoutInflater().inflate(R.layout.toast_per, null);
                        android.widget.TextView textoToast = toastVista.findViewById(R.id.tvToast);
                        textoToast.setText("¡Bienvenido, " + usu.getUsuario() + "!");

                        final Dialog dialogoToast = new Dialog(requireContext());
                        dialogoToast.setContentView(toastVista);
                        if (dialogoToast.getWindow() != null) {
                            dialogoToast.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                        }
                        dialogoToast.show();

                        new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> {
                            dialogoToast.dismiss();
                            Intent intent = new Intent(getContext(), CentralActivity.class);
                            startActivity(intent);
                            if(getActivity() != null) getActivity().finish();
                        }, 2000);
                        */
                        // -------------------------------------------------------------

                        Intent intent = new Intent(getContext(), CentralActivity.class);
                        startActivity(intent);
                        if(getActivity() != null) getActivity().finish();
                    } else {
                        Toast.makeText(getContext(), "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show();
                        dismiss();
                    }
                });
            });
        });
    }
}