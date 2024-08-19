import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

class StateMachine {
    private String currentState;
    private final Map<String, List<Transition>> transitions;
    private final Map<String, List<Runnable>> actions;

    public StateMachine(Map<String, List<Runnable>> states, List<Transition> transitions, Map<String, List<Runnable>> actions) {
        this.transitions = new HashMap<>();
        this.actions = actions;

        // Initialize the state machine with the first state
        this.currentState = states.keySet().iterator().next();

        // Organize transitions by source state
        for (Transition transition : transitions) {
            this.transitions
                .computeIfAbsent(transition.getSourceState(), k -> new ArrayList<>())
                .add(transition);
        }
    }

    public void handleEvent(String event) {
        List<Transition> availableTransitions = transitions.get(currentState);
        if (availableTransitions != null) {
            for (Transition transition : availableTransitions) {
                if (transition.getEvent().equals(event)) {
                    transitionTo(transition.getTargetState());
                    break;
                }
            }
        }
    }

    private void transitionTo(String newState) {
        System.out.println("Transitioning from " + currentState + " to " + newState);
        currentState = newState;
        executeActions(newState);
    }

    private void executeActions(String state) {
        List<Runnable> stateActions = actions.get(state);
        if (stateActions != null) {
            for (Runnable action : stateActions) {
                action.run();
            }
        }
    }
}

class Transition {
    private final String sourceState;
    private final String event;
    private final String targetState;

    public Transition(String sourceState, String event, String targetState) {
        this.sourceState = sourceState;
        this.event = event;
        this.targetState = targetState;
    }

    public String getSourceState() {
        return sourceState;
    }

    public String getEvent() {
        return event;
    }

    public String getTargetState() {
        return targetState;
    }
}

class EventBus {
    private final Map<String, List<Consumer<String>>> subscribers = new HashMap<>();

    public void subscribe(String event, Consumer<String> callback) {
        subscribers
            .computeIfAbsent(event, k -> new ArrayList<>())
            .add(callback);
    }

    public void publish(String event) {
        System.out.println("Event published: " + event);
        List<Consumer<String>> eventSubscribers = subscribers.get(event);
        if (eventSubscribers != null) {
            for (Consumer<String> subscriber : eventSubscribers) {
                subscriber.accept(event);
            }
        }
    }
}

public class RailroadCrossingControl {
    private static EventBus eventBus = new EventBus();

    public static void callFunction(String action) {
        System.out.println("Executing action: " + action);
    }

    public static void raiseEvent(String event) {
        eventBus.publish(event);
    }

    public static void main(String[] args) {
        // Controller State Machine
        Map<String, List<Runnable>> controllerStates = new HashMap<>();
        controllerStates.put("away", null);
        controllerStates.put("approach", null);
        controllerStates.put("close", null);
        controllerStates.put("present", null);
        controllerStates.put("leaving", null);
        controllerStates.put("left", null);

        List<Transition> controllerTransitions = List.of(
            new Transition("away", "seen", "approach"),
            new Transition("approach", "¬seen", "close"),
            new Transition("close", "seen", "present"),
            new Transition("present", "¬seen", "leaving"),
            new Transition("leaving", "¬seen", "left"),
            new Transition("left", "seen", "approach")
        );

        Map<String, List<Runnable>> controllerActions = new HashMap<>();
        controllerActions.put("approach", List.of(() -> raiseEvent("approaching")));
        controllerActions.put("leaving", List.of(() -> raiseEvent("leaving")));

        StateMachine controllerSM = new StateMachine(controllerStates, controllerTransitions, controllerActions);

        // Gate State Machine
        Map<String, List<Runnable>> gateStates = new HashMap<>();
        gateStates.put("up", null);
        gateStates.put("down", null);

        List<Transition> gateTransitions = List.of(
            new Transition("up", "approaching", "down"),
            new Transition("down", "leaving", "up")
        );

        Map<String, List<Runnable>> gateActions = new HashMap<>();
        gateActions.put("down", List.of(() -> callFunction("call down"))); // Lower the gate
        gateActions.put("up", List.of(() -> callFunction("call up")));      // Raise the gate

        StateMachine gateSM = new StateMachine(gateStates, gateTransitions, gateActions);

        // Light State Machine
        Map<String, List<Runnable>> lightStates = new HashMap<>();
        lightStates.put("off", null);
        lightStates.put("on", null);

        List<Transition> lightTransitions = List.of(
            new Transition("off", "approaching", "on"),
            new Transition("on", "leaving", "off")
        );

        Map<String, List<Runnable>> lightActions = new HashMap<>();
        lightActions.put("on", List.of(() -> callFunction("call on")));  // Turn the light on
        lightActions.put("off", List.of(() -> callFunction("call off"))); // Turn the light off

        StateMachine lightSM = new StateMachine(lightStates, lightTransitions, lightActions);

        // Register state machines with the event bus
        eventBus.subscribe("seen", controllerSM::handleEvent);
        eventBus.subscribe("¬seen", controllerSM::handleEvent);
        eventBus.subscribe("approaching", gateSM::handleEvent);
        eventBus.subscribe("leaving", gateSM::handleEvent);
        eventBus.subscribe("approaching", lightSM::handleEvent);
        eventBus.subscribe("leaving", lightSM::handleEvent);

        // Simulate events
        raiseEvent("seen");   // Simulate a train being detected
        raiseEvent("¬seen");  // Simulate the train moving past the sensor
    }
}
