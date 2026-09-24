package com.example.appalmi.adaptadores;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.appalmi.fragmentos.FragmentExterior;
import com.example.appalmi.fragmentos.FragmentInterior;

public class GaleriaPagerAdapter extends FragmentStateAdapter {

    public GaleriaPagerAdapter(@NonNull Fragment fragmentoAnfitrion) {
        super(fragmentoAnfitrion);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) {
            return new FragmentInterior();
        } else {
            return new FragmentExterior();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
