package lab3_concurrency.hydro_system;

public class StudentGenerator extends Thread
{
  private int[] labsCounts_;
  private String[] subjectsName_;
  private StudentsQueue queue_;
  private static int time_;

  public StudentGenerator(int[] counts, String[] names, StudentsQueue queue)
  {
    labsCounts_ = counts;
    subjectsName_ = names;
    queue_ = queue;
  }

  public static void setTime(int time)
  {
    time_ = time;
  }

  private int getRandom(boolean isCount)
  {
    if (isCount)
    {
      return ((int) (Math.random() * labsCounts_.length));
    }
    return ((int) (Math.random() * subjectsName_.length));
  }

  private Student generateStudent()
  {
    return (new Student(labsCounts_[getRandom(true)], subjectsName_[getRandom(false)]));
  }

  @Override
  public void run()
  {
    while (!Thread.interrupted())
    {
      try
      {
        Thread.sleep(time_);
      } catch (InterruptedException e)
      {
        break;
      }
      if (!queue_.isFull())
      {
        StringBuilder s = new StringBuilder("Queue: ");
        Student newStudent = generateStudent();
        for (Student student : queue_.getStudents())
        {
          s.append("[").append(student.getSubjectName(true)).append(", ").append(student.getLabsCount()).append("] ");
        }
        s.append("[").append(newStudent.getSubjectName(true)).append(", ").append(newStudent.getLabsCount()).append("]");
        Robot.printTime(s.toString());
        queue_.addStudent(newStudent);
      }
      else
      {
        try
        {

          synchronized (this)
          {
            Robot.printTime(Robot.ANSI_CYAN + "Generator wait" + Robot.ANSI_RESET);
            wait();
          }

        } catch (InterruptedException e)
        {
          break;
        }
      }
    }
    Robot.printTime(Robot.ANSI_RED + "Generator interrupted" + Robot.ANSI_RESET);
  }
}
