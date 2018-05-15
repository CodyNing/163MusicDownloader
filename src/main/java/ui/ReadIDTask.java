package ui;

import javafx.concurrent.Task;

class ReadIDTask extends Task<Void> {

    private final String id;
    private final RunnableEvent event;

    public ReadIDTask(String id, RunnableEvent event) {
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
