package ui;

import com.jfoenix.validation.base.ValidatorBase;
import javafx.scene.control.TextInputControl;

public abstract class ValidatorBaseAdvanced extends ValidatorBase {

    public ValidatorBaseAdvanced(String message) {
        super(message);
    }

    @Override
    protected void eval() {
        this.hasErrors.set(!valid(((TextInputControl) this.srcControl.get()).getText()));
    }

    abstract boolean valid(String text);
}
