package ro.mpp2024.repository;

import ro.mpp2024.model.ComputerRepairedForm;

import java.util.List;

public interface ComputerRepairedFormRepository extends Repository<Integer, ComputerRepairedForm> {
    List<ComputerRepairedForm> filterByEmployee(String employee);
    List<ComputerRepairedForm> filterByEmployeeAndDate(String employee, String date);
}
