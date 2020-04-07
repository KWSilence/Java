package labs_4_5.productsBD;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.*;
import java.util.ArrayList;
import java.util.Random;

public class BDManager
{
  private static BDManager bdManager = null;
  private Connection connection = null;
  private Statement statement = null;
  private ArrayList<PreparedStatement> prStatements;
  private ArrayList<ResultSet> resultSets;
  private PreparedStatement titleStatement = null;
  private PreparedStatement prodidStatement = null;

  private BDManager()
  {
    try
    {
      String driver = "com.mysql.cj.jdbc.Driver";
      Class.forName(driver).getDeclaredConstructor().newInstance();
      String url = "jdbc:mysql://localhost/products?serverTimezone=UTC";
      String username = "KWS";
      String password = "KWSilenceSQL";
      connection = DriverManager.getConnection(url, username, password);
      statement = connection.createStatement();
      prStatements = new ArrayList<>();
      resultSets = new ArrayList<>();
    }
    catch (Exception e)
    {
      System.out.println("Connection failed...");
      System.out.println(e);
    }
  }

  public static BDManager getInstance()
  {
    if (bdManager == null)
    {
      bdManager = new BDManager();
    }
    return bdManager;
  }

  public void initTable(int n)
  {
    try
    {
      statement.executeUpdate("DELETE FROM prodlist;");
      statement.execute("ALTER TABLE prodlist AUTO_INCREMENT = 1;");
      PreparedStatement prStatement = getPrStatement("INSERT INTO prodlist(prodid, title, cost) values(?, ?, ?);");
      String title;
      int cost;
      Random random = new Random();
      for (int i = 0; i < n; i++)
      {
        title = "product" + i;
        cost = random.nextInt(100000) + 1;
        String code = "";
        for (int j = 0; j < 13; j++)
        {
          code += random.nextInt(10);
        }
        prStatement.setBigDecimal(1, new BigDecimal(code));
        prStatement.setString(2, title);
        prStatement.setInt(3, cost);
        prStatement.executeUpdate();
        prStatement.clearParameters();
      }
      System.out.println("Init successful!");
    }
    catch (Exception e)
    {
      System.out.println("Init failed...");
      System.out.println(e);
    }
  }

  public Connection getConnection()
  {
    return connection;
  }

  public Statement getStatement()
  {
    return statement;
  }

  public void execute(String sql)
  {
    try
    {
      statement.executeUpdate(sql);
    }
    catch (Exception e)
    {
      System.out.println("Update failed...");
      System.out.println(e);
    }
  }

  public PreparedStatement getPrStatement(String sql)
  {
    PreparedStatement prStatement = null;
    try
    {
      prStatement = connection.prepareStatement(sql);
      prStatements.add(prStatement);
    }
    catch (Exception e)
    {
      System.out.println("PrStatement failed...");
      System.out.println(e);
    }

    return prStatement;
  }

  public ResultSet getResultSet(String sql)
  {
    ResultSet resultSet = null;
    try
    {
      resultSet = statement.executeQuery(sql);
      resultSets.add(resultSet);
    }
    catch (Exception e)
    {
      System.out.println("ResultSet failed...");
      System.out.println(e);
    }
    return resultSet;
  }

  public ResultSet getResultSet(PreparedStatement prStatement)
  {
    ResultSet resultSet = null;
    try
    {
      resultSet = prStatement.executeQuery();
      resultSets.add(resultSet);
    }
    catch (Exception e)
    {
      System.out.println("ResultSet failed...");
      System.out.println(e);
    }
    return resultSet;
  }

  public boolean resultSetIsEmpty(ResultSet resultSet)
  {
    boolean isEmpty = false;
    try
    {
      isEmpty = !resultSet.isBeforeFirst();
    }
    catch (Exception e)
    {
      System.out.println("Checking ResultSet failed...");
      System.out.println(e);
    }
    return isEmpty;
  }

  public boolean titleIsExist(String title)
  {
    boolean exist = false;
    ResultSet resultSet = null;
    try
    {
      if (titleStatement == null)
      {
        titleStatement = connection.prepareStatement("SELECT id FROM prodlist where title = ?;");
      }
      titleStatement.setString(1, title);
      resultSet = titleStatement.executeQuery();
      exist = resultSet.isBeforeFirst();
      resultSet.close();
      titleStatement.clearParameters();
    }
    catch (Exception e)
    {
      System.out.println("Checking title failed...");
      System.out.println(e);
    }
    return exist;
  }

  public boolean prodidIsExist(BigInteger prodid)
  {
    boolean exist = false;
    ResultSet resultSet = null;
    try
    {
      if (prodidStatement == null)
      {
        prodidStatement = connection.prepareStatement("SELECT id FROM prodlist where prodid = ?;");
      }
      prodidStatement.setBigDecimal(1, new BigDecimal(prodid));
      resultSet = prodidStatement.executeQuery();
      exist = resultSet.isBeforeFirst();
      resultSet.close();
      prodidStatement.clearParameters();
    }
    catch (Exception e)
    {
      System.out.println("Checking prodid failed...");
      System.out.println(e);
    }
    return exist;
  }

  public void disconnect()
  {
    try
    {
      if ((connection != null))
      {
        connection.close();
      }
      if (connection != null)
      {
        connection.close();
      }
      if (statement != null)
      {
        statement.close();
      }
      if (titleStatement != null)
      {
        titleStatement.close();
      }
      if (prodidStatement != null)
      {
        prodidStatement.close();
      }
      for (ResultSet resultSet : resultSets)
      {
        if (resultSet != null)
        {
          resultSet.close();
        }
      }
      for (PreparedStatement prStatement : prStatements)
      {
        if (prStatement != null)
        {
          prStatement.close();
        }
      }
      prStatements.clear();
      resultSets.clear();
      bdManager = null;
    }
    catch (Exception e)
    {
      System.out.println("Disconnection failed...");
      System.out.println(e);
    }
  }
}
