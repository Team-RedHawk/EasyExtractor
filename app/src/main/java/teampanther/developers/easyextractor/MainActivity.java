package teampanther.developers.easyextractor;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
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

import teampanther.developers.easyextractor.RecyclerView.Adapter;
import teampanther.developers.easyextractor.RecyclerView.OnItemSelectedListener;
import teampanther.developers.easyextractor.ui.InputDialog;
import teampanther.developers.easyextractor.UtilsHelper.FileHelper;
import teampanther.developers.easyextractor.UtilsHelper.PreferenceUtil;

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
    private SharedPreferences.Editor editor;
    private NavigationView navigationView;
    private CoordinatorLayout coordinatorLayout;
    private String name;
    private String type;
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private Adapter adapter;
    private File currentDirectory;
    private CollapsingToolbarLayout toolbarLayout;

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

        //hideItem();

        //cargarFragment();
    }

    /*
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == 0) {

            if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED)
                Snackbar.make(coordinatorLayout, "Permisos requeridos", Snackbar.LENGTH_INDEFINITE)
                        .setAction("Ajustes", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                MainActivity.this.gotoApplicationSettings();
                            }
                        })
                        .show();
            else {
               cargarFragment();
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void initActivityFromIntent() {
        name = getIntent().getStringExtra(EXTRA_NAME);
        type = getIntent().getStringExtra(EXTRA_TYPE);
    }


    private void cargarFragment() {
        String permission = Manifest.permission.WRITE_EXTERNAL_STORAGE;

        if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(this, permission)) {

            ActivityCompat.requestPermissions(this, new String[]{permission}, 0);

            return;
        }

        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        StorageFragment fragment = new StorageFragment();
        transaction.add(R.id.contenedor_fragment,fragment).commit();
    }

    private void hideItem()
    {
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (FileHelper.getStoragePath(MainActivity.this,true) == null) {
            Menu nav_Menu = navigationView.getMenu();
            nav_Menu.findItem(R.id.external).setVisible(false);
        }
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
            return;
        }
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    private void gotoApplicationSettings() {

        Intent intent = new Intent();

        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);

        intent.setData(Uri.fromParts("package", "teampanther.developers.easyextractor", null));

        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        //Fragment transaction and more
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();

        if (id == R.id.internal) {
            StorageFragment fragment = new StorageFragment();
            transaction.replace(R.id.contenedor_fragment,fragment);
        } else if (id == R.id.external) {
            StorageFragment fragment = StorageFragment.newInstance("SD","");
            transaction.replace(R.id.contenedor_fragment,fragment);
        }  else if (id == R.id.about) {

            About fragment = new About();
            transaction.replace(R.id.contenedor_fragment,fragment);
        }


        transaction.commit();
        //end Fragment transaction and more
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    private void initCoordinatorLayout() {
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinator_layout);
    }*/

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

        if (!FileHelper.isStorage(currentDirectory,this)) {

            setPath(currentDirectory.getParentFile());

            return;
        }

        super.onBackPressed();
    }


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

    @Override
    protected void onResume() {

        if (adapter != null) adapter.refresh();

        super.onResume();
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {

        adapter.select(savedInstanceState.getIntegerArrayList(SAVED_SELECTION));

        String path = savedInstanceState.getString(SAVED_DIRECTORY, getInternalStorage().getPath());

        if (currentDirectory != null) setPath(new File(path));

        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        outState.putIntegerArrayList(SAVED_SELECTION, adapter.getSelectedPositions());

        outState.putString(SAVED_DIRECTORY, getPath(currentDirectory));

        super.onSaveInstanceState(outState);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_contextual, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

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

            case R.id.action_sort:
                actionSort();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

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

    private void initActivityFromIntent() {
        name = getIntent().getStringExtra(EXTRA_NAME);
        type = getIntent().getStringExtra(EXTRA_TYPE);
    }

    private void initCoordinatorLayout() {
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinator_layout);
    }

    private void initAppBarLayout() {
        toolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar_layout);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setOverflowIcon(ContextCompat.getDrawable(this, R.drawable.ic_more));
        setSupportActionBar(toolbar);
    }

    private void initDrawerLayout() {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawerLayout == null) return;
        if (name != null || type != null) {
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        }
    }

    private void initFloatingActionButton() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.add_folder);
        if (fab == null) return;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actionCreate();
            }
        });
        if (name != null || type != null)
        {
            ViewGroup.LayoutParams layoutParams = fab.getLayoutParams();

            ((CoordinatorLayout.LayoutParams) layoutParams).setAnchorId(View.NO_ID);

            fab.setLayoutParams(layoutParams);

            fab.hide();
        }
    }

    private void initNavigationView() {

        navigationView = (NavigationView) findViewById(R.id.nav_view);

        if (navigationView == null) return;

        MenuItem menuItem = navigationView.getMenu().findItem(R.id.external);

        menuItem.setVisible(FileHelper.getStoragePath(MainActivity.this,true) != null);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.internal:
                        setPath(getInternalStorage());
                        return true;

                    case R.id.external:
                        setPath(new File(FileHelper.getStoragePath(MainActivity.this, true)));
                        return true;

                    case R.id.about:
                        return true;
                }

                drawerLayout.closeDrawers();

                switch (item.getItemId()) {

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

        TextView textView = (TextView) navigationView.getHeaderView(0).findViewById(R.id.textView);

        textView.setText(getStorageUsage(this));

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.this.startActivity(new Intent(Settings.ACTION_INTERNAL_STORAGE_SETTINGS));
            }
        });
    }

    private void loadIntoRecyclerView() {

        String permission = Manifest.permission.WRITE_EXTERNAL_STORAGE;

        if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(this, permission)) {

            ActivityCompat.requestPermissions(this, new String[]{permission}, 0);

            return;
        }

        final Context context = this;

        if (name != null) {

            adapter.addAll(FileHelper.searchFilesName(context, name));

            return;
        }

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

        setPath(getInternalStorage());
    }

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

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        if (recyclerView != null) recyclerView.setAdapter(adapter);
    }

    private void invalidateTitle() {

        if (adapter.anySelected()) {

            int selectedItemCount = adapter.getSelectedItemCount();

            toolbarLayout.setTitle(String.format("%s seleccionados", selectedItemCount));
        }
        else if (name != null) {

            toolbarLayout.setTitle(String.format("Busqueda por %s", name));
        }
        else if (type != null) {

            switch (type) {

                case "image":
                    toolbarLayout.setTitle("Images");
                    break;

                case "audio":
                    toolbarLayout.setTitle("Music");
                    break;

                case "video":
                    toolbarLayout.setTitle("Videos");
                    break;
            }
        }
        else if (currentDirectory != null && !currentDirectory.equals(getInternalStorage())) {

            toolbarLayout.setTitle(getName(currentDirectory));
        }
        else {

            toolbarLayout.setTitle(getResources().getString(R.string.app_name));
        }
    }

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


    public void theme() {
        sharedPreferences = getSharedPreferences("VALUES", Context.MODE_PRIVATE);
        int theme = sharedPreferences.getInt("THEME", 1);
        settingTheme(theme);
    }

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

    private void showMessage(Exception e) {

        showMessage(e.getMessage());
    }

    private void showMessage(String message) {

        Snackbar.make(coordinatorLayout, message, Snackbar.LENGTH_SHORT).show();
    }


    private void gotoSettings() {

        startActivity(new Intent(this, SettingsActivity.class));
    }

    private void gotoApplicationSettings() {

        Intent intent = new Intent();

        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);

        intent.setData(Uri.fromParts("package", "teampanther.developers.easyextractor", null));

        startActivity(intent);
    }

    private void setPath(File directory) {

        if (!directory.exists()) {

            Toast.makeText(this, "Directorio no existe", Toast.LENGTH_SHORT).show();

            return;
        }

        currentDirectory = directory;

        adapter.clear();

        adapter.clearSelection();

        adapter.addAll(FileHelper.getChildren(directory));

        invalidateTitle();
    }

    private void setName(String name) {

        Intent intent = new Intent(this, MainActivity.class);

        intent.putExtra(EXTRA_NAME, name);

        startActivity(intent);
    }

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

    private void actionCreate() {

        InputDialog inputDialog = new InputDialog(this, "Crear", "Crear directorio") {

            @Override
            public void onActionClick(String text) {

                try {
                    File directory = FileHelper.createDirectory(currentDirectory, text);

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

    private void actionDelete() {

        actionDelete(adapter.getSelectedItems());

        adapter.clearSelection();
    }

    private void actionDelete(final List<File> files) {

        final File sourceDirectory = currentDirectory;

        adapter.removeAll(files);

        String message = String.format("%s archivos borrados", files.size());

        Snackbar.make(coordinatorLayout, message, Snackbar.LENGTH_LONG)
                .setAction("Deshacer", new View.OnClickListener() {
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

    private void actionRename() {

        final List<File> selectedItems = adapter.getSelectedItems();

        InputDialog inputDialog = new InputDialog(this, "Renombrar", "Renombrar") {

            @Override
            public void onActionClick(String text) {

                adapter.clearSelection();

                try {

                    if (selectedItems.size() == 1) {

                        File file = selectedItems.get(0);

                        int index = adapter.indexOf(file);

                        adapter.updateItemAt(index, FileHelper.renameFile(file, text));
                    }
                    else {

                        int size = String.valueOf(selectedItems.size()).length();

                        String format = " (%0" + size + "d)";

                        for (int i = 0; i < selectedItems.size(); i++) {

                            File file = selectedItems.get(i);

                            int index = adapter.indexOf(file);

                            File newFile = FileHelper.renameFile(file, text + String.format(format, i + 1));

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

    private void actionSearch() {

        InputDialog inputDialog = new InputDialog(this, "Buscar", "Buscar") {

            @Override
            public void onActionClick(String text) {

                setName(text);
            }
        };

        inputDialog.show();
    }

    private void actionCopy() {

        List<File> selectedItems = adapter.getSelectedItems();

        adapter.clearSelection();

        transferFiles(selectedItems, false);
    }

    private void actionMove() {

        List<File> selectedItems = adapter.getSelectedItems();

        adapter.clearSelection();

        transferFiles(selectedItems, true);
    }

    private void actionSend() {

        Intent intent = new Intent(Intent.ACTION_SEND_MULTIPLE);

        intent.setType("*/*");

        ArrayList<Uri> uris = new ArrayList<>();

        for (File file : adapter.getSelectedItems()) {

            if (file.isFile()) uris.add(Uri.fromFile(file));
        }

        intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);

        startActivity(intent);
    }

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

        builder.setTitle("Ordenar por");

        builder.show();
    }

    //----------------------------------------------------------------------------------------------

    private void transferFiles(final List<File> files, final Boolean delete) {

        String paste = delete ? "movido(s)" : "copiado(s)";

        String message = String.format(Locale.getDefault(), "%d archivo(s) esperando ser %s", files.size(), paste);

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {

                    for (File file : files) {

                        adapter.addAll(FileHelper.copyFile(file, currentDirectory));

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

                    /*final ProgressDialog dialog = ProgressDialog.show(context, "", "Unzipping", true);

                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {

                            try {

                                MainActivity.this.setPath(unzip(file));

                                MainActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        dialog.dismiss();
                                    }
                                });
                            } catch (Exception e) {

                                MainActivity.this.showMessage(e);
                            }
                        }
                    });

                    thread.run();*/
                }
                else {

                    try {

                        Intent intent = new Intent(Intent.ACTION_VIEW);

                        intent.setDataAndType(Uri.fromFile(file), getMimeType(file));

                        startActivity(intent);
                    }
                    catch (Exception e) {

                        showMessage(String.format("No podemos abrir %s", getName(file)));
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