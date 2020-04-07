package lab3_concurrency.hydro_system;

import lab3_concurrency.Main;

import java.time.*;

public class Robot extends Thread
{
  static final String ANSI_RESET = "\u001B[0m";
  static final String ANSI_PURPLE = "\u001B[35m";
  static final String ANSI_RED = "\u001B[31m";
  static final String ANSI_CYAN = "\u001B[36m";
  private static final String ANSI_GREEN = "\u001B[32m";
  private static final String ANSI_YELLOW = "\u001B[33m";
  private static final String ANSI_BLUE = "\u001B[34m";
  private static final String ANSI_BOLD_BLUE = "\033[1;34m";

  private static int labsAccept_;
  private static int time_;
  private String subjectName_;
  private boolean work_;
  private Student currentStudent_;

  public Robot(String subjectName)
  {
    subjectName_ = subjectName;
    currentStudent_ = null;
    work_ = false;
  }

  private static LocalTime getTimeFromStart()
  {
    LocalTime time = LocalTime.now();
//    time = time.minusHours(Main.startTime.getHour());
    time = time.minusMinutes(Main.startTime.getMinute());
    time = time.minusSeconds(Main.startTime.getSecond());
    return time;
  }

  static void printTime(String s)
  {
    LocalTime time = getTimeFromStart();
    System.out.println(ANSI_YELLOW + "" + time.getMinute() + ":" + time.getSecond() + " " + ANSI_RESET + s);
  }

  public static void setTime(int time)
  {
    time_ = time;
  }

  public static void setLabs(int labs)
  {
    labsAccept_ = labs;
  }

  void setStudent(Student student)
  {
    currentStudent_ = student;
  }

  String getSubjectName()
  {
    return subjectName_;
  }

  boolean wasWork()
  {
    return work_;
  }

  @Override
  public void run()
  {
    work_ = true;
    int allLabs = currentStudent_.getLabsCount();
    printTime(ANSI_BOLD_BLUE + subjectName_ + ": student with [0/" + allLabs + "] labs working" + ANSI_RESET);
    int i = 0;
    while (currentStudent_.decreaseLabsCount(labsAccept_))
    {
      try
      {
        Thread.sleep(time_);
      } catch (InterruptedException e)
      {
        e.printStackTrace();
      }
      i += labsAccept_;
      printTime(ANSI_BLUE + subjectName_ + ": student with [" + i + "/" + allLabs + "] labs working" + ANSI_RESET);
    }
    currentStudent_ = null;
    printTime(ANSI_GREEN + subjectName_ + ": student labs [" + allLabs + "] accepted" + ANSI_RESET);
  }
}
