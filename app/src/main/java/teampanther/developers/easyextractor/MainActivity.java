package teampanther.developers.easyextractor;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import teampanther.developers.easyextractor.Dialogs.About_dialog;
import teampanther.developers.easyextractor.Dialogs.ScriptDialog;
import teampanther.developers.easyextractor.RecyclerView.Adapter;
import teampanther.developers.easyextractor.RecyclerView.OnItemSelectedListener;
import teampanther.developers.easyextractor.Dialogs.InputDialog;
import teampanther.developers.easyextractor.UtilsHelper.FileHelper;
import teampanther.developers.easyextractor.UtilsHelper.PreferenceUtil;

import terranovaproductions.newcomicreader.FloatingActionMenu;

import static android.view.View.VISIBLE;
import static teampanther.developers.easyextractor.UtilsHelper.FileHelper.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {
    private static final String SAVED_DIRECTORY = "teampanther.developers.easyextractor.SAVED_DIRECTORY";
    private static final String SAVED_SELECTION = "teampanther.developers.easyextractor.SAVED_SELECTION";
    private static final String EXTRA_NAME = "teampanther.developers.easyextractor.EXTRA_NAME";
    private static final String EXTRA_TYPE = "teampanther.developers.easyextractor.EXTRA_TYPE";
    private SharedPreferences sharedPreferences;
    private NavigationView navigationView;
    private CoordinatorLayout coordinatorLayout;
    private String name;
    private String type;
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private Adapter adapter;
    private File currentDirectory;
    private CollapsingToolbarLayout toolbarLayout;
    private FloatingActionMenu menu;


    /*
    Al crearse la activity invoco a los metodos, separo todos para mejor organizacion de modulos
    pero practicamente se invocan desde aqui.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        initActivityFromIntent();
        theme();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initAppBarLayout();
        initCoordinatorLayout();
        initDrawerLayout();
        initFloatingActionButton();
        initNavigationView();
        initRecyclerView();
        loadIntoRecyclerView();
        invalidateToolbar();
        invalidateTitle();
    }


    /*
    Metodo al presionar back o retroceder en el movil. En esta parte especifico los eventos que
    pueden ocurrir en caso de que presionen back. La primer condicional es en caso de que el
    panel lateral este abierto, lo cierra. La segunda condicional es para deshacer la lista de
    seleccion de multiples objetos, si es que aun ahi algo seleccionado pues lo limpie y regrese a
    la vista normal. Luego sigue la ultima condicional donde checamos que la direccion actual de
    directorio sea diferente de la direccion root de algun almacenamiento, permita retroceder entre
    directorios. Y el ultimo parametro pues es el super constructor del metodo onback
     */
    @Override
    public void onBackPressed() {

        if (drawerLayout.isDrawerOpen(navigationView)) {

            drawerLayout.closeDrawers();

            return;
        }

        if (adapter.anySelected()) {

            adapter.clearSelection();

            return;
        }

        if (!isStorage(currentDirectory,this)) {

            setPath(currentDirectory.getParentFile());

            return;
        }

        super.onBackPressed();
    }

    /*
    Metodo exclusivo para versiones mayores a M, donde se debe dar los permisos manualmente para que
    la app funcione bien. Solo checo si los permisos estan concedidos o no, y en caso de que no pues
    entonces se lance un mensaje que pida los permisos. Si ya estan concedidos simplemente carga el
    view con los directorios y archivos.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == 0) {

            if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {

                Snackbar.make(coordinatorLayout, "Permisos requeridos", Snackbar.LENGTH_INDEFINITE)
                        .setAction("Ajustes", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                MainActivity.this.gotoApplicationSettings();
                            }
                        })
                        .show();
            }
            else {

                loadIntoRecyclerView();
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    /*
    Metodo en caso de que la actividad sea reiniciada, invoca esto y pues si el adaptador es null,
    refresca los datos para volver a llenar el adaptador.
     */
    @Override
    protected void onResume() {

        if (adapter != null) adapter.refresh();

        super.onResume();
    }


    /*
    En caso de guardar la instancia.
     */
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {

        adapter.select(savedInstanceState.getIntegerArrayList(SAVED_SELECTION));

        String path = savedInstanceState.getString(SAVED_DIRECTORY, getInternalStorage().getPath());

        if (currentDirectory != null) setPath(new File(path));

        super.onRestoreInstanceState(savedInstanceState);
    }

    /*
    Metodo que guarda una instancia para una mayor rapidez entre vistas, asi evita la carga de nuevo
    de elementos que antes ya estaban, si no se modifica nada.
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {

        outState.putIntegerArrayList(SAVED_SELECTION, adapter.getSelectedPositions());

        outState.putString(SAVED_DIRECTORY, getPath(currentDirectory));

        super.onSaveInstanceState(outState);
    }

    /*
    Metodo que agrega los elementos al menu.
    */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_contextual, menu);

        return true;
    }


    /*
    Metodo que captura si algun item del menu es seleccionado.
    */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            //Si algun elemento es seleccionado dependiendo del elemento este lanza un metodo segun
            //lo que se haya seleccionado.
            case R.id.action_delete:
                actionDelete();
                return true;

            case R.id.action_rename:
                actionRename();
                return true;

            case R.id.action_search:
                actionSearch();
                return true;

            case R.id.action_copy:
                actionCopy();
                return true;

            case R.id.action_move:
                actionMove();
                return true;

            case R.id.action_send:
                actionSend();
                return true;

            case R.id.action_compress:
                actionZip();
                return true;

            case R.id.action_sort:
                actionSort();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /*
    Metodo que es invocado antes de mostrar el menu, y donde especifico si debe o no visualizarse
    dependiendo de si es o no seleccionado algun objeto(directorio o archivo).
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        if (adapter != null) {

            int count = adapter.getSelectedItemCount();

            menu.findItem(R.id.action_delete).setVisible(count >= 1);

            menu.findItem(R.id.action_rename).setVisible(count >= 1);

            menu.findItem(R.id.action_search).setVisible(count == 0);

            menu.findItem(R.id.action_copy).setVisible(count >= 1 && name == null && type == null);

            menu.findItem(R.id.action_move).setVisible(count >= 1 && name == null && type == null);

            menu.findItem(R.id.action_send).setVisible(count >= 1);

            menu.findItem(R.id.action_compress).setVisible(count >=1);

            menu.findItem(R.id.action_sort).setVisible(count == 0);
        }

        return super.onPrepareOptionsMenu(menu);
    }

    /*
    Actualmente no usado
     */
    private void initActivityFromIntent() {
        name = getIntent().getStringExtra(EXTRA_NAME);
        type = getIntent().getStringExtra(EXTRA_TYPE);
    }

    /*
    Inicializo el coordinator layout, indispensable para poder mostrar los mensajes de error o exito.
     */
    private void initCoordinatorLayout() {
        coordinatorLayout = findViewById(R.id.coordinator_layout);
    }

    /*
    Metodo de inicializacion del AppBar donde se define el toolbar colapsable, el toolbar y se le
    asigna al actionbar.
     */
    private void initAppBarLayout() {
        toolbarLayout = findViewById(R.id.collapsing_toolbar_layout);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setOverflowIcon(ContextCompat.getDrawable(this, R.drawable.ic_more));
        setSupportActionBar(toolbar);
    }

    /*
    Metodo de inicializacion del panel lateral
     */
    private void initDrawerLayout() {
        drawerLayout = findViewById(R.id.drawer_layout);
        if (drawerLayout == null) return;
        if (name != null || type != null) {
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        }
    }


    /*
    Metodo de inicializacion del boton flotante tanto del que esta para agregar un nuevo directorio
    como del exclusivo para la carpeta de unpack repack img.
     */
    private void initFloatingActionButton() {
        //fab es el boton flotante para agregar un nuevo directorio.
        FloatingActionButton fab = findViewById(R.id.add_folder);

        //menu es el boton flotante esclusivo para las acciones unpack repack img
        menu = findViewById(R.id.fab);
        //Menu en forma de circulo
        //menu.setMultipleOfFB(3.2f);
        //menu.setIsCircle(true);
        //Menu en forma de linea
        //menu.setmItemGap(48);
        menu.setOnMenuItemClickListener(new FloatingActionMenu.OnMenuItemClickListener() {
            @Override
            public void onMenuItemClick(FloatingActionMenu fam, int index, FloatingActionButton item) {
                String str = "";
                switch (index) {
                    case 0:
                        str = "Click en instrucciones!";
                        break;
                    case 1:
                        ScriptUnpack();
                        break;
                    case 2:
                        str = "Click en repack!";
                        break;
                    default:
                }
            }
        });
        if (fab == null) return;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actionCreate();
            }
        });

        //Condicional que hace que se oculte el boton automaticamente al colapsar el toolbar.
        if (name != null || type != null) {
            ViewGroup.LayoutParams layoutParams = fab.getLayoutParams();

            ((CoordinatorLayout.LayoutParams) layoutParams).setAnchorId(View.NO_ID);

            fab.setLayoutParams(layoutParams);

            fab.hide();
        }
    }

    /*
    Metodo que inicializa el panel lateral.
     */
    private void initNavigationView() {

        navigationView = findViewById(R.id.nav_view);

        if (navigationView == null) return;

        MenuItem menuItem = navigationView.getMenu().findItem(R.id.external);

        menuItem.setVisible(getStoragePath(MainActivity.this,true) != null);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                //switch with not remplaze others

                drawerLayout.closeDrawers();

                switch (item.getItemId()) {
                    case R.id.internal:
                        setPath(getInternalStorage());
                        return true;

                    case R.id.external:
                        setPath(new File(getStoragePath(MainActivity.this, true)));
                        return true;

                    case R.id.about:
                        FragmentManager fragmentManager = getSupportFragmentManager();
                        About_dialog dialog = new About_dialog();
                        dialog.show(fragmentManager, "About_Dialog");
                        return true;

                /*case R.id.navigation_directory_0:
                    setPath(getPublicDirectory("DCIM"));
                    return true;

                case R.id.navigation_directory_1:
                    setPath(getPublicDirectory("Download"));
                    return true;

                case R.id.navigation_directory_2:
                    setPath(getPublicDirectory("Movies"));
                    return true;

                case R.id.navigation_directory_3:
                    setPath(getPublicDirectory("Music"));
                    return true;

                case R.id.navigation_directory_4:
                    setPath(getPublicDirectory("Pictures"));
                    return true;

                default:
                    return true;*/
                    case R.id.navigation_settings:
                        gotoSettings();
                        return true;
                    default:
                        return true;
                }

            }
        });

        TextView textView = navigationView.getHeaderView(0).findViewById(R.id.textView);

        textView.setText(getStorageUsage(this));

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.this.startActivity(new Intent(Settings.ACTION_INTERNAL_STORAGE_SETTINGS));
            }
        });
    }


    /*
    Metodo que se encarga de llenar la vista con los objetos obtenidos(directorios y archivos).
     */
    private void loadIntoRecyclerView() {

        String permission = Manifest.permission.WRITE_EXTERNAL_STORAGE;
        //Condicional que verifica si se han concedido los permisos necesarios en ANDROID M y superiores
        //en caso de que no sea asi pues se piden los permisos.
        if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(this, permission)) {

            ActivityCompat.requestPermissions(this, new String[]{permission}, 0);

            return;
        }

        final Context context = this;

        //En caso de busqueda entonces se actualizan los datos de la vista con los resultados de la busqueda.
        if (name != null) {

            adapter.addAll(searchFilesName(context, name));

            return;
        }

        //actualmente no utilizado
        if (type != null) {

            /*switch (type) {

                case "audio":
                    adapter.addAll(FileUtils.getAudioLibrary(context));
                    break;

                case "image":
                    adapter.addAll(FileUtils.getImageLibrary(context));
                    break;

                case "video":
                    adapter.addAll(FileUtils.getVideoLibrary(context));
                    break;
            }

            return;*/
        }

        //por defecto se asigna la vista el almacenamiento interno.
        setPath(getInternalStorage());
    }

    /*
    Metodo que inicializa el adaptador y el recyclerview para obttener los objetos(directorios y archivos)
     */
    private void initRecyclerView() {

        adapter = new Adapter(this);

        adapter.setOnItemClickListener(new OnItemClickListener(this));

        adapter.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected() {

                MainActivity.this.invalidateOptionsMenu();

                MainActivity.this.invalidateTitle();

                MainActivity.this.invalidateToolbar();
            }
        });

        if (type != null) {

            /*switch (type) {

                case "audio":
                    adapter.setItemLayout(R.layout.list_item_1);
                    adapter.setSpanCount(getResources().getInteger(R.integer.span_count1));
                    break;

                case "image":
                    adapter.setItemLayout(R.layout.list_item_2);
                    adapter.setSpanCount(getResources().getInteger(R.integer.span_count2));
                    break;

                case "video":
                    adapter.setItemLayout(R.layout.list_item_3);
                    adapter.setSpanCount(getResources().getInteger(R.integer.span_count3));
                    break;
            }*/
        }
        else {

            adapter.setItemLayout(R.layout.explorer_layout);

            adapter.setSpanCount(1);
        }

        RecyclerView recyclerView = findViewById(R.id.recycler_view);

        if (recyclerView != null) recyclerView.setAdapter(adapter);
    }


    /*
    Metodo que asigna un titulo al toolbar.
     */
    private void invalidateTitle() {

        if (adapter.anySelected()) {

            int selectedItemCount = adapter.getSelectedItemCount();

            toolbarLayout.setTitle(String.format("%s %s", selectedItemCount, getResources().getString(R.string.selected)));
        }
        else if (name != null) {

            toolbarLayout.setTitle(String.format("%s %s",getResources().getString(R.string.searchbyname), name));
        }
        else if (type != null) {

            /*switch (type) {

                case "image":
                    toolbarLayout.setTitle("Images");
                    break;

                case "audio":
                    toolbarLayout.setTitle("Music");
                    break;

                case "video":
                    toolbarLayout.setTitle("Videos");
                    break;
            }*/
        }
        else if (currentDirectory != null && !currentDirectory.equals(getInternalStorage())) {

            toolbarLayout.setTitle(getName(currentDirectory));
        }
        else {

            toolbarLayout.setTitle(getResources().getString(R.string.app_name));
        }
    }

    /*
    Metodo que dependiendo de lo que este realizando se cambia el aspecto en iconos que se agregan
    al toolbar.
     */
    private void invalidateToolbar() {

        if (adapter.anySelected()) {

            toolbar.setNavigationIcon(R.drawable.ic_clear);

            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    adapter.clearSelection();
                }
            });
        }
        else if (name == null && type == null) {

            toolbar.setNavigationIcon(R.drawable.ic_menu);

            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    drawerLayout.openDrawer(navigationView);
                }
            });
        }
        else {
            toolbar.setNavigationIcon(R.drawable.ic_back);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MainActivity.this.finish();
                }
            });
        }
    }

    /*
    Metodo que asigna el tema a la aplicacion con respecto al ultimo seleccionado o guardado.
     */
    public void theme() {
        sharedPreferences = getSharedPreferences("VALUES", Context.MODE_PRIVATE);
        int theme = sharedPreferences.getInt("THEME", 1);
        settingTheme(theme);
    }

    /*
    Metodo de apoyo al anterior metodo theme, que asigna segun el tema seleccionado.
     */
    public void settingTheme(int theme) {
        switch (theme) {
            case 1:
                setTheme(R.style.AppTheme);
                break;
            case 2:
                setTheme(R.style.AppTheme2);
                break;
            default:
                setTheme(R.style.AppTheme);
                break;
        }
    }

    /*
    Metodo que muestra un mensaje pasando como parametro una excepcion.
     */
    private void showMessage(Exception e) {

        showMessage(e.getMessage());
    }

    /*
    Metodo que muestra un mensaje pasando como parametro un string.
     */
    private void showMessage(String message) {

        Snackbar.make(coordinatorLayout, message, Snackbar.LENGTH_SHORT).show();
    }


    /*
    Metodo que lanza una actividad, en este caso la de ajustes de la app.
     */
    private void gotoSettings() {

        startActivity(new Intent(this, SettingsActivity.class));
    }

    /*
    Metodo que abre los ajustes del telefono, para asignar permisos.
     */
    private void gotoApplicationSettings() {

        Intent intent = new Intent();

        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);

        intent.setData(Uri.fromParts("package", "teampanther.developers.easyextractor", null));

        startActivity(intent);
    }

    /*
    Metodo que asigna la direccion segun el archivo pasado, y actualiza la vista con los objetos
    contenidos en la nueva direccion.
     */
    private void setPath(File directory) {

        if (!directory.exists()) {

            Toast.makeText(this, "Directorio no existe", Toast.LENGTH_SHORT).show();

            return;
        }
        //Si la opcion de unpack repack es habilitada pasamos esta condicional. Y si el directorio
        //que se asigna es el que se escogio en los ajustes entonces mostramos el boton flotante
        //para las herramientas de unpack repack.
        if (sharedPreferences.getBoolean("TOOLENABLE",false)){
            if (directory.getAbsolutePath().equals(sharedPreferences.getString("PATHTOOL",""))){
                menu.setVisibility(VISIBLE);
            }else{
                menu.setVisibility(View.GONE);
            }
        }


        currentDirectory = directory;

        adapter.clear();

        adapter.clearSelection();

        adapter.addAll(getChildren(directory));

        invalidateTitle();
    }

    /*
    Asigna el nombre de busqueda en la activity para despues capturarla y asignarla a los resultados
    de busqueda.
     */
    private void setName(String name) {

        Intent intent = new Intent(this, MainActivity.class);

        intent.putExtra(EXTRA_NAME, name);

        startActivity(intent);
    }


    /*
    Actualmente no usado
     */
    private void setType(String type) {

        Intent intent = new Intent(this, MainActivity.class);

        intent.putExtra(EXTRA_TYPE, type);

        if (Build.VERSION.SDK_INT >= 21) {

            intent.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);

            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
        }

        startActivity(intent);
    }


    //--Actions -->

    /*
    Metodo encargado de crear un directorio e invoco un dialog emergente para que le asigne el nuevo
    directorio y posterior a ello actualizo la vista con el nuevo directorio.
     */
    private void actionCreate() {

        InputDialog inputDialog = new InputDialog(this, "Crear", "Crear directorio") {

            @Override
            public void onActionClick(String text) {

                try {
                    File directory = createDirectory(currentDirectory, text);

                    adapter.clearSelection();

                    adapter.add(directory);
                }
                catch (Exception e) {

                    showMessage(e);
                }
            }
        };

        inputDialog.show();
    }

    /*
    Metodo para borrar.
     */
    private void actionDelete() {

        actionDelete(adapter.getSelectedItems());

        adapter.clearSelection();
    }

    /*
    Metodo para borrar segun la lista seleccionada
     */
    private void actionDelete(final List<File> files) {

        final File sourceDirectory = currentDirectory;

        adapter.removeAll(files);

        String message = (files.size() == 1) ? String.format(getResources().getString(R.string.onefiledelete)) :
                String.format("%s %s",files.size(), getResources().getString(R.string.filesdeletes));

        Snackbar.make(coordinatorLayout, message, Snackbar.LENGTH_LONG)
                .setAction(R.string.undo, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (currentDirectory == null || currentDirectory.equals(sourceDirectory)) {

                            adapter.addAll(files);
                        }
                    }
                })
                .addCallback(new Snackbar.Callback() {

                    @Override
                    public void onDismissed(Snackbar snackbar, int event) {

                        if (event != DISMISS_EVENT_ACTION) {

                            try {

                                for (File file : files) FileHelper.deleteFile(file);
                            }
                            catch (Exception e) {

                                showMessage(e);
                            }
                        }

                        super.onDismissed(snackbar, event);
                    }
                })
                .show();
    }


    /*
    Metodo para renombrar un archivo o directorio.
     */
    private void actionRename() {

        final List<File> selectedItems = adapter.getSelectedItems();

        InputDialog inputDialog = new InputDialog(this, getResources().getString(R.string.rename), getResources().getString(R.string.rename)) {

            @Override
            public void onActionClick(String text) {

                adapter.clearSelection();

                try {

                    if (selectedItems.size() == 1) {

                        File file = selectedItems.get(0);

                        int index = adapter.indexOf(file);

                        adapter.updateItemAt(index, renameFile(file, text));
                    }
                    else {

                        int size = String.valueOf(selectedItems.size()).length();

                        String format = " (%0" + size + "d)";

                        for (int i = 0; i < selectedItems.size(); i++) {

                            File file = selectedItems.get(i);

                            int index = adapter.indexOf(file);

                            File newFile = renameFile(file, text + String.format(format, i + 1));

                            adapter.updateItemAt(index, newFile);
                        }
                    }
                }
                catch (Exception e) {

                    showMessage(e);
                }
            }
        };

        if (selectedItems.size() == 1) {

            inputDialog.setDefault(removeExtension(selectedItems.get(0).getName()));
        }

        inputDialog.show();
    }

    /*
    Metodo para realizar una busqueda.
     */
    private void actionSearch() {

        InputDialog inputDialog = new InputDialog(this, "Buscar", "Buscar") {

            @Override
            public void onActionClick(String text) {

                setName(text);
            }
        };

        inputDialog.show();
    }

    /*
    Metodo para realizar la copia de archivos o directorios.
     */
    private void actionCopy() {

        List<File> selectedItems = adapter.getSelectedItems();

        adapter.clearSelection();

        transferFiles(selectedItems, false);
    }

    /*
    Metodo para mover archivos o directorios
     */
    private void actionMove() {

        List<File> selectedItems = adapter.getSelectedItems();

        adapter.clearSelection();

        transferFiles(selectedItems, true);
    }

    /*
    Metodo que realiza la compresion de archivos en formato zip.
     */
    private void actionZip(){
        InputDialog inputDialog = new InputDialog(this, "Comprimir", "Nombre de Archivo nuevo") {
            @Override
            public void onActionClick(final String text) {
                final List<File> selecteditems= adapter.getSelectedItems();
                adapter.clearSelection();
                final ProgressDialog dialog = ProgressDialog.show(MainActivity.this, "", "Comprimiendo", true);
                //Ocupo un hilo para no bloquear el hilo principal y realizar en background la compresion.
                Thread t= new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String nuevoPath= currentDirectory.getAbsolutePath()+File.separator+text+".zip";
                            if(FileHelper.zipFileAtPath(selecteditems,nuevoPath)){
                                runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    setPath(currentDirectory);
                                    dialog.dismiss();
                                }
                                });
                            }else{
                               dialog.dismiss();
                               MainActivity.this.showMessage("Error al comprimir el archivo");
                            }

                    }
                });
                t.start();
            }
        };

        inputDialog.show();
    }

    /*
    Metodo para enviar o compartir archivos o directorios.
     */
    private void actionSend() {

        Intent intente = new Intent(Intent.ACTION_SEND_MULTIPLE);

        intente.setType("*/*");

        ArrayList<Uri> uris = new ArrayList<>();

        for (File file : adapter.getSelectedItems()) {

            if (file.isFile()) uris.add(Uri.fromFile(file));
        }

        intente.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);

        startActivity(intente);
    }


    /*
    Metodo que organiza la vista segun lo seleccionado.
     */
    private void actionSort() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        int checkedItem = PreferenceUtil.getInteger(this, "pref_sort", 0);

        String sorting[] = {"Nombre", "Ultima vez modificado", "Tamaño (De mayor a menor)"};

        final Context context = this;

        builder.setSingleChoiceItems(sorting, checkedItem, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                adapter.update(which);

                PreferenceUtil.putInt(context, "pref_sort", which);

                dialog.dismiss();
            }
        });

        builder.setTitle(R.string.orderby);

        builder.show();
    }

    //----------------------------------------------------------------------------------------------

    //Metodo que segun lo que se desee hacer mueve o copia los archivos.
    private void transferFiles(final List<File> files, final Boolean delete) {

        String paste = delete ? "movido(s)" : "copiado(s)";

        String message = String.format(Locale.getDefault(), "%d archivo(s) esperando ser %s", files.size(), paste);

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {

                    for (File file : files) {

                        adapter.addAll(copyFile(file, currentDirectory));

                        if (delete) FileHelper.deleteFile(file);
                    }
                } catch (Exception e) {

                    MainActivity.this.showMessage(e);
                }
            }
        };

        Snackbar.make(coordinatorLayout, message, Snackbar.LENGTH_INDEFINITE)
                .setAction("Pegar", onClickListener)
                .show();
    }

    //--End Actions -->

    //-- Scripts Action -->

    //Metodo que pretende lanzar la accion de unpack donde lanzo el scriptdialog con la opcion de unpack
    public void ScriptUnpack(){
        final InputDialog inputDialog = new InputDialog(this, "Aceptar", "Nombre del archivo img") {
            @Override
            public void onActionClick(final String text) {
                File test= new File(currentDirectory.getAbsolutePath()+File.separator+text+".img");
                if (test.exists()){
                    InputDialog otroDialog= new InputDialog(MainActivity.this,"Aceptar","Nombre del directorio de salida") {
                        @Override
                        public void onActionClick(String otro) {
                            if (new File(currentDirectory.getAbsolutePath()+File.separator+otro).exists()){
                                showMessage(getString(R.string.error_direct_exist));
                            }else{
                                if (FileHelper.getStatusRoot(getApplicationContext())) {
                                    FragmentManager fragmentManager = getSupportFragmentManager();
                                    ScriptDialog unpack = new ScriptDialog();
                                    unpack.addNameImg(text + ".img");
                                    unpack.addDirectName(otro);
                                    unpack.mode(true);
                                    unpack.setFileDirect(currentDirectory);
                                    unpack.show(fragmentManager, "UnpackScript");
                                }else{
                                    showMessage(getString(R.string.error_root_acces));
                                }
                            }
                        }
                    };
                    otroDialog.show();
                }else{
                    showMessage(getString(R.string.error_not_file_name)+text+".img");
                }
            }
        };
        inputDialog.show();
    }

    /*
    Metodo para repack script aun en construccion XD
     */
    public void ScriptRepack(){

    }

    //Implementacion de si se le da click a un item.
    private final class OnItemClickListener implements teampanther.developers.easyextractor.RecyclerView.OnItemClickListener {

        private final Context context;

        private OnItemClickListener(Context context) {

            this.context = context;
        }

        @Override
        public void onItemClick(int position) {

            final File file = adapter.get(position);

            if (adapter.anySelected()) {

                adapter.toggle(position);

                return;
            }

            if (file.isDirectory()) {

                if (file.canRead()) {

                    setPath(file);
                }
                else {

                    showMessage("No podemos abrir el directorio");
                }
            }
            else {

                if (Intent.ACTION_GET_CONTENT.equals(getIntent().getAction())) {

                    Intent intent = new Intent();

                    intent.setDataAndType(Uri.fromFile(file), getMimeType(file));

                    setResult(Activity.RESULT_OK, intent);

                    finish();
                }
                else if (FileType.getFileType(file) == FileType.ZIP) {
                    //final ProgressDialog dialog = ProgressDialog.show(context, "", "Descomprimiendo", true);
                    final ProgressDialog dialog = ProgressDialog.show(context, "", "Descomprimiendo", true);
                    Thread t= new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                final File nueva= unzip(file);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        setPath(nueva);
                                        dialog.dismiss();
                                    }
                                });
                            }
                            catch (Exception e) {
                                dialog.dismiss();
                                showMessage(e);
                            }

                        }
                    });
                    t.start();

                }
                else if(getExtension(file.getName()).equals("img")){
                    //showMessage(FileHelper.getMimeType(file));
                }else {

                    try {

                        Intent intent = new Intent(Intent.ACTION_VIEW);

                        intent.setDataAndType(Uri.fromFile(file), getMimeType(file));

                        startActivity(intent);
                    }
                    catch (Exception e) {

                        showMessage(String.format("No hay aplicacion para abrir %s", getName(file)));
                    }
                }
            }
        }

        @Override
        public boolean onItemLongClick(int position) {

            adapter.toggle(position);

            return true;
        }
    }


}