package lab3_concurrency.hydro_system;

public class WorkTimer extends Thread
{
  private int minutes_;
  private int seconds_;
  private Thread[] threads_;

  public WorkTimer(int minutes, int seconds)
  {
    minutes_ = minutes;
    seconds_ = seconds;
  }

  public void setThreads(Thread[] threads)
  {
    threads_ = threads;
  }

  @Override
  public void run()
  {
    try
    {
      Thread.sleep(minutes_ * 60000 + seconds_ * 1000);
    } catch (InterruptedException e)
    {
      e.printStackTrace();
    }
    System.out.println(Robot.ANSI_PURPLE + "|Time's up -> " + minutes_ + ":" + seconds_ + "|" + Robot.ANSI_RESET);
    for (Thread thread : threads_)
    {
      thread.interrupt();
    }
  }
}
