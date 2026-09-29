# 🆘 CHULETA OFFLINE DE EXAMEN (Ctrl+F) 🆘

*Esta guía rápida está pensada para que, en mitad del examen (sin internet), sepas dónde encontrar dentro de este mismo proyecto el código para resolver cualquier funcionalidad extra que te pidan implementar sobre el proyecto base original de "App-Almi".*

---

## 🛠️ ¿DÓNDE ESTÁ EL CÓDIGO QUE NECESITO RECICLAR?

Como el proyecto original solo exigía (Menú Lateral, ListView, Animaciones, Tabs, Mapa y Glide con RecyclerView), todas las funcionalidades "avanzadas" que caen en el examen las hemos implementado a lo largo del código. **Búscalas aquí:**

### 1. La Cámara y Guardar Fotos (MediaStore + FileProvider)
Si te piden sacar una foto, guardarla físicamente en el móvil y mostrarla en la pantalla.
*   **¿Dónde tienes un ejemplo funcionando?**: En `FragmentMisFotos.java` (botón "Sacar Foto") o en `DialogAddAlumnoFrag.java`.
*   **¿Qué necesitas copiar?**:
    1.  Las variables globales: `private File fotoActual;` y el `tomarFotoLauncher`.
    2.  El bloque del `tomarFotoLauncher` que va en el `onCreate()`. **OJO**: Fíjate cómo usa Glide cargando con `Uri.fromFile(fotoActual)` para que no salga en gris.
    3.  El método `lanzarCamara()`.
    4.  *(Opcional)* Si te piden guardar la foto en la galería oficial de Android, tienes el método `guardarEnGaleria(File archivo)` en `FragmentMisFotos.java`.

### 2. Base de Datos SQLite (Room)
Si te piden crear una tabla nueva para persistir datos (ej. "Aulas", "Notas", "Profesores") o guardar favoritos.
*   **¿Dónde tienes un ejemplo funcionando?**: En la carpeta `db/`. Tienes 4 tablas de ejemplo: `Usuario`, `Curso`, `Alumno`, `FotoEntity`.
*   **Pasos a seguir:**
    1.  **Crea la Entidad:** Copia `Curso.java`, pégalo con otro nombre, cambia sus variables y general los Getters/Setters.
    2.  **Crea el Dao:** Copia `CursoDao.java` y adapta sus consultas (`@Insert`, `@Query`).
    3.  **Actualiza AppDatabase (¡CRÍTICO!):** Añade tu nueva tabla a `@Database(entities = {..., TuClase.class}, version = X)`. **IMPORTANTE**: Suma +1 al número de versión (ej. de 4 a 5) para que no crashee por culpa de una migración faltante. Room borrará todo automáticamente gracias a `.fallbackToDestructiveMigration()`.
*   **¿Cómo ejecuto las consultas?**: Usando el hilo secundario (NUNCA en el principal): `AppExecutors.getInstance().getDiskIO().execute(() -> { ... });` (Mira cómo se usa en `RegisterActivity` o `DialogAddAlumnoFrag`).

### 3. Sensores del Teléfono
Si te piden que al interactuar físicamente con el móvil ocurra algo.
*   **¿Dónde tienes un ejemplo funcionando?**: En `FragmentSensores.java`.
*   **¿Qué necesitas copiar?**:
    1.  Variables: `SensorManager` y el `Sensor` (ej. `proximitySensor` o `lightSensor`).
    2.  Asignar el sensor en el `onViewCreated` usando `getSystemService`.
    3.  Copiar el método gigante `@Override public void onSensorChanged(SensorEvent event)`.
    4.  **¡Cuidado!** Para que la batería no se muera, DEBES copiar también el `onResume` (que arranca el sensor) y el `onPause` (que lo apaga).

### 4. Cuadros de Diálogo Emergentes (Dialogs Interactivos)
Si te piden que en vez de abrir otra pantalla, salte un cuadro preguntando datos o confirmaciones.
*   **Modelo Básico (Solo Confirmar "Sí/No"):** Lo tienes en `FragmentAlumnos.java` (el `AlertDialog` que sale al borrar un alumno haciendo click largo).
*   **Modelo Avanzado (Con Cajas de Texto y Botones):** Lo tienes en `DialogAddAlumnoFrag.java` o `DialogEditAlumnoFrag.java`. Se hace creando una clase separada que herede de `DialogFragment` y que infle su propio layout XML (ej. `dialog_add_alumno.xml`).

