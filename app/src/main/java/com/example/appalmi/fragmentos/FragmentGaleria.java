package com.example.appalmi.fragmentos;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.appalmi.R;
import com.example.appalmi.adaptadores.GaleriaPagerAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class FragmentGaleria extends Fragment {

    private TabLayout tabLayout;
    private ViewPager2 viewPager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                              @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_galeria, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tabLayout = view.findViewById(R.id.tabLayout);
        viewPager = view.findViewById(R.id.viewPager);

        // El adapter decide qué fragmento se muestra en cada pestaña (0 = Interior, 1 = Exterior)
        GaleriaPagerAdapter adapter = new GaleriaPagerAdapter(this);
        viewPager.setAdapter(adapter);

        String[] titulos = {"Interior", "Exterior"};

        // Conecta las pestañas del TabLayout con las páginas del ViewPager2
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) ->
                tab.setText(titulos[position])
        ).attach();
    }
}
