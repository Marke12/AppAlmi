# 🆘 CHULETA OFFLINE DE EXAMEN (Ctrl+F) 🆘

*Esta guía rápida está pensada para que, en mitad del examen (sin internet), la abras en Android Studio, busques qué te están pidiendo en el enunciado, y sepas en qué archivo exacto de tu proyecto tienes que hacer Copiar/Pegar.*

---

## 💾 1. "Añadir una tabla a la Base de Datos" (Room / SQLite)
Si te piden que crees una nueva entidad en la base de datos (ej: "Aulas", "Notas", "Profesores").

1.  **Copiar Plantilla de Entidad:** Ve a `db/Curso.java` (o `Alumno.java` si tiene muchos campos). Cópialo, pégalo y cámbiale el nombre a tu nueva tabla. Cambia las variables. **No olvides generar los Getters y Setters**.
2.  **Copiar Plantilla de Consultas:** Ve a `db/CursoDao.java`. Cópialo, cámbiale el nombre y ajusta el `@Query("SELECT * FROM ...")` y el `@Insert`.
3.  **Registrarla en la BD (¡CRÍTICO!):** Ve a `db/AppDatabase.java`.
    *   Añade tu clase en la cabecera: `@Database(entities = {Usuario.class, Curso.class, TuClaseNueva.class}, version = 5)`
    *   **Acuérdate de cambiar el número de versión** (ej: de 4 a 5) para que actúe el `.fallbackToDestructiveMigration()`.
    *   Declara tu Dao dentro: `public abstract TuNuevoDao tuNuevoDao();`.

## 📸 2. "Que se abra la cámara y guarde la foto"
Si te piden poner un botón que abra la cámara nativa del móvil. Tienes el modelo perfecto y aislado en `DialogAddAlumnoFrag.java` o en `FragmentInterior.java`.

**Qué debes copiar a tu nueva Actividad/Fragmento:**
1.  **Las variables globales:**
    `private File fotoActual;`
    `private ActivityResultLauncher<Uri> tomarFotoLauncher;`
2.  **El Registro del Launcher (Pegar en el `onCreate`):**
    Copia el bloque `tomarFotoLauncher = registerForActivityResult(...)`. **Aquí es donde se ejecuta la respuesta exitosa de la foto**.
3.  **El método de Apertura:**
    Copia el método `private void lanzarCamara()`.
4.  **Si piden mostrar la foto en miniatura:**
    Acuérdate de usar Glide (la rúbrica lo exige).
    ```java
    Glide.with(requireContext()).load(fotoActual).centerCrop().into(tuImageView);
    ```

## ⏱️ 3. "Simular una carga" (Hilos Asíncronos / ProgressBar)
Si te piden que una acción no sea instantánea, sino que bloquee la pantalla con una barra de progreso que va subiendo.

1.  Ve a `RegisterActivity.java` y busca el método `animarYBorrar()`.
2.  **La estructura obligatoria:**
    ```java
    // 1. Ir al Hilo Secundario (NO bloquea la pantalla)
    AppExecutors.getInstance().getDiskIO().execute(() -> {
        
        try {
            for (int i = 1; i <= 10; i++) {
                Thread.sleep(200); // 2. Simulamos el trabajo pesado (Descarga/Cálculo)
                int progreso = i * 10;
                
                // 3. Volver al Hilo Principal (SÍ puede tocar la UI / Pantalla)
                AppExecutors.getInstance().getMainThread().execute(() -> {
                    miProgressBar.setProgress(progreso);
                });
            }
        } catch (InterruptedException e) { }
        
        // 4. Volver al Hilo Principal para el resultado final
        AppExecutors.getInstance().getMainThread().execute(() -> {
            Toast.makeText(contexto, "Terminado", Toast.LENGTH_SHORT).show();
        });
    });
    ```

## 👆 4. "Pasar datos a un Cuadro de Diálogo emergente"
Si te piden que al tocar un elemento de un ListView se abra una ventanita para **Editarlo**.

1.  El modelo a seguir es `DialogEditAlumnoFrag.java`.
2.  **El truco:** Crea un **Constructor** en el diálogo que reciba el objeto (ej: `public DialogEditAlumnoFrag(Alumno alumno)`).
3.  En el `onViewCreated` del diálogo, usa los datos de ese objeto para rellenar los `EditText` automáticamente (`etNombre.setText(alumnoActual.getNombre())`).
4.  Al darle a guardar, manda la orden a la base de datos de Room: `mDb.alumnoDao().actualizar(alumnoActual)`.

## 📳 5. "El botón se mueve / Tiembla" (Animaciones ObjectAnimator)
Si te piden animación de mover (Translation) o desvanecer (Alpha) al interactuar con algo.

