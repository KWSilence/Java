package labs_4_5.productsBD;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ProductDAO /*изначально думал реализовать паттерн DAO, но в целом ничего бы не изменилось,
                   только добавился бы интерфейс(посчитал что нет в этом необходимости)*/
{
  private static ProductDAO products = null;
  private Alert alert;
  private Alert ialert;
  private BDManager manager;
  private PreparedStatement prAdd;
  private PreparedStatement prDelete;
  private PreparedStatement prAll;
  private PreparedStatement prShow;
  private PreparedStatement prChange;
  private PreparedStatement prFilter;

  private ProductDAO()
  {
    alert = new Alert(Alert.AlertType.WARNING);
    alert.setHeaderText(null);
    ialert = new Alert(Alert.AlertType.INFORMATION);
    ialert.setHeaderText(null);
    manager = BDManager.getInstance();
    prAdd = manager.getPrStatement("INSERT INTO prodlist(prodid, title, cost) values(?, ?, ?);");
    prDelete = manager.getPrStatement("DELETE FROM prodlist WHERE title = ?;");
    prAll = manager.getPrStatement("SELECT * FROM prodlist;");
    prShow = manager.getPrStatement("SELECT * FROM prodlist WHERE title = ?;");
    prChange = manager.getPrStatement("UPDATE prodlist SET cost = ? where title = ?;");
    prFilter = manager.getPrStatement("SELECT * FROM prodlist WHERE cost BETWEEN ? AND ?;");
  }

  public void closeProductDAO()
  {
    alert.close();
    ialert.close();
    manager.disconnect();
    products = null;
  }

  public static ProductDAO getInstance() //подумал что одного экземпляра будет достаточно
  {
    if (products == null)
    {
      products = new ProductDAO();
    }
    return products;
  }

  private void showAlert(boolean cond, String method, String field, String content, boolean isWarning) throws Exception
  {
    Alert alert;
    if (cond)
    {
      if (isWarning)
      {
        alert = this.alert;
        alert.setTitle(method + ": " + field + " field wrong");
      }
      else
      {
        alert = this.ialert;
        alert.setTitle(method + ": information");
      }
      alert.setContentText(content);
      alert.showAndWait();
      throw new Exception();
    }
  }

  private ObservableList<Product> getProducts(ResultSet results) throws Exception
  {
    ObservableList<Product> products = FXCollections.observableArrayList();
    Product product;
    while (results.next())
    {
      product = new Product();
      product.setProdid(results.getBigDecimal("prodid").toString());
      product.setTitle(results.getString("title"));
      product.setCost(results.getInt("cost"));
      products.add(product);
    }
    return products;
  }

  public void addProduct(Product product) throws Exception
  {
    BigInteger prodid = product.getProdid();
    String title = product.getTitle();
    Integer cost = product.getCost();

    showAlert((prodid == null), "Add", "Prodid", "Prodid should be number and not empty", true);
    showAlert(manager.prodidIsExist(prodid), "Add", "Prodid", "Product with this prodid already exist", true);
    showAlert((title == null), "Add", "Title", "Title shouldn't be empty", true);
    showAlert(manager.titleIsExist(title), "Add", "Title", "Product with this title already exist", true);
    showAlert((cost == null), "Add", "Cost", "Cost should be number and not empty and symbols count < 10", true);

    prAdd.setBigDecimal(1, new BigDecimal(prodid));
    prAdd.setString(2, title);
    prAdd.setInt(3, cost);
    prAdd.executeUpdate();
    prAdd.clearParameters();
  }

  public void deleteProduct(Product product) throws Exception
  {
    String title = product.getTitle();

    showAlert((title == null), "Delete", "Title", "Title shouldn't be empty", true);
    showAlert(!manager.titleIsExist(title), "Delete", "Title", "Product with this title doesn't exist", true);

    prDelete.setString(1, title);
    prDelete.executeUpdate();
    prDelete.clearParameters();
  }

  public void updateProduct(Product product) throws Exception
  {
    String title = product.getTitle();
    Integer cost = product.getCost();

    showAlert((title == null), "Change", "Title", "Title shouldn't be empty", true);
    showAlert(!manager.titleIsExist(title), "Change", "Title", "Product with this title doesn't exist", true);
    showAlert((cost == null), "Change", "Cost", "Cost should be number and not empty and symbols count < 10", true);

    prChange.setInt(1, cost);
    prChange.setString(2, title);
    prChange.executeUpdate();
    prChange.clearParameters();
  }

  public ObservableList<Product> getAll() throws Exception
  {
    ResultSet results = manager.getResultSet(prAll);
    showAlert(manager.resultSetIsEmpty(results), "ShowAll", "", "Table is empty", false);
    return getProducts(results);
  }

  public ObservableList<Product> getProduct(Product sproduct) throws Exception
  {
    String title = sproduct.getTitle();
    showAlert((title == null), "ShowOne", "Title", "Title shouldn't be empty", true);

    prShow.setString(1, title);
    ResultSet result = manager.getResultSet(prShow);
    prShow.clearParameters();

    showAlert(manager.resultSetIsEmpty(result), "ShowOne", "", "This product doesn't exist", false);
    return getProducts(result);
  }

  public ObservableList<Product> filterProducts(String from, String to) throws Exception
  {
    Product product = new Product(); //лень было снова писать проверку, поэтому запихнул from и to в Product
    product.setCost(from);
    Integer first = product.getCost();
    showAlert((first == null), "ShowFilter", "From", "From should be number and not empty and symbols count < 10", true);

    product = new Product(); //была ошибка, т.к. second записывался в уже существующий, при некорректном значении проходила
    product.setCost(to);
    Integer second = product.getCost();
    showAlert((second == null), "ShowFilter", "To", "To should be number and not empty and symbols count < 10", true);
    showAlert((first > second), "ShowFilter", "From and To", "From should be less, than To", true);

    prFilter.setInt(1, first);
    prFilter.setInt(2, second);
    ResultSet results = manager.getResultSet(prFilter);
    prFilter.clearParameters();

    showAlert(manager.resultSetIsEmpty(results), "ShowFilter", "", "Products in this range of cost don't exist", false);

    return getProducts(results);
  }
}