### 5. Reconocimiento de Voz y Búsqueda de Localizaciones (Geocoder)
Si te piden que la app escuche tu voz o busque una calle para ponerla en el mapa.
*   **¿Dónde tienes un ejemplo funcionando?**: En `FragmentMapa.java`.
*   **Reconocimiento de voz:** Copia el `vozLauncher` (que va en el `onCreate`) y el método `invocarReconocimientoDeVoz()`.
*   **Buscar calle (Geocoder):** Tienes el método `buscarLugar()`. ¡Fíjate que está envuelto en un `ExecutorService` para no congelar la pantalla porque necesita acceso a internet!

### 6. Animaciones Asíncronas y Temblores
Si te piden animar botones o poner una barra de progreso de carga antes de hacer algo.
*   **¿Dónde tienes un ejemplo funcionando?**: En `RegisterActivity.java`, en el método `animarYBorrar()`.
*   Aprende cómo usa `ObjectAnimator` para desplazar (`TRANSLATION_X`) el botón y cómo abre un bucle `for` en un hilo secundario y hace "saltos" hacia el hilo principal (`AppExecutors.getInstance().getMainThread().execute`) para ir llenando el `ProgressBar`.

---

## ✂️ GUÍA RÁPIDA DE MODIFICACIÓN DE LA UI DEL PROYECTO
El examen suele pedir alteraciones estructurales de lo que ya entregaste.

### 1. "Añade una nueva opción al Menú Lateral (Navigation Drawer)"
1. Ve a `res/menu/nav_menu.xml` y copia un `<item>` existente. Ponle un nuevo ID (ej. `@+id/menuNuevo`) y un Título.
2. Ve a `CentralActivity.java`, baja al método `onMenuItemClick`.
3. Añade la condición: `else if (id == R.id.menuNuevo) cargarFragmento(new TuNuevoFragmento());`

### 2. "Añade una nueva Pestaña (Tab) a la Galería"
1. Ve a `FragmentGaleria.java`, busca el array `String[] titulos = {"Interior", "Exterior", "Mis Fotos"}` y añade un cuarto título al final.
2. Ve a `adaptadores/GaleriaPagerAdapter.java`. 
3. En `getItemCount()`, cambia el `return 3;` a `return 4;`.
4. En `createFragment()`, añade un `else if (position == 3) { return new TuNuevoFragmento(); }`.

### 3. "Al hacer clic largo en la lista (ListView/RecyclerView)..."
*   **Para ListView:** (Portada de Cursos): Copia el bloque `lvCursos.setOnItemLongClickListener(...)` que hay comentado dentro de `RegisterActivity`.
*   **Para RecyclerView:** Tienes que tocar el archivo del adaptador (Ej. `AlumnosAdapter.java`), irte abajo del todo a la función `onBindViewHolder` y ponerle al `holder.itemView.setOnLongClickListener(...)`.

### 4. "Haz que la App arranque directamente en el Registro"
1. Abre `AndroidManifest.xml`.
2. Busca la etiqueta `<intent-filter>` (la que tiene `android.intent.action.MAIN` y `LAUNCHER`).
3. Córtala entera (Ctrl+X) de donde esté (actualmente en `CentralActivity`), y pégala (Ctrl+V) **dentro** de la etiqueta de la actividad donde quieras que arranque.

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
2. Límpialo (icono de la papelera) justo antes de darle al botón de la app que hace que crashee.
3. Toca el botón y busca la frase que empiece por `FATAL EXCEPTION`.
4. Busca las líneas azules. La **primera línea azul** que ponga "com.example.appalmi..." es exactamente el archivo y la línea de código donde está el error. Haz clic en lo azul y te llevará directo.

### 5. "El Gradle no me compila tras copiar cosas"
Si has tocado el `AndroidManifest.xml` o copiado un archivo con un nombre mal puesto y al darle al Play te da error rojo de compilación:
*   Vete arriba a la barra de menú de Android Studio.
*   Dale a **Build > Clean Project**.
*   Cuando termine, dale a **Build > Rebuild Project**.
*   Esto borra los archivos temporales viejos y fuerza a Android Studio a leer tu código desde cero sin errores fantasma.

### 6. "¡He borrado/roto algo sin querer!" (Salvavidas Offline)
Si por nervios borras una clase o rompes un código que funcionaba:
**NO ENTRES EN PÁNICO Y NO INTENTES REHACERLO DE MEMORIA.**
1. Haz clic derecho en el archivo (o en la carpeta app si lo has borrado entero).
2. Selecciona **Local History -> Show History**.
3. Android Studio guarda todo offline minuto a minuto. Busca la versión de hace 10 minutos y dale al botón **Revert** (una flecha azul).