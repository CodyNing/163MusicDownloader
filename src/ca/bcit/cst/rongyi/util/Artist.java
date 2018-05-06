package ca.bcit.cst.rongyi.util;

import java.util.List;

public class Artist {

    private final String name;
    private final String id;
    private List<Album> albumList;

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
