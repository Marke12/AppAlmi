package com.example.appalmi.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.appalmi.modelos.Curso;

import java.util.List;

@Dao
public interface CursoDao {
    @Insert
    void insertar(Curso curso);

    @Query("SELECT * FROM cursos")
    LiveData<List<Curso>> obtenerTodos();
}