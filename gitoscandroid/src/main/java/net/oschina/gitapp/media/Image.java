package net.oschina.gitapp.media;


class Image {
    private int id;
    private String path;
    private boolean isSelect;

    private String name;

     int getId() {
        return id;
    }

     void setId(int id) {
        this.id = id;
    }

     String getPath() {
        return path;
    }

     void setPath(String path) {
        this.path = path;
    }





     boolean isSelect() {
        return isSelect;
    }

     void setSelect(boolean select) {
        isSelect = select;
    }





     String getName() {
        return name;
    }

     void setName(String name) {
        this.name = name;
    }




}
