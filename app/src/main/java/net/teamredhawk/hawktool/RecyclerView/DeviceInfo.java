package net.teamredhawk.hawktool.RecyclerView;

public class DeviceInfo {
    public String nombre;
    public String desc;

    public DeviceInfo(String nombre, String desc){
        this.nombre = nombre;
        this.desc = desc;
    }

    public String getNombre(){
        return nombre;
    }

    public String getDesc(){
        return desc;
    }

    public void setNombre(String nombre){
        this.nombre = nombre;
    }

    public void setDesc(String desc){
        this.desc = desc;
    }
}
