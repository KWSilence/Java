package lab3_concurrency.hydro_system;

import java.util.HashMap;
import java.util.concurrent.*;

public class StudentsQueue extends Thread
{
  private int size_;
  private CopyOnWriteArrayList<Student> students_;
  private HashMap<String, Robot> robots_;
  private StudentGenerator generator_;

  public StudentsQueue(int size)
  {
    students_ = new CopyOnWriteArrayList<>();
    robots_ = new HashMap<>();
    size_ = size;
  }

  public void setGenerator(StudentGenerator generator)
  {
    generator_ = generator;
  }

  public void addRobots(Robot[] robots)
  {
    for (Robot robot : robots)
    {
      robots_.put(robot.getSubjectName(), robot);
    }
  }

  void addStudent(Student student)
  {
    students_.add(student);
  }

  private Student getStudent()
  {
    if (students_.isEmpty())
    {
      return null;
    }
    Student student = students_.get(0);
    students_.remove(0);
    return student;
  }

  CopyOnWriteArrayList<Student> getStudents()
  {
    return students_;
  }

  private String firstStudentsSubject()
  {
    if (students_.isEmpty())
    {
      return "";
    }
    return students_.get(0).getSubjectName(false);
  }

  private boolean isEmpty()
  {
    return students_.isEmpty();
  }

  boolean isFull()
  {
    return (students_.size() == size_);
  }

  @Override
  public void run()
  {
    while (!Thread.interrupted())
    {
      if (!isEmpty())
      {
        String studentSubject = firstStudentsSubject();

        if (!robots_.containsKey(studentSubject))
        {
          System.out.println("ERROR!!!  " + studentSubject);
        }

        Robot robot = robots_.get(studentSubject);
        if (robot.isAlive())
        {
          try
          {
            robot.join();
          } catch (InterruptedException e)
          {
            break;
          }
          robots_.replace(studentSubject, robot, new Robot(studentSubject));
        }
        else
        {
          if (robot.wasWork())
          {
            robots_.replace(studentSubject, robot, new Robot(studentSubject));
          }
        }
        Robot workingRobot = robots_.get(studentSubject);
        workingRobot.setStudent(getStudent());
        workingRobot.start();

        synchronized (generator_)
        {
          generator_.notify();
        }
      }
    }
    students_.clear();
    Robot.printTime(Robot.ANSI_RED + "Queue interrupted" + Robot.ANSI_RESET);
  }
}
