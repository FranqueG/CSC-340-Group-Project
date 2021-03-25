import annotations.Event;
import events.EventBus;
import events.EventData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class EventDataTest {

    private static class TestClass {
        protected boolean worked = false;

        @Event(name = "test")
        private void event() {
            worked = true;
        }
    }

    private static class TestEventData extends EventData {
        public TestEventData() {
            this.eventName = "test";
        }
    }

    @Test
    @DisplayName("Event system test")
    public void test() {
        var testObj = new TestClass();
        EventBus.register(testObj);
        var event = new TestEventData();
        EventBus.broadcastEvent(event);
    }
}
