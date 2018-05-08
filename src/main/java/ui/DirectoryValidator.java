package ui;

import java.io.File;

public class DirectoryValidator extends ValidatorBaseAdvanced {

    public DirectoryValidator(String message) {
        super(message);
    }

    @Override
    boolean valid(String text) {
        return new File(text).isDirectory();
    }

}
