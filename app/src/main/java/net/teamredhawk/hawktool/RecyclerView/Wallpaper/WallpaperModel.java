package net.teamredhawk.hawktool.RecyclerView.Wallpaper;

public class WallpaperModel implements ItemInterface{
    String url;
    String vistas;
    String urlFull;


    public WallpaperModel(String url, String vistas, String urlFull) {
        this.url = url;
        this.vistas = vistas;
        this.urlFull= urlFull;
    }

    @Override
    public boolean isSection() {
        return false;
    }
}
