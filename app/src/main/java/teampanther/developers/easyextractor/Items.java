package teampanther.developers.easyextractor;


import java.io.File;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class Items {

    int image;
    String name;
    String size;
    String metadata;

    public Items(int image, String name, String size, long file){

        Date d = new Date(file);
        Calendar c = new GregorianCalendar();
        c.setTime(d);

        String day = Integer.toString(c.get(Calendar.DATE));
        String mouht = Integer.toString(c.get(Calendar.MONTH));
        String year = Integer.toString(c.get(Calendar.YEAR));
        String hora = Integer.toString(c.get(Calendar.HOUR_OF_DAY));
        String minuto = Integer.toString(c.get(Calendar.MINUTE));

        String datofull = day+"/"+mouht+"/"+year+"-"+hora+":"+minuto;

        this.metadata = datofull;

        // Metodo obtener peso los ficheros
        String sizeEnd;
        DecimalFormat df = new DecimalFormat("#.00");
        File longitud=new File(size);
        float peso = Float.parseFloat(String.valueOf(longitud));
        if(peso>1024000000) {
            sizeEnd =(df.format(peso/1024000000) + " Gb");
        }else if(peso>1024000) {
            sizeEnd =(df.format(peso/1024000) + " Mb");
        }else if(peso>1024) {
            sizeEnd =(df.format(peso/ 1024) + " Kb");

        }else {
            sizeEnd =(df.format(peso) + " bytes");

        }
        this.size = sizeEnd;

        this.image = image;
        this.name = name;
    }

    public int getImage() {
        return image;
    }

    public String getName(){

        return name;
    }

    public String getSize(){

        return size;
    }

    public String getMetadata(){
        return metadata;

    }
}

