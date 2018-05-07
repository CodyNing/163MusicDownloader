package ui;

import com.jfoenix.validation.base.ValidatorBase;
import javafx.scene.control.TextInputControl;

public class PositiveNumberValidator extends ValidatorBase {

    public PositiveNumberValidator(String message) {
        super(message);
    }

    @Override
    protected void eval() {
        TextInputControl textField = (TextInputControl) this.srcControl.get();
        String text = textField.getText();

        try {
            this.hasErrors.set(false);
            int val = Integer.parseInt(text);
            if (val <= 0)
                throw new Exception();
        } catch (Exception e) {
            this.hasErrors.set(true);
        }
    }

}
