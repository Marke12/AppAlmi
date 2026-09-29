# Simulación de Examen: DialogFragment + Cámara + Room

Este documento explica el ejercicio adicional que se ha programado para preparar el examen. Combina tres manuales en un solo flujo: crear un cuadro de diálogo (DialogFragment) que captura una fotografía real con la cámara del dispositivo y guarda el registro permanentemente en una base de datos local (Room).

---

## 1. La Arquitectura Creada

Hemos dividido este ejercicio en tres piezas de código que se comunican entre sí. Al igual que en el resto del proyecto, todo el código extra de este documento se encuentra **comentado** en el código fuente para que no afecte a la puntuación del proyecto inicial. 

### A. La Base de Datos (Modelo `Curso`)
Igual que teníamos una tabla de `Usuario`, hemos creado una nueva en la carpeta `db`:
*   `Curso.java`: Es la `@Entity` (La tabla). Contiene un `id`, un `nombre` y la ruta de la `rutaFoto` donde la cámara ha guardado la imagen.
*   `CursoDao.java`: Es el `@Dao` (Las consultas). Tiene un método `@Insert` para guardar.
*   En `AppDatabase.java`, hemos añadido `Curso.class` a la lista de entidades y subido la versión a 2.

### B. El Layout del Diálogo
Hemos creado el diseño XML `res/layout/dialog_add_curso_examen.xml`. Contiene:
*   Un `EditText` para introducir el nombre del curso.
*   Un `Button` para abrir la cámara nativa.
*   Un `ImageView` (`ivPreviewFotoCurso`) que mostrará la foto recién sacada en miniatura.
*   Botones de "Guardar" y "Cancelar".

### C. La Lógica (`AnadirCursoDialogFrag.java`)
Es una clase que hereda de `DialogFragment`. Aquí se mezclan los conceptos de la Cámara y de los Hilos Asíncronos.

```java
// 1. Registro del Launcher de la Cámara (Copia de FragmentInterior)
tomarFotoLauncher = registerForActivityResult(
        new ActivityResultContracts.TakePicture(),
        exito -> {
            if (Boolean.TRUE.equals(exito) && fotoActual != null) {
                ivPreview.setImageURI(Uri.fromFile(fotoActual));
            }
        });

// 2. Preparar el Uri Seguro y Lanzar (Requiere FileProvider en AndroidManifest)
private void lanzarCamara() {
    File carpeta = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
    String nombre = "curso_" + new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date()) + ".jpg";
    fotoActual = new File(carpeta, nombre);

    Uri uri = FileProvider.getUriForFile(requireContext(), requireContext().getPackageName() + ".fileprovider", fotoActual);
    tomarFotoLauncher.launch(uri);
}

// 3. Botón de Guardar en la Base de Datos
btnGuardar.setOnClickListener(v -> {
    String nombre = etNombreCurso.getText().toString();
    String ruta = fotoActual != null ? fotoActual.getAbsolutePath() : "";

    // Insertar en BD SIEMPRE en hilo secundario (getDiskIO)
    AppExecutors.getInstance().getDiskIO().execute(() -> {
        Curso nuevoCurso = new Curso(nombre, ruta);
        mDb.cursoDao().insertar(nuevoCurso);

        // Volver al hilo principal para avisar a la UI y cerrar el diálogo
        AppExecutors.getInstance().getMainThread().execute(() -> {
            Toast.makeText(requireContext(), "Guardado!", Toast.LENGTH_SHORT).show();
            dismiss();
        });
    });
});
```

---

## 2. Dónde Implementarlo en el Examen

Si en el examen te piden que este sea el método oficial para añadir cursos, solo tienes que ir a tu código y descomentar su llamada.

Actualmente, he añadido un botón de prueba en la pantalla principal llamado `btnExamenDialog`.

**1. En `res/layout/fragment_inicio.xml`**:
Busca y descomenta este bloque que está justo encima del `ListView`:
```xml
<Button
    android:id="@+id/btnExamenDialog"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:text="[EXAMEN] Añadir Curso (Diálogo + Cámara + BD)"
    android:layout_marginBottom="8dp"/>
```

**2. En `FragmentInicio.java`**:
Busca y descomenta este bloque dentro del `onViewCreated` para darle vida al botón:
```java
Button btnExamen = view.findViewById(R.id.btnExamenDialog);
if (btnExamen != null) {
    btnExamen.setOnClickListener(v -> {
        AnadirCursoDialogFrag dialog = new AnadirCursoDialogFrag();
        
        // El diálogo tiene un 'Listener' (Escuchador) personalizado.
        // Cuando termine de guardar en la BD, nos avisará ejecutando esto:
        dialog.setOnCursoAnadidoListener(nombreCurso -> {
            listaCursos.add(nombreCurso + " (En BD + Foto)");
            adapter.notifyDataSetChanged();
            lvCursos.smoothScrollToPosition(listaCursos.size() - 1);
        });
        
        dialog.show(getChildFragmentManager(), "AnadirCurso");
    });
}
```

### ¿Cómo funciona esa magia del `Listener`?
Dado que los `DialogFragment` son ventanas independientes, no pueden modificar directamente el `ListView` que está en el Fragmento principal. 

Para solucionar esto, en el código de `AnadirCursoDialogFrag.java` he creado una interfaz (`interface OnCursoAnadidoListener`). Cuando el diálogo termina de hacer el guardado en la base de datos (paso final del hilo de `AppExecutors`), ejecuta internamente `listener.onCursoAnadido(nombre)`. 

El `FragmentInicio` está "escuchando" esa orden, así que automáticamente coge el texto, lo añade a la lista visual de cursos (`adapter.notifyDataSetChanged()`) y hace scroll hacia abajo del todo. ¡Completamente asíncrono y en la vida real!