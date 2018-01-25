package teampanther.developers.easyextractor;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.design.widget.Snackbar;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import teampanther.developers.easyextractor.ui.InputDialog;

import static teampanther.developers.easyextractor.FileHelper.getImageResource;
import static teampanther.developers.easyextractor.FileHelper.removeExtension;
import static teampanther.developers.easyextractor.FileHelper.renameFile;

public class Explorer {

    private Context context;
    private List pathFiles;
    private String rootPath;
    private String backPath;
    private AdapterItems adapter;

    public Explorer(Context context)
    {

        this.context = context;
    }

    public AdapterItems setItems(String path){
        rootPath= path;
        File directorio = new File(path);
        if (!FileHelper.isStorage(directorio,context)){
            backPath = directorio.getParent();
        }else{
            backPath = null;
        }
        File[] files = directorio.listFiles();
        pathFiles = new ArrayList();
        List pathFolders = new ArrayList();
        List nameFiles = new ArrayList();
        List sizeFiles = new ArrayList();
        List metadata = new ArrayList();

        /*IMPORTANTE LEER:
          en esta parte es donde tienes que implementar algun metodo para poder
          retroceder entre carpetas esta parte te la dejo a vos no es tan complicado
         */
        //Guardo Path de archivos a listar
        for (File archivo:files){

            if (archivo.isFile() && !archivo.getName().equals(".android_secure")){

                pathFiles.add(archivo.getPath());

            }else if(!archivo.getName().equals(".android_secure")) {

                pathFolders.add(archivo.getPath());
            }
        }

        if (files.length < 1){
            showMessage("No existen Archivos para mostrar");
        }


        //ordeno paths en forma ascendente

        Collections.sort(pathFiles,String.CASE_INSENSITIVE_ORDER);
        Collections.sort(pathFolders,String.CASE_INSENSITIVE_ORDER);

        //ahora agrego paths de folders a path de archivos

        for (int i = 0;i < pathFiles.size();i++){

            pathFolders.add(pathFiles.get(i));
        }

        //lleno array adapter para mostrar en lista
        //Items[] items = new Items[pathFolders.size()];
        ArrayList<Items> test= new ArrayList<>();

        for (int i = 0; i < pathFolders.size();i++){

            int image;

            File file = new File(pathFolders.get(i).toString());
            nameFiles.add(file.getName());
            sizeFiles.add(file.length()); // Optengo el peso
                                         // del fichero en Bytes
            metadata.add(file.lastModified());

            //Esta linea ahora obtiene la imagen correspondiente
            image= getImageResource(file);

            test.add(new Items(image,nameFiles.get(i).toString(),sizeFiles.get(i).toString(), (Long) metadata.get(i)));
        }

        pathFiles = pathFolders;
        adapter = new AdapterItems(context,R.layout.explorer_layout,test);

        return adapter;
    }

