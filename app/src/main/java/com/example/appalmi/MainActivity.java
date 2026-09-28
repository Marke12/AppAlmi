package com.example.appalmi;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnAcceder = findViewById(R.id.btnAcceder);
        btnAcceder.setOnClickListener(v -> {
            LoginDialogFrag dialog = new LoginDialogFrag();
            dialog.show(getSupportFragmentManager(), "login");
        });

        Button btnRegistrar = findViewById(R.id.btnRegistro);
        btnRegistrar.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), RegisterActivity.class)));
    }
}