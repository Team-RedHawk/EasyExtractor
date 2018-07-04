package net.teamredhawk.hawktool.RecyclerView.Wallpaper;

public class WallpaperModel implements ItemInterface{
    String url;
    String vistas;
    String categoria;


    public WallpaperModel(String categoria, String url, String vistas) {
        this.categoria = categoria;
        this.url = url;
        this.vistas = vistas;
    }

    @Override
    public boolean isSection() {
        return false;
    }
}
