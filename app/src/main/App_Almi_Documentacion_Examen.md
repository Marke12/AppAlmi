# Documentación del Proyecto: App-Almi y Guía de Examen

Este documento detalla toda la arquitectura de la aplicación **App-Almi**, separando las características base requeridas para la evaluación del proyecto (10/10) y las funcionalidades avanzadas (actualmente comentadas en el código) implementadas como material de estudio para el examen offline.

---

## 1. Arquitectura Base (Evaluación del Proyecto 10/10)

Esta sección cubre todo lo que la aplicación ejecuta actualmente por defecto al compilar, cumpliendo estrictamente con la rúbrica del proyecto.

### Navegación Principal
*   **CentralActivity**: Actúa como la actividad principal (Launcher). Contiene el `DrawerLayout` y el `NavigationView` (Menú lateral).
*   Se gestiona la carga dinámica de tres fragmentos principales: `FragmentInicio`, `FragmentMapa` y `FragmentGaleria`.
*   **Animaciones**: Al cambiar entre fragmentos mediante el menú lateral, se aplican transiciones personalizadas definidas en `res/anim/fade_in.xml` y `res/anim/fade_out.xml`.

### Fragmento: Inicio (`FragmentInicio`)
*   Muestra el logotipo del centro (`ImageView`).
*   Despliega una lista estática de cursos leídos desde el archivo `strings.xml` y mostrados a través de un `ListView` estándar y un `ArrayAdapter`.

### Fragmento: Mapa (`FragmentMapa`)
*   Implementa la API de Google Maps (`SupportMapFragment`).
*   Centra la cámara en las coordenadas exactas de Almi Ikastetxea al cargar la vista (`onMapReady`).
*   Coloca un marcador con el título "Centro Almi".

### Fragmento: Galería (`FragmentGaleria`)
*   Usa una arquitectura de **Tabs** (`TabLayout`) y **ViewPager2** para navegar deslizando.
*   Contiene dos sub-fragmentos: `FragmentInterior` y `FragmentExterior`.
*   Ambos fragmentos hacen uso de un `RecyclerView` con un `GridLayoutManager` de 3 columnas.
*   **Glide**: Integrado dentro del `FotosAdapter` para cargar imágenes desde URLs remotas de forma asíncrona y eficiente.
*   **Dialogs**: Al hacer clic en cualquier foto de la galería, se abre un cuadro de diálogo nativo (`AlertDialog` personalizado) que muestra la imagen ampliada. Al pulsar sobre la imagen ampliada, el cuadro se cierra automáticamente.

---

## 2. Elementos del Examen (Código Comentado con Explicación Técnica)

Todas estas funcionalidades están programadas y probadas en el proyecto. Para evitar penalizaciones en el trabajo base, se han envuelto en comentarios (`/* ... */` en Java y `<!-- ... -->` en XML). Si en el examen te piden implementarlas, solo tienes que buscar las marcas `// EXAMEN` y quitar los comentarios.

### A. Base de Datos Local (SQLite / Room)
*   **Ubicación**: Carpeta `db/`, `MainActivity.java`, `RegisterActivity.java`, `LoginDialogFrag.java`.
*   **Descripción**: Sistema de autenticación de usuarios.
*   **Explicación Técnica**:
    *   Room funciona en 3 capas: **Entity** (La tabla `Usuario`), **Dao** (Las consultas SQL `UsuarioDao`) y la **Database** (El archivo físico `AppDatabase`).
    *   Para evitar que la app crashee al hacer cambios en las tablas (error de "Migration not found"), usamos `.fallbackToDestructiveMigration()` en el `Room.databaseBuilder`. Esto hace que, ante la duda, borre la BD vieja y la cree nueva limpia.
    *   Room **prohíbe** hacer lecturas/escrituras en el hilo principal (`MainThread`) porque congelaría la pantalla. Por eso usamos `AppExecutors.getInstance().getDiskIO().execute(...)` para mandar la operación de SQL a un hilo secundario invisible.
    *   Para leer la lista de usuarios usamos `LiveData`. Es un "observador" automático: si añades o borras un usuario en la BD, el `LiveData` avisa al instante a `RegisterActivity` para que repinte el `ListView` sin tener que volver a hacer la consulta SQL a mano.

### B. Sensores (Sensor de Proximidad)
*   **Ubicación**: `RegisterActivity.java` y `FragmentMapa.java`.
*   **Explicación Técnica**:
    *   Para usar sensores hay que pedirlos al sistema operativo usando el `SensorManager`: `getSystemService(Context.SENSOR_SERVICE)`.
    *   Definimos un escuchador (`SensorEventListener`) que tiene el método `onSensorChanged`.
    *   El sensor de proximidad suele dar 0.0 cuando está tapado y 5.0 (o su rango máximo) cuando está libre. Por eso el código pregunta: `if (event.values[0] < proximitySensor.getMaximumRange())`. Si el valor es menor que el máximo, es que la mano o la oreja están encima.
    *   **¡Muy Importante!**: Los sensores consumen mucha batería. Por eso, en los métodos del ciclo de vida de la actividad (`onResume`) le decimos al Manager que empiece a escuchar (`registerListener`), y en el `onPause` (cuando cerramos la app o la minimizamos) le obligamos a parar (`unregisterListener`).

