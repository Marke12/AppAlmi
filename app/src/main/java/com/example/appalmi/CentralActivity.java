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

public class CentralActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_central);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawerLayout);
        NavigationView navigationView = findViewById(R.id.navigationView);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.abrir_menu, R.string.cerrar_menu);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        if (savedInstanceState == null) {
            cargarFragmento(new FragmentInicio());
            navigationView.setCheckedItem(R.id.menuInicio);
        }

        navigationView.setNavigationItemSelectedListener(this::onMenuItemClick);
    }

    private boolean onMenuItemClick(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menuInicio) cargarFragmento(new FragmentInicio());
        else if (id == R.id.menuMapa) cargarFragmento(new FragmentMapa());
        else if (id == R.id.menuGaleria) cargarFragmento(new FragmentGaleria());
        else if (id == R.id.menuSensores) cargarFragmento(new com.example.appalmi.fragmentos.FragmentSensores());

        item.setChecked(true);
        drawerLayout.closeDrawers();
        return true;
    }

    private void cargarFragmento(Fragment fragmento) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
        transaction.replace(R.id.contenedorFragmentos, fragmento);
        transaction.commit();
    }
}