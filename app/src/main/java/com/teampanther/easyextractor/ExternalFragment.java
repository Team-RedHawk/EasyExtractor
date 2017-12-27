package com.teampanther.easyextractor;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class ExternalFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    Explorer explorer;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public ExternalFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment InternalFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static InternalFragment newInstance(String param1, String param2) {
        InternalFragment fragment = new InternalFragment();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View vista = inflater.inflate(R.layout.fragment_internal, container, false);
        explorer = new Explorer(getContext());
        final ListView lista = (ListView) vista.findViewById(R.id.list_items);//Lista a mostrar carpetas etc
        String str_sdcard = ("/storage/");
        File path = new File(str_sdcard );
       // final String path = path1.toString();
        lista.setAdapter(explorer.setItems(path.toString()));
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
                    if (position == 0){

                        lista.setAdapter(explorer.setItems(returnPathBack(file.getPath().toString())));

                    }else {

                        lista.setAdapter(explorer.setItems(ruta));
                    }


                    Toast.makeText(getContext(),ruta,Toast.LENGTH_LONG)
                            .show();
                }

            }
        });

        //metodo para saber que item fue presionado

        lista.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
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
        });
        return vista;
    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
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

    public String returnPathBack (String path){

        int c = 0;
        Toast.makeText(getContext(),"Entra: "+path,Toast.LENGTH_LONG)
                .show();
        //ciclo que cuenta cuantos > se encuentran en la cadena
        for (int i = 0; i < path.length();i++){

            if (path.charAt(i) == '/')
                c++;
        }
        Toast.makeText(getContext(),Integer.toString(c),Toast.LENGTH_LONG)
                .show();
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

        Toast.makeText(getContext(),"Sale: "+path,Toast.LENGTH_LONG)
                .show();
        return path;
    }

}
