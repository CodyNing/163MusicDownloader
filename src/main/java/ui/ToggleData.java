package ui;

public class ToggleData {

    private RunnableEvent event;
    private String data;

    public ToggleData(RunnableEvent event, String data) {
        this.event = event;
        this.data = data;
    }

    public RunnableEvent getEvent() {
        return event;
    }

    public String getData() {
        return data;
    }
}
