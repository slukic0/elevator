Christopher Semaan 
Filip Lukic
Stefan Lukic
Nicolas Tanouchev
Pranav Hari

Milestone 1

Files contained:
src/elevator: Elevator.java, Floor.java, Scheduler.java, Message.java, Main.java
  Scheduler: Contains server class for scheduler
  Elevator: Contains client class for Elevator object
  ElevatorSubSystem: Contains client class for Elevator SubSystem object
  ElevatorData: Message structure used to send data about an elevator
  Floor: Contains client class for Floor object
  FloorData: Message structure used to send data about a floor button
  Message: Contains class for message data structure
  Main: Contains main, initializes all objects
  Constants: Constants file
  events.txt: text file containing data log
  
src/util: 
  FileUtil.java: Class for parsing input file
  SendRecieveUtil.java: Class for sending and receiving data between subsystems
  
src/elevatorTests: SchedulerTests.java, ElevatorTests.java, FloorTests.java, events.txt
  SchedulerTests: Contains tests for Scheduler class
  ElevatorTests: Contains tests for Elevator class
  FloorTests: Contains tests for Floor class
  events: text file containing data log

src/diagrams:
  M1_sequence.png - Sequence diagram
  M1_class.png - class diagram

To run:
Open elevator project in eclipse. Run with configuration.
