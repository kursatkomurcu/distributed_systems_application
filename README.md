# **Distributed State Machine for Railroad Crossing Control**

## **Overview**

This project implements a distributed state machine model in Java to control a railroad crossing system. The system manages the gates and lights at the crossing in response to the detection and movement of trains. The design uses three interacting state machines: the Controller State Machine, Gate State Machine, and Light State Machine. These state machines communicate through an event-driven architecture facilitated by an `EventBus` to ensure that the gates and lights operate correctly as a train approaches and departs.

## **Project Structure**

- **`StateMachine` Class**: A generic state machine class that can be used to model any system with states, transitions, and actions.
- **`EventBus` Class**: Manages the event-driven communication between state machines.
- **Controller State Machine**: Tracks the position of the train and raises events to control the gate and light state machines.
- **Gate State Machine**: Controls the position of the crossing gate (up or down) based on the events received from the Controller.
- **Light State Machine**: Controls the crossing lights (on or off) based on the events received from the Controller.

## **Getting Started**

### **Prerequisites**

- **Java Development Kit (JDK) 8 or later**: Ensure that you have JDK installed on your system. You can download it from [Oracle's website](https://www.oracle.com/java/technologies/javase-jdk8-downloads.html) or use a package manager.

## **Result**

Once you run the code, you will see the outpur like this:

```
Event published: seen
Transitioning from away to approach
Event published: approaching
Executing action: call down
Executing action: call on

Event published: ¬seen
Transitioning from approach to close
Transitioning from present to leaving
Event published: leaving
Executing action: call up
Executing action: call off


```
