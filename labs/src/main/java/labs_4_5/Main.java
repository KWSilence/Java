package labs_4_5;

import labs_4_5.commandLine.Parser;
import labs_4_5.productsBD.BDManager;

public class Main
{
  public static void main(String[] args)
  {
    BDManager manager = BDManager.getInstance();
    manager.initTable(10);
    Parser parser = new Parser(manager);
    parser.start();
  }
}
