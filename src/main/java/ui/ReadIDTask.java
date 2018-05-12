package ui;

import javafx.concurrent.Task;

class ReadIDTask extends Task<Void> {

    private final String id;
    private final DownloadEvent event;

    public ReadIDTask(String id, DownloadEvent event) {
        this.id = id;
        this.event = event;
    }

    @Override
    protected Void call() {
        if(this.event.run(id)) {
            succeeded();
        } else {
            failed();
        }
        return null;
    }
}
