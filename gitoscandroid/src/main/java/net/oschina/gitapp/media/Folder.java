package net.oschina.gitapp.media;


import java.util.ArrayList;

class Folder {
    private String name;
    private String path;
    private String albumPath;
    private ArrayList<Image> images = new ArrayList<>();

    String getName() {
        return name;
    }

    void setName(String name) {
        this.name = name;
    }

    String getPath() {
        return path;
    }

    void setPath(String path) {
        this.path = path;
    }

    ArrayList<Image> getImages() {
        return images;
    }

    String getAlbumPath() {
        return albumPath;
    }

    void setAlbumPath(String albumPath) {
        this.albumPath = albumPath;
    }

    @Override
    public boolean equals(Object o) {
        if (o != null && o instanceof Folder) {
            if (((Folder) o).getPath() == null && path != null)
                return false;
            String oPath = ((Folder) o).getPath().toLowerCase();
            return oPath.equals(this.path.toLowerCase());
        }
        return false;
    }
}
