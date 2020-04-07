package lab3_concurrency.hydro_system;


class Student
{
  private int labsCount_;
  private String subjectName_;

  Student(int labsCount, String subjectName)
  {
    labsCount_ = labsCount;
    subjectName_ = subjectName;
  }

  int getLabsCount()
  {
    return labsCount_;
  }

  String getSubjectName(boolean isShort)
  {
    if (isShort)
    {
      return subjectName_.substring(0, 1);
    }
    return subjectName_;
  }

  boolean decreaseLabsCount(int num)
  {
    boolean success = (labsCount_ > 0);
    labsCount_ -= num;
    return success;
  }
}