1.  Ve a `RegisterActivity.java` y busca el método `animarYBorrar()`.
2.  Copia y adapta este fragmento:
    ```java
    PropertyValuesHolder pvhX = PropertyValuesHolder.ofFloat(View.TRANSLATION_X, 0f, 20f, -20f, 0f);
    ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(miBotonODialogo, pvhX);
    animator.setDuration(500); 
    animator.start();
    ```

## 👁️ 6. "Borrar el texto si paso la mano" (Sensor de Proximidad)
1.  Ve a `FragmentSensores.java` o `RegisterActivity.java`.
2.  **Qué debes copiar:**
    *   Variables: `SensorManager` y `Sensor`.
    *   Inicialización en `onCreate` / `onViewCreated`: `getSystemService(Context.SENSOR_SERVICE)`.
    *   El escuchador: `SensorEventListener` (Aquí pones tu `if` comparando con `getMaximumRange()`).
    *   **¡Vital!**: Los métodos `@Override onResume()` y `@Override onPause()` para encender y apagar el sensor.

## 🎤 7. "Búsqueda por Voz o Geocoder" (¡Cuidado, es Offline!)
*   Si te lo piden sabiendo que no va a ir, ve a `FragmentMapa.java`.
*   Para voz: Copia el `vozLauncher` y el método `invocarReconocimientoDeVoz()`. Usa `RecognizerIntent`.
*   Para Geocoder (Buscar lugar por texto): Copia el método `buscarLugar()`. **¡RECUERDA!** Tiene que ir dentro de un `ExecutorService` o `AppExecutors.getDiskIO()` o la app explotará al intentar acceder a la red en el hilo principal.

---
**🏆 REGLA DE ORO DEL EXAMEN:** 
Si la app se te cierra de golpe al pulsar un botón y en el Logcat lees algo de **"Only the original thread..."**, significa que has intentado cambiar un texto o una foto estando dentro de la Base de Datos. Pega tu código de cambiar la foto/texto dentro de un bloque `AppExecutors.getInstance().getMainThread().execute(() -> { ... });` y se solucionará.

---

## 🧠 CONSEJOS EXTRA PARA SOBREVIVIR OFFLINE

### 1. "No me acuerdo de cómo se importa esta clase" (Alt + Enter)
Al copiar y pegar código de un archivo a otro, muchísimas palabras se te pondrán en **rojo** (ej: `View`, `Toast`, `Intent`).
*   **No las escribas a mano arriba**. Simplemente haz clic sobre la palabra en rojo y pulsa `Alt + Enter`.
*   Android Studio te dirá "Import Class". Dale a enter y la importará mágicamente aunque no tengas internet.

### 2. "NullPointerException al buscar un botón (findViewById)"
Si copias un `findViewById` de un `Activity` a un `Fragment`, o viceversa, ten cuidado:
*   **En un Activity (ej. RegisterActivity):** Se escribe a secas: `findViewById(R.id.miBoton)`.
*   **En un Fragmento (ej. FragmentInicio):** Tienes que usar la vista inflada primero: `view.findViewById(R.id.miBoton)`. Si se te olvida poner el `view.` delante en un fragmento, el código no compilará o te dará null.

### 3. "Diferencias entre `this`, `getContext()` y `requireActivity()`"
Muchos métodos como los `Toast`, los `AlertDialog` o los `Adapters` te van a pedir que les pases un **Contexto** por parámetro. Al copiar y pegar, esto suele fallar muchísimo:
*   **Si estás dentro de un Activity (`MainActivity`):** Usa `this` o `NombreActividad.this`.
*   **Si estás dentro de un Fragmento (`FragmentMapa`):** Usa `requireContext()`. Si no te lo acepta, prueba con `getContext()`.
*   **Si estás dentro del código de un adaptador (`AlumnosAdapter`):** No tienes contexto directo. Tienes que pedírselo a la vista del layout usando `holder.itemView.getContext()`.

### 4. "¡Mi código crashea y no sé por qué!" (Uso del Logcat sin internet)
En un examen sin StackOverflow, tu única salvación es la pestaña **Logcat** de abajo.
1. Abre el Logcat.
2. Si está lleno de basura, límpialo (icono de la papelera) justo antes de darle al botón de la app que hace que crashee.
3. Toca el botón y busca la frase que empiece por `FATAL EXCEPTION`.
4. Busca las líneas azules. La **primera línea azul** que ponga "com.example.appalmi..." es exactamente el archivo y la línea de código donde está el error. Haz clic en lo azul y te llevará directo.

### 5. "El Gradle no me compila tras copiar cosas"
Si has tocado el `AndroidManifest.xml` (por ejemplo copiando el `FileProvider` o moviendo el `Intent-Filter` del Launcher) o el `build.gradle`, y al darle al Play te da error rojo:
*   Vete arriba a la barra de menú de Android Studio.
*   Dale a **Build > Clean Project**.
*   Cuando termine, dale a **Build > Rebuild Project**.
*   Esto borra los archivos temporales viejos y fuerza a Android Studio a leer tu código desde cero sin errores fantasma.