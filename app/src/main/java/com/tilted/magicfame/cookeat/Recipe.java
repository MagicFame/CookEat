package com.tilted.magicfame.cookeat;

public class Recipe {
    private String name;
    private String calories;
    private String imageURL;
    private String id;

    public Recipe(String id, String name, String imageURL, String calories){
        this.id = id;
        this.name = name;
        this.imageURL = imageURL;
        this.calories = calories;
        modifyID();
    }

    public String getCalories() {
        return calories;
    }

    public void setCalories(String calories) {
        this.calories = calories;
    }

    public String getName() {
        return name;
    }
    public void setName(String name){
        this.name = name;
    }

    public String getId(){
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public void modifyID(){
        int i = 0;
        while(this.id.charAt(i) != '#'){
            i++;
        }
        String premiere_partie = this.id.substring(0, i);
        String seconde_partie = this.id.substring(i + 1, this.id.length());


        this.id = premiere_partie + "%23" +  seconde_partie;
    }
}
