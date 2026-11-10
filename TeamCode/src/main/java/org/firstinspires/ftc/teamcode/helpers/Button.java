package org.firstinspires.ftc.teamcode.helpers;

import java.util.function.BooleanSupplier;

//public class Button {
//    private boolean lastState = false;
//
//    // Returns true only on the exact frame the button is freshly pressed
//    // I want to murder the founder of First
//    public boolean wasJustPressed(boolean currentState) {
//        boolean justPressed = currentState && !lastState;
//        lastState = currentState;
//        return justPressed;
//    }
//}


public class Button {
    private final BooleanSupplier supplier;
    private boolean lastState = false;

    public Button(BooleanSupplier supplier) {
        this.supplier = supplier;
    }

    public boolean wasJustPressed() {
        boolean currentState = supplier.getAsBoolean();
        boolean justPressed = currentState && !lastState;
        lastState = currentState;
        return justPressed;
    }

    public boolean isHeld() {
        return supplier.getAsBoolean();
    }




// for stuff that can't get direct access to the boolean supplier, 6767
    public static class ButtonSimple {
        private boolean lastState = false;

        public boolean wasJustPressed(boolean currentState) {
            boolean justPressed = currentState && !lastState;
            lastState = currentState;
            return justPressed;
        }

        public boolean isHeld(boolean currentState) {
            return currentState;
        }
    }
}