    public ArrayList<Items> getitemupdate(){
        File directorio = new File(rootPath);
        if (!FileHelper.isStorage(directorio,context)){
            backPath = directorio.getParent();
        }else{
            backPath = null;
        }
        File[] files = directorio.listFiles();
        pathFiles = new ArrayList();
        List pathFolders = new ArrayList();
        List nameFiles = new ArrayList();
        List sizeFiles = new ArrayList();
        List metadata = new ArrayList();
        for (File archivo:files){

            if (archivo.isFile() && !archivo.getName().equals(".android_secure")){

                pathFiles.add(archivo.getPath());

            }else if(!archivo.getName().equals(".android_secure")) {

                pathFolders.add(archivo.getPath());
            }
        }

        if (files.length < 1){
            showMessage("No existen Archivos para mostrar");
        }


        //ordeno paths en forma ascendente

        Collections.sort(pathFiles,String.CASE_INSENSITIVE_ORDER);
        Collections.sort(pathFolders,String.CASE_INSENSITIVE_ORDER);

        //ahora agrego paths de folders a path de archivos

        for (int i = 0;i < pathFiles.size();i++){

            pathFolders.add(pathFiles.get(i));
        }

        //lleno array adapter para mostrar en lista
        //Items[] items2 = new Items[pathFolders.size()];
        ArrayList<Items> test= new ArrayList<>();
        for (int i = 0; i < pathFolders.size();i++){

            int image;

            File file = new File(pathFolders.get(i).toString());
            nameFiles.add(file.getName());
            sizeFiles.add(file.length()); // Optengo el peso
            // del fichero en Bytes
            metadata.add(file.lastModified());


            //Remplazo esto por uno mejor estructurado
            /*
            if (file.isFile()){

                String name = file.getName(); //nombre del archivo
                int begin = name.length()-3; //Empiezo de substring
                int end = begin+3;
                //en caso que sea una imagen

                if ((name.substring(begin,end).toLowerCase().equals("jpg")) || (name.substring(begin,end).toLowerCase().equals("png"))){

                    image = R.drawable.ic_image;
                }else if (name.substring(begin,end).toLowerCase().equals("apk")){

                    image = R.drawable.ic_apk;
                }else{

                    image = R.drawable.ic_file;
                } if (name.substring(begin,end).toLowerCase().equals("img")){
                    image = R.drawable.ic_img;

                }

            }else {

                image = R.drawable.ic_folder;
            }*/

            //Esta linea ahora obtiene la imagen correspondiente
            image= getImageResource(file);

            test.add(new Items(image,nameFiles.get(i).toString(),sizeFiles.get(i).toString(), (Long) metadata.get(i)));
        }
        pathFiles = pathFolders;
        return test;
    }

    public List getPathFiles(){

        return pathFiles;
    }

    public String getRootPath(){
        return rootPath;
    }

    public String getPathBack(){
        return backPath;
    }

    //Este es un procedimiento que lo que hace es mostrar al mantener presionado un item
    //distitas opciones como eliminar etc etc
    public void setDialog(final File file, final String[] items){

        //seteo de imagen a mostrar para archivo
        int icon;
        //Nuevamente este metodo lo sustituyo por uno optimizado
        /*
        if (file.isFile()){

            String name = file.getName(); //nombre del archivo
            int begin = name.length()-3; //Empiezo de substring
            int end = begin+3;
            //en caso que sea una imagen

            if ((name.substring(begin,end).toLowerCase().equals("jpg")) || (name.substring(begin,end).toLowerCase().equals("png"))){

                icon = R.drawable.ic_image;
            }else if (name.substring(begin,end).toLowerCase().equals("apk")){

                icon = R.drawable.ic_apk;
            }else{

                icon = R.drawable.ic_file;
            } if (name.substring(begin,end).toLowerCase().equals("img")){
                icon = R.drawable.ic_img;

            }

        }else {

            icon = R.drawable.ic_folder;
        }*/

        icon= getImageResource(file);

        //En esta parte de aqui es donde se valida el evento elegido podrias implementar un switch
        //y mandar a llamar metodos para ciertas opciones
        String fileName = file.getName();
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(fileName);
        builder.setIcon(icon);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                switch (which){
                    case 0:
                        //copiar
                        break;
                    case 1:
                        //cortar
                        break;
                    case 2:
                        //comprimir
                        break;
                    case 3:
                        //renombrar
                        actionRename(file);
                        break;
                    case 4:
                        //eliminar
                        break;
                }
                Toast.makeText(context,"Accion: "+items[which],Toast.LENGTH_LONG).show();
            }
        });
        builder.create().show();
    }

    private void actionRename(final File file) {

        final InputDialog inputDialog = new InputDialog(context, "Renombrar", "Renombrar") {

            @Override
            public void onActionClick(String text) {
                try {
                        renameFile(file, text);
                        adapter.refreshEvents(getitemupdate());
                }
                catch (Exception e) {
                    showMessage(e);
                }
            }
        };
        inputDialog.setDefault(removeExtension(file.getName()));
        inputDialog.show();
    }

    public void showMessage(Exception e) {
        showMessage(e.getMessage());
    }

    public void showMessage(String message) {
        Toast.makeText(context,message,Toast.LENGTH_LONG).show();
    }



}
