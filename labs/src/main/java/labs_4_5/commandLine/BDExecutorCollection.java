package labs_4_5.commandLine;

import labs_4_5.productsBD.BDManager;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Scanner;

class BDExecutorCollection
{
  private static final String AddSQL = "INSERT INTO prodlist(prodid, title, cost) values(?, ?, ?);";
  private static final String DeleteSQL = "DELETE FROM prodlist WHERE title = ?;";
  private static final String ShowSQL = "SELECT * FROM prodlist;";
  private static final String GetPriceSQL = "SELECT cost FROM prodlist WHERE title = ?;";
  private static final String ChangePriceSQL = "UPDATE prodlist SET cost = ? where title = ?;";
  private static final String FilterByPriceSQL = "SELECT * FROM prodlist WHERE cost BETWEEN ? AND ?;";

  private static BDManager manager = null;

  static void setManager(BDManager bdmanager)
  {
    manager = bdmanager;
  }


  public static class BDAdd implements BDExecutor
  {
    private PreparedStatement prStatement;

    BDAdd()
    {
      prStatement = manager.getPrStatement(AddSQL);
    }

    @Override
    public void execute(String data)
    {
      Scanner scanner;
      BigInteger prodid;
      int cost;

      scanner = new Scanner(data);
      if (scanner.hasNextBigInteger())
      {
        prodid = scanner.nextBigInteger();
      }
      else
      {
        System.out.println("Add: 1 param should be product id number{int}");
        return;
      }

      if (manager.prodidIsExist(prodid))
      {
        System.out.println("Sorry you can not add exiting product (product id)");
        return;
      }

      data = scanner.hasNext() ? scanner.nextLine() : "";

      String[] parserRes = Parser.getTitle(data);
      String title = parserRes[0];
      if (title.isEmpty())
      {
        System.out.println("Add: 2 param should be title name {String}");
        return;
      }

      if (parserRes[1].isEmpty())
      {
        scanner = new Scanner(data);
        scanner.next();
      }
      else
      {
        scanner = new Scanner(parserRes[1]);
      }

      if (manager.titleIsExist(title))
      {
        System.out.println("Sorry you can not add exiting product (title)");
        return;
      }

      if (scanner.hasNextInt())
      {
        cost = scanner.nextInt();
      }
      else
      {
        System.out.println("Add: 3 param should be product cost {int}");
        return;
      }

      try
      {
        prStatement.setBigDecimal(1, new BigDecimal(prodid));
        prStatement.setString(2, title);
        prStatement.setInt(3, cost);
        prStatement.executeUpdate();
        prStatement.clearParameters();
      }
      catch (Exception e)
      {
        System.out.println("Adding failed...");
        System.out.println(e);
      }
    }
  }

  public static class BDDelete implements BDExecutor
  {
    private PreparedStatement prStatement;

    BDDelete()
    {
      prStatement = manager.getPrStatement(DeleteSQL);
    }

    @Override
    public void execute(String data)
    {
      String[] parserRes = Parser.getTitle(data);
      String title = parserRes[0];
      if (title.isEmpty())
      {
        System.out.println("Delete: 1 param should be title name {String}");
        return;
      }

      if (!manager.titleIsExist(title))
      {
        System.out.println("Sorry you can not delete not exiting product");
        return;
      }
      try
      {
        prStatement.setString(1, title);
        prStatement.executeUpdate();
        prStatement.clearParameters();
      }
      catch (Exception e)
      {
        System.out.println("Deleting failed...");
        System.out.println(e);
      }
    }
  }

  public static class BDShow implements BDExecutor
  {
    private PreparedStatement prStatement;

    BDShow()
    {
      prStatement = manager.getPrStatement(ShowSQL);
    }

    @Override
    public void execute(String data)
    {
      ResultSet resultSet;
      try
      {
        resultSet = prStatement.executeQuery();
        if (!resultSet.isBeforeFirst())
        {
          System.out.println("table is empty");
        }

        while (resultSet.next())
        {
          int id = resultSet.getInt("id");
          BigDecimal prodid = resultSet.getBigDecimal("prodid");
          String title = resultSet.getString("title");
          int cost = resultSet.getInt("cost");
          System.out.println("id = " + id + "; prodid = " + prodid + "; title = \"" + title + "\"; cost = " + cost);
        }

        resultSet.close();
      }
      catch (Exception e)
      {
        System.out.println("Show failed...");
        System.out.println(e);
      }
    }
  }

