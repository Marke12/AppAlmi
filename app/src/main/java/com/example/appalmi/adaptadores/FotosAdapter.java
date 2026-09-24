package com.example.appalmi.adaptadores;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.appalmi.R;
import com.example.appalmi.modelos.Foto;

import java.util.List;

public class FotosAdapter extends RecyclerView.Adapter<FotosAdapter.FotoViewHolder> {

    private final List<Foto> fotos;

    public FotosAdapter(List<Foto> fotos) {
        this.fotos = fotos;
    }

    @NonNull
    @Override
    public FotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_foto, parent, false);
        return new FotoViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull FotoViewHolder holder, int position) {
        Foto foto = fotos.get(position);

        holder.tvTexto.setText(foto.getTexto());

        if (foto.getUrlImagen() != null) {
            // Carga desde Internet si hay URL
            Glide.with(holder.itemView.getContext())
                 .load(foto.getUrlImagen())
                 .into(holder.ivFoto);
        } else {
            // Carga local si no hay URL
            Glide.with(holder.itemView.getContext())
                 .load(foto.getImagenResId())
                 .into(holder.ivFoto);
        }

         holder.itemView.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 Toast.makeText(v.getContext(), foto.getTexto(), Toast.LENGTH_SHORT).show();
                 mostrarDialogFoto(v.getContext(), foto);
             }
         });
    }

    @Override
    public int getItemCount() {
        return fotos.size();
    }

    private void mostrarDialogFoto(Context contexto, Foto foto) {
        View vista = LayoutInflater.from(contexto).inflate(R.layout.dialog_foto, null);

     ImageView ivFotoDialog = vista.findViewById(R.id.ivFotoDialog);
        if (foto.getUrlImagen() != null) {
            Glide.with(contexto).load(foto.getUrlImagen()).into(ivFotoDialog);
        } else {
            Glide.with(contexto).load(foto.getImagenResId()).into(ivFotoDialog);
        }

        Dialog dialogo = new Dialog(contexto);
       dialogo.setContentView(vista);
         if (dialogo.getWindow() != null) {
             dialogo.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
         }
         dialogo.show();

         vista.setOnClickListener(v -> dialogo.dismiss());
     }



    static class FotoViewHolder extends RecyclerView.ViewHolder {
        ImageView ivFoto;
        TextView tvTexto;

        public FotoViewHolder(@NonNull View itemView) {
            super(itemView);
            ivFoto = itemView.findViewById(R.id.ivFoto);
            tvTexto = itemView.findViewById(R.id.tvTextoFoto);
        }
    }
}
