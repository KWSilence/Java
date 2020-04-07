package labs_4_5.productsBD;

import java.math.BigInteger;

public class Product
{
  private BigInteger prodid;
  private String title;
  private Integer cost;

  public Product()
  {
    this.prodid = null;
    this.title = null;
    this.cost = null;
  }

  public Product(String prodid, String title, Integer cost)
  {
    this.prodid = new BigInteger(prodid);
    this.title = title;
    this.cost = cost;
  }

  public BigInteger getProdid()
  {
    return prodid;
  }

  public String getTitle()
  {
    return title;
  }

  public Integer getCost()
  {
    return cost;
  }

  public void setProdid(String prodid) //т.к. TextField возвращает строку; заодно здесь проверяются разичные условия
  {
    if (prodid.matches("[0-9]+") && !prodid.isEmpty())
    {
      this.prodid = new BigInteger(prodid);
    }
  }

  public void setTitle(String title)
  {
    title = title.trim();
    if (!title.isEmpty())
    {
      this.title = title;
    }
  }

  public void setCost(String cost) //я не думаю что будет ценник на продукт > 2 млрд
  {
    if (cost.matches("[0-9]+") && (!cost.isEmpty()) && (cost.length() < 10))
    {
      this.cost = new Integer(cost);
    }
  }

  public void setProdid(BigInteger prodid)
  {
    this.prodid = prodid;
  }

  public void setCost(Integer cost)
  {
    this.cost = cost;
  }
}