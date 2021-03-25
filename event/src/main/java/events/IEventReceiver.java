package events;

import annotations.Event;

import java.lang.reflect.Method;
import java.util.ArrayList;

public interface IEventReceiver {

    default void register() {
        var methods = this.getClass().getDeclaredMethods();
        var foundMethods = new ArrayList<Method>();
        for (var method : methods) {
            if (method.getAnnotation(Event.class) != null)
                foundMethods.add(method);
        }
        EventBus.register(this,foundMethods);
    }
}
