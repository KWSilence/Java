package labs_4_5;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import labs_4_5.productsBD.ProductDAO;

import java.io.File;
import java.net.URL;

public class MainFX extends Application
{

  @Override
  public void start(Stage stage) {
    Parent root;
    try
    {
      URL url = new File("src/main/resources/window.fxml").toURI().toURL();
      root = FXMLLoader.load(url);
    }
    catch (Exception e)
    {
      System.out.println("Loading fxml failed...");
      System.out.println(e);
      return;
    }
    Scene scene = new Scene(root);
    stage.setTitle("Products Manager");
    stage.setScene(scene);
    stage.show();
  }

  @Override
  public void stop() throws Exception
  {
    super.stop();
    ProductDAO.getInstance().closeProductDAO(); //закрытие запросов и отключение от сервера
  }

  public static void main(String[] args) {
    launch(args);
  }
}
