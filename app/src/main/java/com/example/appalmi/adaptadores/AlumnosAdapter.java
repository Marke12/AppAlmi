package com.example.appalmi.adaptadores;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.appalmi.R;
import com.example.appalmi.modelos.Alumno;

import java.util.List;

public class AlumnosAdapter extends RecyclerView.Adapter<AlumnosAdapter.AlumnoViewHolder> {

    private List<Alumno> listaAlumnos;
    private OnAlumnoClickListener clickListener;
    private OnAlumnoLongClickListener longListener;

    public interface OnAlumnoClickListener {
        void onAlumnoClick(Alumno alumno);
    }

    public interface OnAlumnoLongClickListener {
        void onAlumnoLongClick(Alumno alumno);
    }

    public AlumnosAdapter(List<Alumno> listaAlumnos, OnAlumnoClickListener clickListener, OnAlumnoLongClickListener longListener) {
        this.listaAlumnos = listaAlumnos;
        this.clickListener = clickListener;
        this.longListener = longListener;
    }

    @NonNull
    @Override
    public AlumnoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_alumno, parent, false);
        return new AlumnoViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AlumnoViewHolder holder, int position) {
        Alumno alumno = listaAlumnos.get(position);
        holder.tvNombre.setText(alumno.getNombre());
        holder.tvApellido.setText(alumno.getApellido());

        if (alumno.getRutaFoto() != null && !alumno.getRutaFoto().isEmpty()) {
            // USANDO GLIDE
            Glide.with(holder.itemView.getContext())
                    .load(alumno.getRutaFoto())
                    .centerCrop()
                    .into(holder.ivFoto);
        } else {
            holder.ivFoto.setImageResource(android.R.drawable.ic_menu_camera);
        }

        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) clickListener.onAlumnoClick(alumno);
        });

        holder.itemView.setOnLongClickListener(v -> {
            if (longListener != null) longListener.onAlumnoLongClick(alumno);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return listaAlumnos.size();
    }

    public static class AlumnoViewHolder extends RecyclerView.ViewHolder {
        ImageView ivFoto;
        TextView tvNombre, tvApellido;

        public AlumnoViewHolder(@NonNull View itemView) {
            super(itemView);
            ivFoto = itemView.findViewById(R.id.ivFotoAlumno);
            tvNombre = itemView.findViewById(R.id.tvNombreAlumno);
            tvApellido = itemView.findViewById(R.id.tvApellidoAlumno);
        }
    }
}