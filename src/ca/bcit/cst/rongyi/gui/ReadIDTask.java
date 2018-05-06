package ca.bcit.cst.rongyi.gui;

import javafx.concurrent.Task;

public class ReadIDTask extends Task<Void> {

    private final String id;
    private final DownloadEvent event;

    public ReadIDTask(String id, DownloadEvent event) {
        this.id = id;
        this.event = event;
    }

    @Override
    protected Void call() {
        this.event.run(id);
        return null;
    }
}