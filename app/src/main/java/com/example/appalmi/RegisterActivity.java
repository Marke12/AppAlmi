package com.example.appalmi;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.appalmi.adaptadores.UsuariosAdapter;
import com.example.appalmi.db.AppDatabase;
import com.example.appalmi.db.AppExecutors;
import com.example.appalmi.modelos.Usuario;

public class RegisterActivity extends AppCompatActivity {
    private ListView lvUsers;
    private Button btnRegistrarNuevo, btnUpdateUsuario, btnLimpiarBD;
    private EditText etNombre, etPassword, etRePassword;
    private ProgressBar pbBorrado;
    private int identificador = -1;
    private AppDatabase mDb;
    private UsuariosAdapter usuariosAdapter;

    // --- VARIABLES SENSOR (EXAMEN) ---
    private SensorManager sensorManager;
    private Sensor proximitySensor;
    private SensorEventListener proximityListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mDb = AppDatabase.getInstance(getApplicationContext());

        lvUsers = findViewById(R.id.lvUsers);
        // Usar Contexto de la Actividad (this) en lugar de getApplicationContext() para evitar crasheados de tema/inflador
        usuariosAdapter = new UsuariosAdapter(RegisterActivity.this, android.R.layout.simple_list_item_1);
        lvUsers.setAdapter(usuariosAdapter);

        etNombre = findViewById(R.id.etRegistroUser);
        etPassword = findViewById(R.id.etRegistroPassword);
        etRePassword = findViewById(R.id.etRegistroRePassword);
        btnRegistrarNuevo = findViewById(R.id.btnNuevoUsuario);
        btnUpdateUsuario = findViewById(R.id.btnUpdateUsuario);


        
        // Elementos del examen de Animaciones y Async
        btnLimpiarBD = findViewById(R.id.btnLimpiarBD);
        pbBorrado = findViewById(R.id.pbBorrado);

        btnRegistrarNuevo.setOnClickListener(v -> guardar(etNombre.getText().toString(), etPassword.getText().toString()));

        lvUsers.setOnItemClickListener((parent, view, position, id) -> {
            Usuario usuario = usuariosAdapter.getItem(position);
            if (usuario != null) {
                identificador = usuario.getId();
                etNombre.setText(usuario.getUsuario());
                etPassword.setText(usuario.getPassword());
                etRePassword.setText(usuario.getPassword());
            }
        });

        lvUsers.setOnItemLongClickListener((parent, view, position, id) -> {
            Usuario usuario = usuariosAdapter.getItem(position);
            if (usuario != null) {
                final int idEliminar = usuario.getId();
                new AlertDialog.Builder(RegisterActivity.this)
                        .setTitle("Advertencia")
                        .setMessage("¿Estás seguro de que quieres eliminar el usuario?")
                        .setPositiveButton("Sí", (dialog, which) -> eliminar(idEliminar))
                        .setNegativeButton("No", null)
                        .show();
            }
            return true;
        });

        btnUpdateUsuario.setOnClickListener(v -> actualizar(identificador, etNombre.getText().toString(), etPassword.getText().toString()));
        
        // EXAMEN: Botón limpiar con Animación de Propiedad y Simulación AsyncTask
        btnLimpiarBD.setOnClickListener(v -> animarYBorrar());

        etRePassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }
            @Override
            public void afterTextChanged(Editable s) {
                if (!etPassword.getText().toString().equals(etRePassword.getText().toString())) {
                    btnRegistrarNuevo.setEnabled(false);
                    etRePassword.setBackgroundColor(Color.RED);
                } else {
                    btnRegistrarNuevo.setEnabled(true);
                    etRePassword.setBackgroundColor(Color.WHITE);
                }
            }
        });

        // -------------------------------------------------------------
        // EXAMEN (Manual "Sensores")
        // -------------------------------------------------------------
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        if (sensorManager != null) {
            proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        }

        if (proximitySensor == null) {
            Log.e("EXAMEN_SENSORES", "No existe el sensor de proximidad en este móvil");
        }

        proximityListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                if (proximitySensor != null && event.values[0] < proximitySensor.getMaximumRange()) {
                    Log.d("EXAMEN_SENSORES", "¡Alguien está mirando! Borrando campos por seguridad.");
                    etPassword.setText("");
                    etRePassword.setText("");
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) { }
        };

        consultar();
    }

    // --- MÉTODOS DE EXAMEN (Animaciones y Tareas Asíncronas) ---
    private void animarYBorrar() {
        PropertyValuesHolder pvhX = PropertyValuesHolder.ofFloat(View.TRANSLATION_X, 0f, 20f, -20f, 20f, -20f, 0f);
        PropertyValuesHolder pvhAlpha = PropertyValuesHolder.ofFloat(View.ALPHA, 1f, 0.5f, 1f);
        
        ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(btnLimpiarBD, pvhX, pvhAlpha);
        animator.setDuration(500); 
        animator.start();

        pbBorrado.setVisibility(View.VISIBLE);
        pbBorrado.setProgress(0);
        btnLimpiarBD.setEnabled(false);

        AppExecutors.getInstance().getDiskIO().execute(() -> {
            try {
                for (int i = 1; i <= 10; i++) {
                    Thread.sleep(200); 
                    final int progreso = i * 10;
                    
                    AppExecutors.getInstance().getMainThread().execute(() -> {
                        pbBorrado.setProgress(progreso);
                    });
                }
                
                mDb.usuarioDao().deleteAll();
                
                AppExecutors.getInstance().getMainThread().execute(() -> {
                    pbBorrado.setVisibility(View.GONE);
                    btnLimpiarBD.setEnabled(true);
                    Toast.makeText(RegisterActivity.this, "Base de datos limpia", Toast.LENGTH_SHORT).show();
                    mDb.usuarioDao().insertar(new Usuario("Almi", "Almi123"));
                });
                
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    // --- METODOS DE BD ORIGINALES ---

    private void guardar(String user, String password) {
        final Usuario usuario = new Usuario(user, password);
        AppExecutors.getInstance().getDiskIO().execute(() -> mDb.usuarioDao().insertUsuario(usuario));
    }

    private void actualizar(final int id, final String user, final String password) {
        AppExecutors.getInstance().getDiskIO().execute(() -> {
            Usuario usu = mDb.usuarioDao().loadUsuarioById(id);
            if (usu != null) {
                usu.setUsuario(user);
                usu.setPassword(password);
                mDb.usuarioDao().updateUsuario(usu);
            }
        });
    }

    private void eliminar(final int id) {
        AppExecutors.getInstance().getDiskIO().execute(() -> {
            Usuario usu = mDb.usuarioDao().loadUsuarioById(id);
            if (usu != null) mDb.usuarioDao().delete(usu);
        });
    }

    private void consultar() {
        mDb.usuarioDao().loadAllUsuarios().observe(this, usuarios -> {
            usuariosAdapter.setUsuarios(usuarios);
            etNombre.setText("");
            etPassword.setText("");
            etRePassword.setText("");
            identificador = -1;
        });
    }

    // --- CICLO DE VIDA DEL SENSOR ---
    @Override
    protected void onResume() {
        super.onResume();
        if (sensorManager != null && proximitySensor != null) {
            sensorManager.registerListener(proximityListener, proximitySensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (sensorManager != null && proximitySensor != null) {
            sensorManager.unregisterListener(proximityListener);
        }
    }
}