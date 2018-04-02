package ca.bcit.cst.rongyi.util;

public class Album {

    public final String name;
    public final String id;

    public Album(String name, String id) {
        this.name = name;
        this.id = id;
    }

    @Override
    public String toString() {
        return "Album{" +
                "name='" + name + '\'' +
                ", id='" + id + '\'' +
                '}';
    }
}