  public static class BDGetPrice implements BDExecutor
  {
    private PreparedStatement prStatement;

    BDGetPrice()
    {
      prStatement = manager.getPrStatement(GetPriceSQL);
    }

    @Override
    public void execute(String data)
    {
      String[] parserRes = Parser.getTitle(data);
      String title = parserRes[0];
      if (title.isEmpty())
      {
        System.out.println("GetPrice: 1 param should be title name {String}");
        return;
      }

      if (!manager.titleIsExist(title))
      {
        System.out.println("Sorry you can not get price of not exiting product");
        return;
      }

      ResultSet resultSet;
      try
      {
        prStatement.setString(1, title);
        resultSet = prStatement.executeQuery();
        prStatement.clearParameters();
        resultSet.next();
        System.out.println("cost = " + resultSet.getInt("cost"));
        resultSet.close();
      }
      catch (Exception e)
      {
        System.out.println("Getting price failed...");
        System.out.println(e);
      }
    }
  }

  public static class BDChangePrice implements BDExecutor
  {
    private PreparedStatement prStatement;

    BDChangePrice()
    {
      prStatement = manager.getPrStatement(ChangePriceSQL);
    }

    @Override
    public void execute(String data)
    {
      int cost;
      String[] parserRes = Parser.getTitle(data);
      String title = parserRes[0];
      if (title.isEmpty())
      {
        System.out.println("ChangePrice: 1 param should be title name {String}");
        return;
      }

      Scanner scanner;
      if (parserRes[1].isEmpty())
      {
        scanner = new Scanner(data);
        scanner.next();
      }
      else
      {
        scanner = new Scanner(parserRes[1]);
      }

      if (!manager.titleIsExist(title))
      {
        System.out.println("Sorry you can not change price of not exiting product");
        return;
      }

      if (scanner.hasNextInt())
      {
        cost = scanner.nextInt();
      }
      else
      {
        System.out.println("ChangePrice: 2 param should be product cost {int}");
        return;
      }

      try
      {
        prStatement.setInt(1, cost);
        prStatement.setString(2, title);
        prStatement.executeUpdate();
        prStatement.clearParameters();
      }
      catch (Exception e)
      {
        System.out.println("Changing price failed...");
        System.out.println(e);
      }
    }
  }

  public static class BDFilterPrice implements BDExecutor
  {
    private PreparedStatement prStatement;

    BDFilterPrice()
    {
      prStatement = manager.getPrStatement(FilterByPriceSQL);
    }

    @Override
    public void execute(String data)
    {
      Scanner scanner = new Scanner(data);
      int first;
      int second;

      if (scanner.hasNextInt())
      {
        first = scanner.nextInt();
      }
      else
      {
        System.out.println("FilterPrice: 1 param should be price {int}");
        return;
      }

      if (scanner.hasNextInt())
      {
        second = scanner.nextInt();
      }
      else
      {
        System.out.println("FilterPrice: 2 param should be price {int}");
        return;
      }

      if (first > second)
      {
        System.out.println("FilterPrice: 1 value should be less 2 value");
        return;
      }

      ResultSet resultSet;
      try
      {
        prStatement.setInt(1, first);
        prStatement.setInt(2, second);
        resultSet = prStatement.executeQuery();
        prStatement.clearParameters();
        if (!resultSet.isBeforeFirst())
        {
          System.out.println("No records with price from " + first + " to " + second);
        }

        while (resultSet.next())
        {
          int id = resultSet.getInt("id");
          String title = resultSet.getString("title");
          int cost = resultSet.getInt("cost");
          System.out.println("id = " + id + "; title = \"" + title + "\"; cost = " + cost);
        }

        resultSet.close();
      }
      catch (Exception e)
      {
        System.out.println("Show failed...");
        System.out.println(e);
      }
    }
  }
}
