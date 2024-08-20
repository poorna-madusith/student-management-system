package com.example.student_managemnt;

import com.example.student_managemnt.DatabaseConnection;
import javafx.animation.ScaleTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class controller {
    private Stage stage;
    private Scene scene;
    private Parent root;

    private final int studentCapacity = 100;

    @FXML
    private TextField nameField;
    @FXML
    private TextField idField;
    @FXML
    private TextField ageField;
    @FXML
    private TextField removeIdField;
    @FXML
    private ChoiceBox<String> courseField;
    @FXML
    private ListView<String> studentDetailsList;
    @FXML
    private ImageView imageView;

    @FXML
    private TableView<Student> studentTable;
    @FXML
    private TableColumn<Student, String> nameColumn;
    @FXML
    private TableColumn<Student, Integer> ageColumn;
    @FXML
    private TableColumn<Student, String> idColumn;
    @FXML
    private TableColumn<Student, String> courseColumn;
    @FXML
    private TableColumn<Student, Integer> module1Column;
    @FXML
    private TableColumn<Student, Integer> module2Column;
    @FXML
    private TableColumn<Student, Integer> module3Column;

    @FXML
    public void initialize() {
        // Set up the course choice box
        if (courseField == null) {
            courseField = new ChoiceBox<>();
        }
        courseField.getItems().addAll("Software Engineer", "Computer Science");
        courseField.setValue("Computer Science");  // Set default value
    }

    @FXML
    private void handleMouseEntered() {
        zoomIn();
    }

    @FXML
    private void handleMouseExited() {
        zoomOut();
    }

    private void zoomIn() {
        ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(200), imageView);
        scaleTransition.setToX(1.6);  // Slightly zoom in (60% larger)
        scaleTransition.setToY(1.6);
        scaleTransition.play();
    }

    private void zoomOut() {
        ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(400), imageView);
        scaleTransition.setToX(1.0);  // Zoom out to original size
        scaleTransition.setToY(1.0);
        scaleTransition.play();
    }

    @FXML
    private void handleImageClick() throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("menu.fxml"));
        stage = (Stage) imageView.getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void switchtomenu(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("menu.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void option1(ActionEvent event) throws IOException {
        int seatsLeft = studentCapacity - getStudentCount();

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Seats Information");
        alert.setHeaderText(null);
        alert.setContentText("Seats left = " + seatsLeft);

        alert.showAndWait();
    }

    public void option2(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("registerst.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void option3(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("editremove.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void option4(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("searchst.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void option5(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("modulemarks.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void saveStudent(ActionEvent event) {
        String name = nameField.getText().trim();
        String studentId = idField.getText().trim();
        String ageText = ageField.getText().trim();
        String course = courseField.getValue();

        // Check if any field is empty
        if (name.isEmpty() || studentId.isEmpty() || ageText.isEmpty() || course == null) {
            showAlert2("Missing Information", "Please fill in all fields.");
            return;
        }

        // Validate the input
        if (!validateInput(name, studentId, ageText)) {
            return;
        }

        int age = Integer.parseInt(ageText);

        if (getStudentCount() >= studentCapacity) {
            showAlert2("Registration Error", "You have reached the maximum number of students registered.");
            return;
        }

        String sql = "INSERT INTO st_details(name, id, age, course) VALUES(?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Check if ID already exists
            String checkIdSql = "SELECT COUNT(*) FROM st_details WHERE id = ?";
            try (PreparedStatement checkIdStmt = conn.prepareStatement(checkIdSql)) {
                checkIdStmt.setString(1, studentId);
                ResultSet rs = checkIdStmt.executeQuery();
                rs.next();
                if (rs.getInt(1) > 0) {
                    showAlert2("Duplicate ID", "This ID is already registered.");
                    return;
                }
            }

            pstmt.setString(1, name);
            pstmt.setString(2, studentId);
            pstmt.setInt(3, age);
            pstmt.setString(4, course);
            pstmt.executeUpdate();

            // Reset fields
            nameField.clear();
            idField.clear();
            ageField.clear();
            courseField.setValue("Computer Science");  // Reset to default value

            showAlert("Successful", "Student Registered Successfully");

        } catch (SQLException e) {
            System.out.println(e.getMessage());
            showAlert2("Error", "Registration Failed");
        }
    }

    @FXML
    public void searchStudent(ActionEvent event) {
        String studentId = removeIdField.getText().trim();

        if (studentId.isEmpty()) {
            showAlert2("Missing Information", "Please enter a student ID.");
            return;
        }

        String sql = "SELECT name, id, age, course, module_1, module_2, module_3 FROM st_details WHERE id = ?";

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, studentId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                studentDetailsList.getItems().clear();
                studentDetailsList.getItems().add("Name: " + rs.getString("name"));
                studentDetailsList.getItems().add("ID: " + rs.getString("id"));
                studentDetailsList.getItems().add("Age: " + rs.getInt("age"));
                studentDetailsList.getItems().add("Course: " + rs.getString("course"));
                studentDetailsList.getItems().add("Module 1: " + rs.getInt("module_1"));
                studentDetailsList.getItems().add("Module 2: " + rs.getInt("module_2"));
                studentDetailsList.getItems().add("Module 3: " + rs.getInt("module_3"));
            } else {
                showAlert2("No Record Found", "No student found with ID: " + studentId);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            showAlert2("Database Error", "Failed to search for student data.");
        }
    }

    @FXML
    public void removeStudent(ActionEvent event) {
        String studentId = removeIdField.getText().trim();

        if (studentId.isEmpty()) {
            showAlert2("Missing Information", "Please enter a student ID.");
            return;
        }

        String sql = "DELETE FROM st_details WHERE id = ?";

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, studentId);
            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                showAlert("Success", "Student with ID: " + studentId + " has been removed.");
                removeIdField.clear();
            } else {
                showAlert2("No Record Found", "No student found with ID: " + studentId);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            showAlert2("Database Error", "Failed to remove student data.");
        }
    }

    public void editStudent(ActionEvent event) throws IOException {
        String studentId = removeIdField.getText().trim();

        if (studentId.isEmpty()) {
            showAlert2("Missing Information", "Please enter a student ID.");
            return;
        }

        if (studentDetailsList.getItems().isEmpty()) {
            showAlert2("No Data", "Please search for a student first.");
            return;
        }

        // Load the edit.fxml scene
        FXMLLoader loader = new FXMLLoader(getClass().getResource("edit.fxml"));
        Parent root = loader.load();

        // Get the controller for the edit.fxml scene
        EditController editController = loader.getController();

        // Pass the student details to the next scene
        editController.setStudentDetails(studentDetailsList.getItems());

        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }




    public void switchToListScene(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("studentlist.fxml"));
        Parent root = loader.load();

        controller controller = loader.getController();
        controller.loadStudentData();  // Populate the TableView with data

        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    private void loadStudentData() {
        ObservableList<Student> studentList = FXCollections.observableArrayList();

        String sql = "SELECT * FROM st_details";
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Student student = new Student(rs.getString("name"), rs.getString("id"),
                        rs.getInt("age"), rs.getString("course"),
                        rs.getInt("module_1"), rs.getInt("module_2"), rs.getInt("module_3"));
                studentList.add(student);
            }

            // Set up the columns in the table
            nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
            idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
            ageColumn.setCellValueFactory(new PropertyValueFactory<>("age"));
            courseColumn.setCellValueFactory(new PropertyValueFactory<>("course"));
            module1Column.setCellValueFactory(new PropertyValueFactory<>("module1"));
            module2Column.setCellValueFactory(new PropertyValueFactory<>("module2"));
            module3Column.setCellValueFactory(new PropertyValueFactory<>("module3"));

            studentTable.setItems(studentList);

        } catch (SQLException e) {
            System.out.println(e.getMessage());
            showAlert2("Database Error", "Failed to load student data.");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showAlert2(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private boolean validateInput(String name, String id, String ageText) {
        // Validate name
        if (!name.matches("[a-zA-Z ]+")) {
            showAlert2("Invalid Input", "Name must not contain numbers or symbols.");
            return false;
        }

        // Validate ID
        if (!id.matches("\\d{8}")) {
            showAlert2("Invalid Input", "ID must be an 8-digit number.");
            return false;
        }

        // Validate age
        try {
            int age = Integer.parseInt(ageText);
            if (age >= 25) {
                showAlert2("Invalid Input", "Age must be less than 25.");
                return false;
            }
        } catch (NumberFormatException e) {
            showAlert2("Invalid Input", "Age must be a valid number.");
            return false;
        }

        return true;
    }

    private int getStudentCount() {
        String sql = "SELECT COUNT(*) FROM st_details";
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return 0;
    }
}
