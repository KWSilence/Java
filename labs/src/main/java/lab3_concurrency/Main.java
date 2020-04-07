package lab3_concurrency;

import lab3_concurrency.hydro_system.Robot;
import lab3_concurrency.hydro_system.StudentGenerator;
import lab3_concurrency.hydro_system.StudentsQueue;
import lab3_concurrency.hydro_system.WorkTimer;

import java.time.LocalTime;

public class Main
{
  public static LocalTime startTime;

  public static void main(String[] args)
  {
    startTime = LocalTime.now();
    StudentsQueue queue = new StudentsQueue(10);

    String oop = "OOP";
    String physics = "Physics";
    String math = "Math";

    StudentGenerator.setTime(500);
    StudentGenerator generator = new StudentGenerator(new int[]{10, 20, 100}, new String[]{oop, physics, math}, queue);
    queue.setGenerator(generator);

    Robot.setTime(1000);
    Robot.setLabs(5);
    Robot robotOOP = new Robot(oop);
    Robot robotP = new Robot(physics);
    Robot robotHM = new Robot(math);
    Robot[] robots = {robotOOP, robotP, robotHM};

    queue.addRobots(robots);

    Thread[] threads = {generator, queue};
    WorkTimer timer = new WorkTimer(5, 40);
    timer.setThreads(threads);

    generator.start();
    queue.start();
    timer.start();
  }
}
