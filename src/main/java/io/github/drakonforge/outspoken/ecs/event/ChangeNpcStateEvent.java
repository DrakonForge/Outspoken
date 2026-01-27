package io.github.drakonforge.outspoken.ecs.event;

import com.hypixel.hytale.component.system.EcsEvent;

public class ChangeNpcStateEvent extends EcsEvent {
    private final String prevState;
    private final String currentState;

    public ChangeNpcStateEvent(String prevState, String currentState) {
        this.prevState = prevState;
        this.currentState = currentState;
    }

    public String getPrevState() {
        return prevState;
    }

    public String getCurrentState() {
        return currentState;
    }
}
