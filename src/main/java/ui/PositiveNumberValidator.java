package ui;

class PositiveNumberValidator extends ValidatorBaseAdvanced {

    public PositiveNumberValidator(String message) {
        super(message);
    }

    @Override
    boolean valid(String text) {
        try {
            int val = Integer.parseInt(text);
            if (val <= 0)
                throw new Exception();
        } catch (Exception e) {
            return false;
        }
        return true;
    }

}
