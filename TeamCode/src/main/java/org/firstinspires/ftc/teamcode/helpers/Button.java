package org.firstinspires.ftc.teamcode.helpers;

public class Button {
    private boolean lastState = false;

    // Returns true only on the exact frame the button is freshly pressed
    // I want to murder the founder of First
    public boolean wasJustPressed(boolean currentState) {
        boolean justPressed = currentState && !lastState;
        lastState = currentState;
        return justPressed;
    }
}