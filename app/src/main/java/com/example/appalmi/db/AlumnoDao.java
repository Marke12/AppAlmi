package com.example.appalmi.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.appalmi.modelos.Alumno;

import java.util.List;

@Dao
public interface AlumnoDao {
    @Insert
    void insertar(Alumno alumno);

    @androidx.room.Update
    void actualizar(Alumno alumno);

    @Delete
    void borrar(Alumno alumno);

    @Query("SELECT * FROM alumnos WHERE cursoNombre = :cursoNombre")
    LiveData<List<Alumno>> obtenerAlumnosPorCurso(String cursoNombre);
}