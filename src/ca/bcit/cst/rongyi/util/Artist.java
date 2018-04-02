package ca.bcit.cst.rongyi.util;

public class Artist {

    public final String name;
    public final String id;

    public Artist(String name, String id) {
        this.name = name;
        this.id = id;
    }

    @Override
    public String toString() {
        return "Artist{" +
                "name='" + name + '\'' +
                ", id='" + id + '\'' +
                '}';
    }
}
