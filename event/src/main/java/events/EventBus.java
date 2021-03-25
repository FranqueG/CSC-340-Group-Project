package events;


import annotations.Event;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class EventBus {
    static Map<String, Map<Object, List<Method>>> eventMethods = new HashMap<>();


    static void register(Object _owningObj, List<Method> _eventMethods) {
        for (var method : _eventMethods) {
            var annotation = method.getAnnotation(Event.class);
            var eventName = annotation.name().equals("") ? method.getName() : annotation.name();
            if (eventMethods.containsKey(eventName)) {
                var objMethodMap = eventMethods.get(eventName);
                if (objMethodMap.containsKey(_owningObj))
                    objMethodMap.get(_owningObj).add(method);
                else
                    objMethodMap.put(_owningObj, new ArrayList<>(Collections.singletonList(method)));
            } else {
                var newMap = new HashMap<Object, List<Method>>();
                newMap.put(_owningObj, new ArrayList<>(Collections.singletonList(method)));
                eventMethods.put(eventName, newMap);
            }
        }
        ;
    }

    public static void registerStaticClass(Class<?> _class) {
        var methods = _class.getDeclaredMethods();

    }

    public static void broadcastEvent(EventData _eventData) {
        if (!eventMethods.containsKey(_eventData.eventName)) {
            System.err.println("Warning: attempted to broadcast event with no handlers");
        }

        var methodsMap = (HashMap<Object, List<Method>>) eventMethods.get(_eventData.eventName);
        try {
            for (var objMethodPair : methodsMap.entrySet()) {
                var obj = objMethodPair.getKey();
                for (var method : objMethodPair.getValue()) {
                    method.setAccessible(true);
                    if (method.getParameterCount() == 0)
                        method.invoke(obj);
                    else
                        method.invoke(obj, _eventData);
                }
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            System.err.println("Failed to invoke event method: ");
            e.printStackTrace();
        }
    }
}