### C. Tareas Asíncronas (Alternativa a AsyncTask) y Barra de Progreso
*   **Ubicación**: `RegisterActivity.java` (Botón "Limpiar BD").
*   **Explicación Técnica**:
    *   Tu profesor puede pedir el temario antiguo de `AsyncTask`, pero Android lo eliminó porque causaba fugas de memoria. La forma actual de hacerlo y que funciona es usando un `ExecutorService`.
    *   En el código (`animarYBorrar`), abrimos un hilo secundario para el trabajo pesado: `AppExecutors.getInstance().getDiskIO().execute(...)`.
    *   Hacemos un bucle `for` del 1 al 10 y en cada pasada congelamos el hilo 200 milisegundos (`Thread.sleep(200)`).
    *   **El truco crítico**: Tú no puedes actualizar la barra de progreso (`pbBorrado.setProgress()`) desde el hilo secundario (crasheará). Tienes que "saltar" temporalmente de vuelta al hilo de la interfaz de usuario usando `AppExecutors.getInstance().getMainThread().execute(...)` para actualizar la barrita visual, y luego el bucle secundario sigue su camino.

### D. Animaciones de Propiedad (Property Animations)
*   **Ubicación**: `RegisterActivity.java` (Botón "Limpiar BD").
*   **Explicación Técnica**:
    *   A diferencia de las animaciones XML clásicas (que solo mueven los píxeles de un botón pero el área de clic real se queda donde estaba), el `ObjectAnimator` mueve el botón físicamente y cambia sus propiedades matemáticas reales en pantalla.
    *   Se usa `PropertyValuesHolder`. Por ejemplo, `PropertyValuesHolder.ofFloat(View.TRANSLATION_X, 0f, 20f, -20f, 0f)` le dice al sistema: "Mueve este objeto en el eje X de 0 a 20, luego a -20, y luego a 0".
    *   Agrupamos ambos holders en el `ObjectAnimator`, le damos una duración de 500 milisegundos (`.setDuration(500)`) y lo arrancamos (`.start()`).

### E. Integración de la Cámara y Almacenamiento
*   **Ubicación**: `FragmentInterior.java` / `FragmentExterior.java`.
*   **Explicación Técnica**:
    *   **¿Por qué es tan complejo abrir la cámara?** A partir de Android 10, Google bloqueó el acceso directo a carpetas. Si tu app quiere que la cámara saque una foto y la guarde, tienes que crear un contrato legal (`FileProvider`) en el `AndroidManifest` y crear un XML (`file_paths.xml`) que diga a qué carpetas específicas les das permiso.
    *   En el código, creamos un archivo físico vacío temporal (`File`). Usamos el FileProvider para generar un `Uri` seguro para ese archivo.
    *   Lanzamos el intent de la cámara (`tomarFotoLauncher.launch(uri)`). La cámara llena de píxeles ese Uri seguro que le hemos dado.
    *   Una vez volvemos a la app con éxito, entra la API **MediaStore**. Esta API sirve para copiar ese archivo temporal "privado" de tu app, hacia la galería pública oficial de fotos del móvil (`Environment.DIRECTORY_PICTURES`). Se usan Input y Output Streams para copiar los bytes uno a uno.

### F. Reconocimiento de Voz y Búsqueda Web (Geocoder)
*   **Ubicación**: `FragmentMapa.java`.
*   **Explicación Técnica**:
    *   **Voz**: Invocamos un intent nativo de Android: `RecognizerIntent.ACTION_RECOGNIZE_SPEECH`. Al volver, sacamos los textos que Google ha entendido del paquete de resultados (`resultado.getData().getStringArrayListExtra`).
    *   **Geocoder**: Es una clase nativa de Android que usa los servidores de Google para hacer búsquedas. `geocoder.getFromLocationName(texto, 1)` busca 1 dirección que coincida con el texto de búsqueda.
    *   Al igual que en las bases de datos, buscar en los servidores de Google en el hilo principal bloquearía el móvil. Por lo que toda la búsqueda del Geocoder está dentro de un `ExecutorService.execute(...)`, y una vez tenemos las coordenadas (`Address` a `LatLng`), hacemos un `requireActivity().runOnUiThread(...)` para volver a tocar el mapa y añadir el marcador rojo.

### G. Interacciones de Interfaz Adicionales
*   **Doble Clic (`FragmentInicio`)**: Algoritmo manual de medición de milisegundos (`System.currentTimeMillis()`) para detectar doble toque rápido sobre un curso y borrarlo.
*   **Toast Personalizado (`LoginDialogFrag`)**: Un Toast construido desde cero cargando un layout XML personalizado (logo + texto) dentro de un fondo transparente en forma de Dialog.
*   **TextWatcher (`RegisterActivity`)**: Pinta la caja de texto en rojo al vuelo (mientras escribes) si la contraseña repetida no coincide con la original.

---

## 3. ¿Cómo prepararse para el Examen Offline?

1.  **Familiarízate con las rutas**: El examen te exigirá copiar el código base sin poder usar Google. Sabiendo que el código de la Cámara está en `FragmentInterior.java` o el de los Sensores en `FragmentMapa.java`, podrás abrir tus propios archivos en Android Studio y consultar la sintaxis sin necesidad de memorizarla entera.
2.  **Permisos Críticos en el Manifest**: Memoriza que para que el hardware o red funcionen, **siempre** hay que mirar el `AndroidManifest.xml` (Permisos de Internet, Storage, Features del Sensor de proximidad y el `<provider>` de la cámara). Si falla en el examen, es lo primero que debes revisar.
3.  **Hilos (Threading)**: Si tocas la UI (`Toast`, `.setText()`, `.setVisibility()`) desde el fondo (`AppExecutors.getDiskIO()` o un `Thread`), la app se cerrará ("*Only the original thread that created a view hierarchy can touch its views*"). En el examen, recuerda siempre envolver lo visual en un `runOnUiThread` o el `getMainThread()`.