package net.teamredhawk.hawktool.RecyclerView.Wallpaper;

public class WallpaperModel implements ItemInterface{
    String url;
    String vistas;


    public WallpaperModel(String url, String vistas) {
        this.url = url;
        this.vistas = vistas;
    }

    @Override
    public boolean isSection() {
        return false;
    }
}
