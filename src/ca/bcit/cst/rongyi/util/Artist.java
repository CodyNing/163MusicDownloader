package ca.bcit.cst.rongyi.util;

public class Artist {

    private String name;
    private String id;

    public Artist(String name, String id) {
        this.name = name;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return "Artist{" +
                "name='" + name + '\'' +
                ", id='" + id + '\'' +
                '}';
    }
}
