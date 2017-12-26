package teampanther.developers.easyextractor;

public class Items {

    int image;
    String name;
    String size;

    public Items(int image, String name, String size){

        this.image = image;
        this.name = name;
        this.size = size;
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
}
