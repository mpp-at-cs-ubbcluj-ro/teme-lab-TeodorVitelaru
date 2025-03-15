package ro.mpp2024;

import ro.mpp2024.domain.Rezervare;
import ro.mpp2024.domain.User;
import ro.mpp2024.repo.RepoDB.RezervareRepoDB;
import ro.mpp2024.repo.RepoDB.UserRepoDB;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        Properties props = new Properties();
        try{
            props.load(new FileReader("bd.config"));
            System.out.println("Properties set. " + props);
        } catch (IOException e) {
            System.out.println("Cannot find bd.config " + e);
        }

        UserRepoDB userRepoDB = new UserRepoDB(props);
        RezervareRepoDB rezervareRepoDB = new RezervareRepoDB(props);
        try{
           for (Rezervare user : rezervareRepoDB.findAll()) {
               System.out.println(user);
           }
        } catch (Exception e) {
            System.out.println("Error " + e);
        }
    }
}