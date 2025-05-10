package ro.mpp2024;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import ro.mpp2024.ctrl.ComputerRepairShopController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import ro.mpp2024.repository.ComputerRepairRequestRepository;
import ro.mpp2024.repository.file.ComputerRepairRequestFileRepository;
import ro.mpp2024.repository.file.ComputerRepairedFormFileRepository;
import ro.mpp2024.services.ComputerRepairServices;
import ro.mpp2024.services.ServicesException;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class MainFX extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/RepairShopWindow.fxml"));
            Parent root = loader.load();
            ComputerRepairShopController ctrl = loader.getController();

            ctrl.setService(getService());
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Computer Repairs Shop");
            primaryStage.show();
        }catch(Exception e){
            System.out.println(e.getMessage());
            Alert alert=new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error ");
            alert.setContentText("Error while starting app "+e);
            alert.showAndWait();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

    static ComputerRepairServices getService() throws ServicesException {
        ApplicationContext context = new ClassPathXmlApplicationContext("RepairShopConfig.xml");

        ComputerRepairServices services = context.getBean(ComputerRepairServices.class);
        return services;
    }
}
