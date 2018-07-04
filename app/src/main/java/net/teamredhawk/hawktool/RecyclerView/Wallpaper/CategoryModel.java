package net.teamredhawk.hawktool.RecyclerView.Wallpaper;

public class CategoryModel implements ItemInterface {
    public String title;

    public CategoryModel(String title) {
        this.title = title;
    }

    @Override
    public boolean isSection() {
        return true;
    }
}
