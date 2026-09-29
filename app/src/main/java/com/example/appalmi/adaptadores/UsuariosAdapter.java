package com.example.appalmi.adaptadores;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.appalmi.modelos.Usuario;

import java.util.ArrayList;
import java.util.List;

public class UsuariosAdapter extends ArrayAdapter<Usuario> {

    private final Context context;
    private List<Usuario> mUsuarioList;

    public UsuariosAdapter(@NonNull Context context, int resource) {
        super(context, resource > 0 ? resource : android.R.layout.simple_list_item_1);
        this.context = context;
        this.mUsuarioList = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return mUsuarioList != null ? mUsuarioList.size() : 0;
    }

    @Nullable
    @Override
    public Usuario getItem(int position) {
        if (mUsuarioList != null && position >= 0 && position < mUsuarioList.size()) {
            return mUsuarioList.get(position);
        }
        return null;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            view = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
        }

        TextView tvUsuario = view.findViewById(android.R.id.text1);
        Usuario usuario = getItem(position);
        if (usuario != null && usuario.getUsuario() != null) {
            tvUsuario.setText(usuario.getUsuario());
        } else {
            tvUsuario.setText("");
        }

        return view;
    }

    public void setUsuarios(List<Usuario> usuariosList) {
        if (usuariosList != null) {
            this.mUsuarioList = usuariosList;
        } else {
            this.mUsuarioList = new ArrayList<>();
        }
        notifyDataSetChanged();
    }

    public List<Usuario> getUsuarios() {
        return mUsuarioList;
    }
}