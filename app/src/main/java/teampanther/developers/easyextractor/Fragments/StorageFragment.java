package teampanther.developers.easyextractor.Fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import teampanther.developers.easyextractor.AdapterItems;
import teampanther.developers.easyextractor.Explorer;
import teampanther.developers.easyextractor.FileHelper;
import teampanther.developers.easyextractor.MainActivity;
import teampanther.developers.easyextractor.R;
import teampanther.developers.easyextractor.ui.InputDialog;

/**
 * Created by luffynando on 23/01/2018.
 */

public class StorageFragment extends Fragment{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private Explorer explorer;
    private ImageButton back;
    private AdapterItems adapter;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    /** MultiSelect list adapter */
    private OnFragmentInteractionListener mListener;
    private String CurrentDir;
    private CoordinatorLayout coordinatorLayout;
    private File ActualDir;

    public StorageFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     */
    // TODO: Rename and change types and number of parameters
    public static StorageFragment newInstance(String param1, String param2) {
        StorageFragment fragment = new StorageFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        initCoordinatorLayout();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View vista = inflater.inflate(R.layout.fragment_storage, container, false);
        explorer = new Explorer(getContext());
        final ListView lista = (ListView) vista.findViewById(R.id.list_items);//Lista a mostrar carpetas etc
        final String path;
        if (getArguments() != null){
            if (mParam1.equals("SD")){
                path = FileHelper.getStoragePath(getContext(),true);
            }else{
                path = FileHelper.getStoragePath(getContext(),false);
            }
        }else{
            path = FileHelper.getStoragePath(getContext(),false);
        }

        // final String path = path1.toString();
        back= (ImageButton) vista.findViewById(R.id.back_id);
        back.setVisibility(View.GONE);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (explorer.getPathBack() != null){
                    if (!FileHelper.isStorage(new File(explorer.getPathBack()),getContext())) {
                        back.setVisibility(View.VISIBLE);
                    }else{
                        back.setVisibility(View.GONE);
                    }
                    lista.setAdapter(explorer.setItems(explorer.getPathBack()));
                }else{
                    back.setVisibility(View.GONE);
                }
            }
        });
        CurrentDir= path;
        adapter = explorer.setItems(path);

        ImageButton mas=(ImageButton) vista.findViewById(R.id.add_folder);
        mas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actionCreate(lista);
            }
        });

        lista.setAdapter(adapter);
        lista.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        lista.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode actionMode, int i, long l, boolean b) {
                if (b) {
                    adapter.setNewSelection(i);
                }else{
                    adapter.removeSelection(i);
                }
                actionMode.setTitle(adapter.getSelectionCount() + " items seleccionados");
            }

            @Override
            public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                if (((AppCompatActivity)getActivity()).getSupportActionBar() != null){
                    ((AppCompatActivity)getActivity()).getSupportActionBar().hide();
                }else{
                    if (getActivity().getActionBar() != null){
                        getActivity().getActionBar().hide();
                    }
                }
                back.setVisibility(View.GONE);
                MenuInflater inflater = actionMode.getMenuInflater();
                inflater.inflate(R.menu.menu_contextual, menu);
                return true;
                }

            @Override
            public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.eliminar:
                        actionDelete(lista);
                        actionMode.finish();
                        return true;
                    case R.id.copiar:
                        return true;
                    case R.id.cortar:
                        return true;
                    case R.id.renombrar:
                        return true;
                    case R.id.comprimir:
                        return true;
                    default:
                        return false;
                }
            }

            @Override
            public void onDestroyActionMode(ActionMode actionMode) {
                if (((AppCompatActivity)getActivity()).getSupportActionBar() != null){
                    ((AppCompatActivity)getActivity()).getSupportActionBar().show();
                }else{
                    if (getActivity().getActionBar() != null){
                       getActivity().getActionBar().show();
                    }
                }
                adapter.clearSelection();
                if (explorer.getPathBack() != null){
                    if (!FileHelper.isStorage(new File(explorer.getRootPath()),getContext())) {
                        back.setVisibility(View.VISIBLE);
                    }else{
                        back.setVisibility(View.GONE);
                    }
                }else{
                    back.setVisibility(View.GONE);
                }
            }
        });
        //metodo para saber que elemento fue seleccionado
        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                List pathFiles = new ArrayList();
                pathFiles = explorer.getPathFiles();
                File file = new File(pathFiles.get(position).toString());

                if (file.isFile()){

                    //procedimiento para mostrar dialogo
                    explorer.setDialog(file,new String[]{"Copiar","Cortar","Comprimir "+file.getName()
                            ,"Renombrar "+file.getName()
                            ,"Eliminar"});
                }else{
                    String ruta = file.getPath();
                    CurrentDir= ruta;
                    back.setVisibility(View.VISIBLE);
                    if (position == 0){
                        lista.setAdapter(explorer.setItems(returnPathBack(ruta)));
                    }else {
                        lista.setAdapter(explorer.setItems(ruta));
                    }
                }

            }
        });

        //metodo para saber que item fue presionado

        /*lista.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                //obtengo item
                File file = new File(explorer.getPathFiles().get(position).toString());

                //procedimiento para mostrar dialogo
                explorer.setDialog(file,new String[]{"Copiar","Cortar","Comprimir "+file.getName()
                        ,"Renombrar "+file.getName()
                        ,"Eliminar"});


                return true;
            }
        });*/
        return vista;
    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    private void actionCreate(final ListView viewlist) {
        InputDialog inputDialog = new InputDialog(getContext(), "Crear", "Crear Directorio") {
            @Override
            public void onActionClick(String text) {
                try {
                    File directory = FileHelper.createDirectory(new File(explorer.getRootPath()), text);
                    adapter.clearSelection();
                    adapter = explorer.setItems(directory.getParent());
                    viewlist.setAdapter(adapter);
                }
                catch (Exception e) {
                    explorer.showMessage(e);
                }
            }
        };
        inputDialog.show();
    }

    private void actionDelete(final ListView viewlist) {
        ActualDir= new File(explorer.getRootPath());
        actionDelete(adapter.getCurrentCheckedPosition(),viewlist);
    }

    private File[] getFilesfromPath(ArrayList<Integer> items){
        File[] archivos= new File[items.size()];

        for(int i = 0; i < items.size();i++){
            archivos[i] = new File(explorer.getPathFiles().get(items.get(i)).toString());
        }
        return archivos;
    }

    private void actionDelete(final ArrayList<Integer> files, final ListView view) {
        final File sourceDirectory = new File(explorer.getRootPath());
        adapter.removeAll(files);
        adapter.notifyDataSetChanged();
        //view.setAdapter(adapter);
        String message = String.format("%s archivos eliminados", files.size());
        final File[] archivos = getFilesfromPath(files);
        Snackbar.make(coordinatorLayout, message, Snackbar.LENGTH_LONG)
                .setAction("Deshacer", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (ActualDir == null || ActualDir.equals(sourceDirectory)) {
                            adapter = explorer.setItems(explorer.getRootPath());
                            view.setAdapter(adapter);
                        }
                    }
                })
                .addCallback(new Snackbar.Callback() {

                    @Override
                    public void onDismissed(Snackbar snackbar, int event) {

                        if (event != DISMISS_EVENT_ACTION) {

                            try {
                                for (File file : archivos) FileHelper.deleteFile(file);
                            }
                            catch (Exception e) {
                                explorer.showMessage(e);
                            }

                            adapter = explorer.setItems(explorer.getRootPath());
                            view.setAdapter(adapter);
                        }

                        super.onDismissed(snackbar, event);
                    }
                })
                .show();
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private void initCoordinatorLayout() {
        coordinatorLayout = (CoordinatorLayout) getActivity().findViewById(R.id.coordinator_layout);
    }

    public String returnPathBack (String path){

        int c = 0;
        //ciclo que cuenta cuantos > se encuentran en la cadena
        for (int i = 0; i < path.length();i++){

            if (path.charAt(i) == '/')
                c++;
        }
        //ciclo utilizado para eliminar path actual

        int o = 0;
        for (int i = 0; i < path.length();i++){

            if (path.charAt(i) == '/'){

                o++;
                if (o == c){

                    path = path.substring(0,i);
                }
                break;
            }
        }
        return path;
    }

}
