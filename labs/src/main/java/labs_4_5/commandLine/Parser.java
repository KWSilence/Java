package labs_4_5.commandLine;

import labs_4_5.productsBD.BDManager;

import java.util.HashMap;
import java.util.Scanner;

public class Parser extends Thread
{
  private BDManager manager;
  private HashMap<String, BDExecutor> commandMap;

  public Parser(BDManager manager)
  {
    this.manager = manager;
    commandMap = new HashMap<>();
    BDExecutorCollection.setManager(manager);
    commandMap.put("/add", new BDExecutorCollection.BDAdd());
    commandMap.put("/delete", new BDExecutorCollection.BDDelete());
    commandMap.put("/show_all", new BDExecutorCollection.BDShow());
    commandMap.put("/price", new BDExecutorCollection.BDGetPrice());
    commandMap.put("/change_price", new BDExecutorCollection.BDChangePrice());
    commandMap.put("/filter_by_price", new BDExecutorCollection.BDFilterPrice());
  }

  private void showHelp()
  {
    System.out.println("It is help section, {} - comments/type, [] - var:");
    System.out.println("/add {int}[id] {str}[title] {int}[cost] {adding product with this id, title and price}");
    System.out.println("/delete {str}[title] {delete product with this title}");
    System.out.println("/show_all {return all products}");
    System.out.println("/price {str}[title] {return product price}");
    System.out.println("/change_price {str}[title] {int}[cost] {change selected product price}");
    System.out.println("/filter_by_price {int}[cost1] {int}[cost2] {return all element with selected price range}");
    System.out.println("/exit {close program}");
  }

  static String[] getTitle(String data)
  {
    int first = data.indexOf('\"');
    Scanner scanner;
    if (first == -1)
    {
      scanner = new Scanner(data);
      if (scanner.hasNext())
      {
        return (new String[]{scanner.next(), ""});
      }
    }
    else
    {
      if ((first + 1) <= data.length())
      {
        int second = data.indexOf('\"', (first + 1));
        if (second == -1)
        {
          System.out.println("Title has first \'\"\', but has not second");
          return new String[]{"", ""};
        }
        return (new String[]{data.substring((first + 1), second), data.substring((second + 1))});
      }
    }
    return (new String[]{"", ""});
  }

  @Override
  public void run()
  {
    Scanner sysScan = new Scanner(System.in);
    while (true)
    {
      String input = sysScan.nextLine();
      Scanner strScan = new Scanner(input);
      if (!strScan.hasNext())
      {
        continue;
      }

      String command = strScan.next();

      if (command.equals("/exit"))
      {
        break;
      }
      if (command.equals("/help"))
      {
        showHelp();
        continue;
      }

      if (commandMap.containsKey(command))
      {
        if (strScan.hasNext())
        {
          commandMap.get(command).execute(strScan.nextLine());
        }
        else
        {
          commandMap.get(command).execute("");
        }
      }
      else
      {
        System.out.println("Command \'" + command + "\' is not declared {try '/help'}");
      }
    }
    manager.disconnect();
  }
}
