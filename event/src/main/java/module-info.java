import events.IEventReceiver;

module  MTG.Deck.Builder.event {
    uses IEventReceiver;
    exports events;
    exports annotations;
}