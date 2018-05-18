package entity;

import java.util.HashMap;
import java.util.Map;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;

import javafx.beans.property.StringProperty;

public abstract class Entity extends RecursiveTreeObject<Entity> {
    
    protected transient Map<String, StringProperty> properties = new HashMap<>();
    
    public void setProperties(Map<String, StringProperty> properties) {
        this.properties = properties;
    }
    
    public Map<String, StringProperty> getProperties(){
        return properties;
    }

    public abstract void setProperty();
}
