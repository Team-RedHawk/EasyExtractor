package teampanther.developers.easyextractor.UtilsHelper;

/**
 * Created by malcolmx on 28/12/17.
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Point;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.provider.MediaStore;
import android.support.v4.content.CursorLoader;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.text.format.Formatter;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.webkit.MimeTypeMap;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import teampanther.developers.easyextractor.R;

public class FileHelper {

    /*
    Funcion encargada de hacer la copia del archivo, utilizando las propiedades del tipo File
    En caso de ser directorio se aplica recursividad para por cada archivo dentro del directorio
    este sea copiado a la nueva direccion.
     */
    public static File copyFile(File src, File path) throws Exception {

        try {

            if (src.isDirectory()) {

                if (src.getPath().equals(path.getPath())) throw new Exception();

                File directory = createDirectory(path, src.getName());

                for (File file : src.listFiles()) copyFile(file, directory);

                return directory;
            }
            else {

                File file = new File(path, src.getName());

                FileChannel channel = new FileInputStream(src).getChannel();

                channel.transferTo(0, channel.size(), new FileOutputStream(file).getChannel());

                return file;
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new Exception(String.format("Error copiando %s", src.getName()));
        }
    }

    //----------------------------------------------------------------------------------------------

    /*
    Funcion encargada de crear un directorio, nuevamente usando las caracteristicas del objeto tipo
    file, para poder crearlo en caso de que no exista.
     */
    public static File createDirectory(File path, String name) throws Exception {

        File directory = new File(path, name);

        if (directory.mkdirs()) return directory;

        if (directory.exists()) throw new Exception(String.format("%s ya existe", name));

        throw new Exception(String.format("Error creando %s", name));
    }

    /*
    Funcion encargada de borrar un archivo, si se trata de un archivo, en caso de que sea directorio
    por cada archivo dentro del directorio se vuelve a llamar a la funcion para borrarse. Nuevamente
    usando las propiedades de objeto tipo file.
     */
    public static File deleteFile(File file) throws Exception {

        if (file.isDirectory()) {

            for (File child : file.listFiles()) {

                deleteFile(child);
            }
        }

        if (file.delete()) return file;

        throw new Exception(String.format("Error eliminando %s", file.getName()));
    }


    /*
    Funcion encargada de renombrar un archivo. Nuevamente usando las propiedades del objeto tipo
    file.
     */
    public static File renameFile(File file, String name) throws Exception {

        String extension = getExtension(file.getName());

        if (!extension.isEmpty()) name += "." + extension;

        File newFile = new File(file.getParent(), name);

        if (file.renameTo(newFile)) return newFile;

        throw new Exception(String.format("Error renombrando %s", file.getName()));
    }


    /*
    Funcion encargada de poder comprimir archivos en formato zip, requiere la lista de archivos a
    comprimir y la direccion a donde se pondra. Divido esta funcion en otra mas por si alguno de
    los archivos a comprimir es un directorio. Entonces se hace una busqueda recursiva para añadirlo
    al zip.
     */
    public static boolean zipFileAtPath(List<File> files, String toLocation){
        final int BUFFER = 2048;
        try {
            BufferedInputStream origin = null;
            FileOutputStream dest = new FileOutputStream(toLocation);
            ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(
                    dest));
            for(File sourceFile: files) {
                if (sourceFile.isDirectory()) {
                    zipSubFolder(out, sourceFile, sourceFile.getParent().length());
                } else {
                    byte data[] = new byte[BUFFER];
                    String unmodifiedFilePath = sourceFile.getPath();
                    String relativePath = unmodifiedFilePath
                            .substring(sourceFile.getParent().length()+1);
                    FileInputStream fi = new FileInputStream(unmodifiedFilePath);
                    origin = new BufferedInputStream(fi, BUFFER);
                    ZipEntry entry = new ZipEntry(relativePath);
                    out.putNextEntry(entry);
                    int count;
                    while ((count = origin.read(data, 0, BUFFER)) != -1) {
                        out.write(data, 0, count);
                    }
                    origin.close();
                }
            }
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /*
    Funcion complementaria de la creacion del zip donde si es directorio busco todos los archivos
    que contenga, y en caso de que ya no tenga ni un solo archivo por cuestiones de organizacion,
    conserve el directorio vacio.
     */
    public static void zipSubFolder(ZipOutputStream out, File folder,
                              int basePathLength) throws IOException {

        final int BUFFER = 2048;

        File[] fileList = folder.listFiles();
        if(fileList.length == 0){
            String relativePath = folder.getPath()
                    .substring(basePathLength+1);
            out.putNextEntry(new ZipEntry(relativePath+"/"));
        }
        BufferedInputStream origin = null;
        for (File file : fileList) {
            if (file.isDirectory()) {
                zipSubFolder(out, file, basePathLength);
            } else {
                byte data[] = new byte[BUFFER];
                String unmodifiedFilePath = file.getPath();
                String relativePath = unmodifiedFilePath
                        .substring(basePathLength+1);
                FileInputStream fi = new FileInputStream(unmodifiedFilePath);
                origin = new BufferedInputStream(fi, BUFFER);
                ZipEntry entry = new ZipEntry(relativePath);
                out.putNextEntry(entry);
                int count;
                while ((count = origin.read(data, 0, BUFFER)) != -1) {
                    out.write(data, 0, count);
                }
                origin.close();
            }
        }
    }


    /*
    Luego esta la contraparte el descomprimir un archivo .zip donde ingresamos el zip, y por cada
    archivo dentro del zip, lo ponemos dentro de la nueva carpeta con el nombre del zip.
     */
    public static File unzip(File zip) throws Exception {

        File directory = createDirectory(zip.getParentFile(), removeExtension(zip.getName()));

        FileInputStream fileInputStream = new FileInputStream(zip);

        BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);

        try (ZipInputStream zipInputStream = new ZipInputStream(bufferedInputStream)) {

            ZipEntry zipEntry;

            while ((zipEntry = zipInputStream.getNextEntry()) != null) {

                byte[] buffer = new byte[1024];

                File file = new File(directory, zipEntry.getName());

                if (zipEntry.isDirectory()) {

                    if (!file.mkdirs()) throw new Exception("Error descomprimiendo");
                }
                else {

                    int count;

                    try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {

                        while ((count = zipInputStream.read(buffer)) != -1) {

                            fileOutputStream.write(buffer, 0, count);
                        }
                    }
                }
            }
        }

        return directory;
    }

    /*
    Funcion que retorna la direccion de la memoria interna.
     */
    public static File getInternalStorage() {
        return Environment.getExternalStorageDirectory();
    }

    //----------------------------------------------------------------------------------------------

    /*
    Funcion que retorna la direccion de la memoria externa, aunque en algunos dispositivos funciona
    y en otros no, por eso la deje en caso de utilizarla en caso de que el otro metodo mas efectivo
    no funcione.
     */
    public static File getExternalStorage() {
        String path;
        if (Environment.isExternalStorageRemovable()){
            path = System.getenv("EXTERNAL_STORAGE");
        } else {
            path = System.getenv("SECONDARY_STORAGE");
            if (path == null || path.length() == 0) {
                path = System.getenv("EXTERNAL_SDCARD_STORAGE");
            }
        }

        return path != null ? new File(path) : null;
    }

    /*
    Funcion alternativa para obtener tanto la direccion de la memoria interna como la de la externa,
    en este caso si se quiere invocar la interna se pasan los parametros context y false, y si se
    quiere obtener la memoria externa se pasan los parametros context y true.
     */
    public static String getStoragePath(Context mContext, boolean is_removale) {
        //Mejor alternativa
        StorageManager mStorageManager = (StorageManager) mContext.getSystemService(Context.STORAGE_SERVICE);
        Class<?> storageVolumeClazz = null;
        try {
            storageVolumeClazz = Class.forName("android.os.storage.StorageVolume");
            Method getVolumeList = mStorageManager.getClass().getMethod("getVolumeList");
            Method getPath = storageVolumeClazz.getMethod("getPath");
            Method isRemovable = storageVolumeClazz.getMethod("isRemovable");
            Object result = getVolumeList.invoke(mStorageManager);
            final int length = Array.getLength(result);
            for (int i = 0; i < length; i++) {
                Object storageVolumeElement = Array.get(result, i);
                String path = (String) getPath.invoke(storageVolumeElement);
                boolean removable = (Boolean) isRemovable.invoke(storageVolumeElement);
                if (is_removale == removable) {
                    return path;
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }


    /*
    Funcion que retorna la direccion del directorio publico segun el tipo que se le pase en el
    parametro. Actualmente no se usa debido a que no se requiere, pero esta listo para ser utilizado
    cuando se requiera.
     */
    public static File getPublicDirectory(String type) {
        return Environment.getExternalStoragePublicDirectory(type);
    }

    //----------------------------------------------------------------------------------------------

    /*
    Funcion que obtiene la fecha de modificacion del archivo en el formato dia mes y año. Utilizadas
    si se habilita la opcion de mostrar fecha en ajustes.
     */
    public static String getLastModified(File file) {
        return DateFormat.format("dd MMM yyy", new Date(file.lastModified())).toString();
    }

    /*
    Funcion que obtiene el mime tipo por el archivo pasado o null si es que no hay alguno.
     */
    public static String getMimeType(File file) {
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(getExtension(file.getName()));
    }


    /*
    Funcion que retorna el nombre del archivo ocultando las extensiones de los tipos de archivos
    conocidos.
     */
    public static String getName(File file) {

        switch (FileType.getFileType(file)) {

            case DIRECTORY:
                return file.getName();

            case MISC_FILE:
                return file.getName();

            default:
                return removeExtension(file.getName());
        }
    }


    /*
    Funcion que retorna la direccion de donde esta ubicado el archivo o null si el archivo es null.
     */
    public static String getPath(File file) {

        return file != null ? file.getPath() : null;
    }


    /*
    Funcion que retorna el tamaño del archivo o si se trata de un directorio retorna vacio o el
    numero de archivos que contiene.
     */
    public static String getSize(Context context, File file) {

        if (file.isDirectory()) {

            File[] children = getChildren(file);

            if (children == null) return null;

            return String.format(" %s archivos", children.length);
        }
        else {

            return Formatter.formatShortFileSize(context, file.length());
        }
    }


    /*
    Funcion que nos retorna si el usuario puede o no usar Root, o si tiene o no habilitado el uso de
    root en la aplicacion.
     */
    public static boolean getStatusRoot(Context context){
        boolean retval=false;
        SharedPreferences sharedPreferences= context.getSharedPreferences("VALUES", Context.MODE_PRIVATE);
        if (sharedPreferences.getBoolean("ROOTENABLE",false)){
            if (canRunRootCommands()){
                retval = true;
            }else{
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("ROOTENABLE",false).apply();
                retval = false;
            }
        }else{
            retval = false;
        }
        return retval;
    }


    /*
    Funcion auxiliar a getStatusRoot, y esta nos obtiene el si puede o no correr comandos root, para
    saber si efectivamente cuenta el dispositivo con acceso root.
     */
    public static boolean canRunRootCommands()
    {
        boolean retval = false;
        Process suProcess;

        try
        {
            suProcess = Runtime.getRuntime().exec("su");

            DataOutputStream os = new DataOutputStream(suProcess.getOutputStream());
            BufferedReader osRes = new BufferedReader(new InputStreamReader(suProcess.getInputStream()));

            if (null != os && null != osRes)
            {
                // Getting the id of the current user to check if this is root
                os.writeBytes("id\n");
                os.flush();

                String currUid = osRes.readLine();
                boolean exitSu = false;
                if (null == currUid)
                {
                    retval = false;
                    exitSu = false;
                    Log.d("ROOT", "Can't get root access or denied by user");
                }
                else if (currUid.contains("uid=0"))
                {
                    retval = true;
                    exitSu = true;
                    Log.d("ROOT", "Root access granted");
                }
                else
                {
                    retval = false;
                    exitSu = true;
                    Log.d("ROOT", "Root access rejected: " + currUid);
                }

                if (exitSu)
                {
                    os.writeBytes("exit\n");
                    os.flush();
                }
            }
        }
        catch (Exception e)
        {
            // Can't get root !
            // Probably broken pipe exception on trying to write to output stream (os) after su failed, meaning that the device is not rooted

            retval = false;
            Log.d("ROOT", "Root access rejected [" + e.getClass().getName() + "] : " + e.getMessage());
        }

        return retval;
    }


    /*
    Funcion que ejecuta funciones a nivel kernel sin necesidad de terminal, en este caso no requiere
    de root.
     */
    public static String Executer(String command) {

        StringBuffer output = new StringBuffer();

        Process p;
        try {
            p = Runtime.getRuntime().exec(command);
            p.waitFor();
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));

            String line = "";
            while ((line = reader.readLine())!= null) {
                output.append(line + "n");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        String response = output.toString();
        return response;

    }

    /*
    Funcion que me retorna si un archivo contiene o no symlinks
     */
    public static boolean containsSymlink(File file) {
        try {
            return !file.getCanonicalFile().equals(file.getAbsoluteFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }


    /*
    Funcion que me retorna el resultado de una consulta de comandos con permisos root, utilizo esta
    para poder ejecutar acciones root.
     */
    public static String sudoForResult(String...strings) {
        String res = "";
        DataOutputStream outputStream = null;
        InputStream response = null;
        try{
            Process su = Runtime.getRuntime().exec("su");
            outputStream = new DataOutputStream(su.getOutputStream());
            response = su.getInputStream();

            for (String s : strings) {
                outputStream.writeBytes(s+"\n");
                outputStream.flush();
            }

            outputStream.writeBytes("exit\n");
            outputStream.flush();
            try {
                su.waitFor();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            res = readFully(response);
        } catch (IOException e){
            e.printStackTrace();
        } finally {
            Closer.closeSilently(outputStream, response);
        }
        return res;
    }

    /*
    Funcion auxiliar a Sudoforresult que nos retorna los resultados de los comandos en un termino
    leible.
     */
    public static String readFully(InputStream is) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length = 0;
        while ((length = is.read(buffer)) != -1) {
            baos.write(buffer, 0, length);
        }
        return baos.toString("UTF-8");
    }

    /*
    Funcion que nos retorna si busybox esta instalado o no usando de la otra funcion para ejecutar
    comandos con acceso root.
     */
    public static Boolean getBusyboxInstalled(){
        Boolean flag= false;
        String ok= sudoForResult("busybox | head -1");
        Log.d("Busybox",ok);
        if (!ok.isEmpty()){
            flag= true;
        }else{
            flag=false;
        }
        return flag;
    }

    /*
    Funcion que me retorna el total de memoria libre y usada del dispositivo, solo para fines
    informativos, se suman ambas memorias.
     */
    public static String getStorageUsage(Context context) {

        File internal = getInternalStorage();
        File external;
        if (getStoragePath(context,false) != null) {
            external = new File(getStoragePath(context, false));
        }else{
            external = null;
        }

        long f = internal.getFreeSpace();

        long t = internal.getTotalSpace();

        if (external != null) {

            f += external.getFreeSpace();

            t += external.getTotalSpace();
        }

        String use = Formatter.formatShortFileSize(context, t - f);

        String tot = Formatter.formatShortFileSize(context, t);

        return String.format("%s usados de %s", use, tot);
    }

    /*
     */
    public static String getTitle(File file) {

        try {

            MediaMetadataRetriever retriever = new MediaMetadataRetriever();

            retriever.setDataSource(file.getPath());

            return retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
        }
        catch (Exception e) {

            return null;
        }
    }

    /*
    Funcion que retorna la extension del archivo o un string vacio si es que no contiene extension.
     */
    public static String getExtension(String filename) {

        //returns the file extension or an empty string iff there is no extension

        return filename.contains(".") ? filename.substring(filename.lastIndexOf(".") + 1) : "";
    }

    //----------------------------------------------------------------------------------------------

    /*
    Funcion que retorna el nombre de un archivo pero quitandole la extension.
     */
    public static String removeExtension(String filename) {

        int index = filename.lastIndexOf(".");

        return index != -1 ? filename.substring(0, index) : filename;
    }


    /*
    Funcion que compara las fechas de dos archivos.
     */
    public static int compareDate(File file1, File file2) {

        long lastModified1 = file1.lastModified();

        long lastModified2 = file2.lastModified();

        return Long.compare(lastModified2, lastModified1);
    }

    //----------------------------------------------------------------------------------------------

    /*
    Funcion que compara los nombres de dos archivos.
     */
    public static int compareName(File file1, File file2) {

        String name1 = file1.getName();

        String name2 = file2.getName();

        return name1.compareToIgnoreCase(name2);
    }


    /*
    Funcion que compara los tamaños de dos archivos.
     */
    public static int compareSize(File file1, File file2) {

        long length1 = file1.length();

        long length2 = file2.length();

        return Long.compare(length2, length1);
    }

    //----------------------------------------------------------------------------------------------

    /*
    Funcion que retorna el resource icon para cada tipo de archivo especificado segun su mimetype.
     */
    public static int getImageResource(File file) {

        switch (FileType.getFileType(file)) {

            case DIRECTORY:
                return R.drawable.ic_folder;

            case MISC_FILE:
                return R.drawable.ic_file;

            case AUDIO:
                return R.drawable.ic_audio;

            case IMAGE:
                return R.drawable.ic_image;

            case VIDEO:
                return R.drawable.ic_video;

            case DOC:
                return R.drawable.ic_doc;

            case PPT:
                return R.drawable.ic_ppt;

            case XLS:
                return R.drawable.ic_xls;

            case PDF:
                return R.drawable.ic_pdf;

            case TXT:
                return R.drawable.ic_txt;

            case ZIP:
                return R.drawable.ic_zip;

            case APK:
                return R.drawable.ic_apk;

            case IMG:
                return R.drawable.ic_img_file;

            default:
                return 0;
        }
    }

    /*
    Funcion que nos dice si el archivo pasado como parametro es memoria de almacenamiento(Osea
    si es la direccion de la memoria interna o externa)
     */
    public static boolean isStorage(File dir,Context context) {
        return dir == null || dir.getPath().equals(getStoragePath(context,false)) || dir.getPath().equals(getStoragePath(context,true));
    }

    //----------------------------------------------------------------------------------------------

    /*
    Funcion que retorna un listado de archivos dentro de un directorio.
     */
    public static File[] getChildren(File directory) {

        if (!directory.canRead()) return null;

        return directory.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.exists() && !pathname.isHidden();
            }
        });
    }

    //----------------------------------------------------------------------------------------------

    /*
    Funcion encargada de la busqueda de archivos en la memoria. Segun su nombre.
     */
    public static ArrayList<File> searchFilesName(Context context, String name) {

        ArrayList<File> list = new ArrayList<>();

        Uri uri = MediaStore.Files.getContentUri("external");

        String data[] = new String[]{MediaStore.Files.FileColumns.DATA};

        Cursor cursor = new CursorLoader(context, uri, data, null, null, null).loadInBackground();

        if (cursor != null) {

            while (cursor.moveToNext()) {

                File file = new File(cursor.getString(cursor.getColumnIndex(data[0])));

                if (file.exists() && file.getName().startsWith(name)) list.add(file);
            }

            cursor.close();
        }

        return list;
    }

    /*
    Funcion encargada de proporcionar un tipo de archivo segun su mymetipe.
     */
    public enum FileType {

        DIRECTORY, MISC_FILE, AUDIO, IMAGE, VIDEO, DOC, PPT, XLS, PDF, TXT, ZIP, APK, IMG;

        public static FileType getFileType(File file) {

            if (file.isDirectory())
                return FileType.DIRECTORY;

            String mime = FileHelper.getMimeType(file);

            if (mime == null) {
                if (getExtension(file.getName()).equals("img")){
                    return FileType.IMG;
                }
                return FileType.MISC_FILE;
            }

            if (mime.startsWith("audio"))
                return FileType.AUDIO;

            if (mime.startsWith("image"))
                return FileType.IMAGE;

            if (mime.startsWith("video"))
                return FileType.VIDEO;

            if (mime.startsWith("application/ogg"))
                return FileType.AUDIO;

            if (mime.startsWith("application/msword"))
                return FileType.DOC;

            if (mime.startsWith("application/vnd.ms-word"))
                return FileType.DOC;

            if (mime.startsWith("application/vnd.ms-powerpoint"))
                return FileType.PPT;

            if (mime.startsWith("application/vnd.ms-excel"))
                return FileType.XLS;

            if (mime.startsWith("application/vnd.openxmlformats-officedocument.wordprocessingml"))
                return FileType.DOC;

            if (mime.startsWith("application/vnd.openxmlformats-officedocument.presentationml"))
                return FileType.PPT;

            if (mime.startsWith("application/vnd.openxmlformats-officedocument.spreadsheetml"))
                return FileType.XLS;

            if (mime.startsWith("application/pdf"))
                return FileType.PDF;

            if (mime.startsWith("text"))
                return FileType.TXT;

            if (mime.startsWith("application/zip"))
                return FileType.ZIP;

            if (mime.startsWith("application/vnd.android.package-archive"))
                return  FileType.APK;

            if (mime.startsWith("application/x-img"))
                return FileType.IMG;

            if (mime.startsWith("image/img"))
                return FileType.IMG;

            return FileType.MISC_FILE;
        }
    }
}