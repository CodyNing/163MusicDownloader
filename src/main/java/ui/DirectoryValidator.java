package ui;

import java.io.File;

import static util.Downloader.TEMP_DIR;

public class DirectoryValidator extends ValidatorBaseAdvanced {

    public DirectoryValidator(String message) {
        super(message);
    }

    @Override
    boolean valid(String text) {
        File file = new File(text);
        return file != TEMP_DIR && file.isDirectory();
    }

}
