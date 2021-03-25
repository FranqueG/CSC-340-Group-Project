import annotations.Event;
import events.EventBus;
import events.EventData;
import events.IEventReceiver;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class EventDataTest {

    private static class TestClass implements IEventReceiver {
        protected boolean worked = false;

        @Event(name = "test")
        private void event() {
            worked = true;
        }
    }

    private static class TestEventData extends EventData {
        TestEventData() {
            eventName = "test";
        }
    }

    @Test
    @DisplayName("Event system test")
    public void test() {
        var testObj = new TestClass();
        testObj.register();
        var event = new TestEventData();
        EventBus.broadcastEvent(event);
    }
}
