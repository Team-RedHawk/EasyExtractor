package net.teamredhawk.hawktool.RecyclerView.Wallpaper;

public class CategoryModel implements ItemInterface {
    public String title;
    public int id;

    public CategoryModel(String title, int id) {
        this.title = title;
        this.id= id;
    }

    @Override
    public boolean isSection() {
        return true;
    }
}
