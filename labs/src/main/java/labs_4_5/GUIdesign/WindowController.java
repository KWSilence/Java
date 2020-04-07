package labs_4_5.GUIdesign;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import labs_4_5.productsBD.BDManager;
import labs_4_5.productsBD.Product;
import labs_4_5.productsBD.ProductDAO;

import java.math.BigInteger;
import java.net.URL;
import java.util.ResourceBundle;

public class WindowController implements Initializable
{
  @FXML
  private TableView<Product> prodTable;
  @FXML
  private TableColumn<Product, BigInteger> prodidColumn;
  @FXML
  private TableColumn<Product, String> titleColumn;
  @FXML
  private TableColumn<Product, Integer> costColumn;

  @FXML
  private TabPane tabPane;
  @FXML
  private Label successUpdate;

  @FXML
  private ToggleGroup updateGroup;
  @FXML
  private GridPane updateGrid;

  @FXML
  private ToggleGroup showGroup;
  @FXML
  private GridPane showOne;
  @FXML
  private GridPane showFilter;

  private ProductDAO products;

  @Override
  public void initialize(URL location, ResourceBundle resources) //привязка таблицы полями класса и подключение к бд
  {
    prodidColumn.setCellValueFactory(new PropertyValueFactory<Product, BigInteger>("prodid"));
    titleColumn.setCellValueFactory(new PropertyValueFactory<Product, String>("title"));
    costColumn.setCellValueFactory(new PropertyValueFactory<Product, Integer>("cost"));
    products = ProductDAO.getInstance();
    BDManager.getInstance().initTable(10); /*10 рандомных записей(кроме поля title),
                                                 конечно там может случайно получится так,
                                                 что все 13 рандомных численных символа в prodid когда-то совпадут :)*/
  }

  public void changeUpdate(MouseEvent mouseEvent) //закрытие доступа к полям, которые в данной задаче не нужны
  {
    if (updateGroup.getToggles().get(0).isSelected())
    {
      disableUpdateRows(false, false, false);
      return;
    }
    if (updateGroup.getToggles().get(1).isSelected())
    {
      disableUpdateRows(true, false, true);
      return;
    }
    disableUpdateRows(true, false, false);
  }

  private void disableUpdateRows(boolean first, boolean second, boolean third) //метод закрытия и очистки
  {
    updateGrid.getChildren().get(0).setDisable(first); //prodid
    updateGrid.getChildren().get(3).setDisable(first);
    if (first)
    {
      ((TextField) updateGrid.getChildren().get(3)).setText("");
    }

    updateGrid.getChildren().get(1).setDisable(second); //title
    updateGrid.getChildren().get(4).setDisable(second);
    if (second)
    {
      ((TextField) updateGrid.getChildren().get(4)).setText("");
    }

    updateGrid.getChildren().get(2).setDisable(third); //cost
    updateGrid.getChildren().get(5).setDisable(third);
    if (third)
    {
      ((TextField) updateGrid.getChildren().get(5)).setText("");
    }
  }

  public void changeShow(MouseEvent mouseEvent) //аналогично с update
  {
    if (showGroup.getToggles().get(0).isSelected())
    {
      disableShowRows(true, true);
      return;
    }
    if (showGroup.getToggles().get(1).isSelected())
    {
      disableShowRows(false, true);
      return;
    }
    disableShowRows(true, false);
  }

  private void disableShowRows(boolean first, boolean second) // аналогично ...
  {
    showOne.setDisable(first);
    if (first)
    {
      ((TextField) showOne.getChildren().get(1)).setText("");
    }

    showFilter.setDisable(second);
    if (second)
    {
      ((TextField) showFilter.getChildren().get(1)).setText("");
      ((TextField) showFilter.getChildren().get(2)).setText("");
    }
  }

  public void clearUpdateTab(MouseEvent mouseEvent) //метод привязан к кнопке Clear(очищает поля ввода)
  {
    ((TextField) updateGrid.getChildren().get(4)).setText("");
    ((TextField) updateGrid.getChildren().get(3)).setText("");
    ((TextField) updateGrid.getChildren().get(5)).setText("");
  }

  public void clearShowTab(MouseEvent mouseEvent) //аналогично во вкладке Show
  {
    ((TextField) showFilter.getChildren().get(1)).setText("");
    ((TextField) showFilter.getChildren().get(2)).setText("");
    ((TextField) showOne.getChildren().get(1)).setText("");
  }

  public void clearSuccess(Event event) //при переходе на следующую вкладку очищается надпись "SUCCESS"
  {
    successUpdate.setVisible(false);
  }


  public void updateExecute(MouseEvent mouseEvent) //кнопка Update выполнения изменений в бд
  {
    successUpdate.setVisible(false);
    Product product = new Product();

    try
    {
      if (updateGroup.getToggles().get(0).isSelected())
      {
        product.setProdid(((TextField) updateGrid.getChildren().get(3)).getText());
        product.setTitle(((TextField) updateGrid.getChildren().get(4)).getText());
        product.setCost(((TextField) updateGrid.getChildren().get(5)).getText());
        products.addProduct(product);
      }
      if (updateGroup.getToggles().get(1).isSelected())
      {
        product.setTitle(((TextField) updateGrid.getChildren().get(4)).getText());
        products.deleteProduct(product);
      }
      if (updateGroup.getToggles().get(2).isSelected())
      {
        product.setTitle(((TextField) updateGrid.getChildren().get(4)).getText());
        product.setCost(((TextField) updateGrid.getChildren().get(5)).getText());
        products.updateProduct(product);
      }
    }
    catch (Exception e)
    {
      System.out.println("Update:  " + e);
      return;
    }

    successUpdate.setVisible(true);
  }

  public void showExecute(MouseEvent mouseEvent) //аналогично updateExecute для Show с последующим переходом к таблице
  {
    ObservableList<Product> list;

    try
    {
      if (showGroup.getToggles().get(0).isSelected())
      {
        list = products.getAll();
        prodTable.setItems(list);
      }
      if (showGroup.getToggles().get(1).isSelected())
      {
        Product product = new Product();
        product.setTitle(((TextField) showOne.getChildren().get(1)).getText());
        list = products.getProduct(product);
        prodTable.setItems(list);
      }
      if (showGroup.getToggles().get(2).isSelected())
      {
        String from = ((TextField) showFilter.getChildren().get(1)).getText();
        String to = ((TextField) showFilter.getChildren().get(2)).getText();
        list = products.filterProducts(from, to);
        prodTable.setItems(list);
      }
    }
    catch (Exception e)
    {
      prodTable.setItems(FXCollections.<Product>observableArrayList()); //если что пошло не так очищает таблицу
      System.out.println("Show: " + e);
      return;
    }
    tabPane.getSelectionModel().select(2); //вкладка талицы
  }

  public void clearTable(MouseEvent mouseEvent) //кнопка Clear во вкладке с таблицей
  {
    prodTable.setItems(FXCollections.<Product>observableArrayList());
  }
}
