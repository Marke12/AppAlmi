package com.example.appalmi;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.appalmi.fragmentos.FragmentGaleria;
import com.example.appalmi.fragmentos.FragmentInicio;
import com.example.appalmi.fragmentos.FragmentMapa;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);

        // Icono "hamburguesa" que abre/cierra el drawer
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.abrir_menu, R.string.cerrar_menu);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Se carga el fragmento de Inicio solo la primera vez
        if (savedInstanceState == null) {
            cargarFragmento(new FragmentInicio());
            navigationView.setCheckedItem(R.id.menuInicio);
        }

        navigationView.setNavigationItemSelectedListener(this::onMenuItemClick);
    }

    private boolean onMenuItemClick(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menuInicio) {
            cargarFragmento(new FragmentInicio());
        } else if (id == R.id.menuMapa) {
            cargarFragmento(new FragmentMapa());
        } else if (id == R.id.menuGaleria) {
            cargarFragmento(new FragmentGaleria());
        }

        item.setChecked(true);
        drawerLayout.closeDrawers();
        return true;
    }

    private void cargarFragmento(Fragment fragmento) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        // Animación al cambiar de fragmento (pedido en el enunciado)
        transaction.setCustomAnimations(
                android.R.anim.fade_in,
                android.R.anim.fade_out
        );

        transaction.replace(R.id.contenedorFragmentos, fragmento);
        transaction.commit();
    }
}
