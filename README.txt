Christopher Semaan 101140813
Filip Lukic 101156713
Stefan Lukic 101156711
Nicolas Tanouchev 101143947
Pranav Hari 101144482

Milestone 4

Files contained:
src/elevatorImpl:
  Constants.java: Constants file
  Elevator.java: Contains client class for Elevator object
  ElevatorStates.java: Enum states class for elevator states
  ElevatorStatus.java: Contains class for Elevator's current status
  ElevatorSubSystem.java: Contains client class for Elevator SubSystem object
  events.txt: text file containing data log
  Floor: Contains client class for Floor object
  Scheduler.java: Contains server class for scheduler
  SchedulerStates.java: Enum states class for scheduler states
  
src/util: 
  FileUtil.java: Class for parsing input file
  NetworkUtils.java: Class for sending, receiving, and handling UDP packets
  SendRecieveUtil.java: Class for sending and receiving data between subsystems
  
src/messages:
  ElevatorData: Message structure used to send data about an elevator
  FloorData: Message structure used to send data about a floor button
  
src/elevatorTests:
  ElevatorSubsystemTests: Contains tests for ElevatorSubsystem class
  ElevatorTests: Contains tests for Elevator class
  events.txt: text file containing data log
  FloorTests: Contains tests for Floor class
  SchedulerTests: Contains tests for Scheduler class

src/diagrams:
  M1_sequence.png - Sequence diagram
  M1_class.png - class diagram
  Iteration2UML.png - Iteration 2 class diagram
  Iteration2Sequence.png - Iteration 2 sequence diagram
  Iteration2State.png - Iteration 2 state diagram
  Iteration3UML.png - Iteration 3 class diagram
  Iteration3Sequence.png - Iteration 3 sequence diagram
  Iteration3State.png - Iteration 3 state diagram

To run:
1. Run the main method found in Scheduler.java
2. Run the main method found in ElevatorSubSystem.java
3. Run the main method found in Floor.java

Task Breakdown for Iteration 3:
Iteration 3 code implementation, debugging and testing: Filip Lukic, Stefan Lukic, Nicolas Tanouchev, Christopher Semaan
Unit testing: Pranav Hari
UML Diagrams: Christopher Semaan

Task Breakdown for Iteration 4:
Iteration 4 code implementation (Implement and handle hard and transient faults): Nicolas Tanouchev, Christopher Semaan
Iteration 4 code implementation (Elevator algorithm and interrupts): Filip Lukic, Stefan Lukic,
Unit testing: Pranav Hari
UML Diagrams: 
