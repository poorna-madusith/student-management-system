package com.example.student_managemnt;

import javafx.animation.ScaleTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class EditController {

    @FXML
    private ListView<String> editStudentDetailsList;

    @FXML
    private TextField nameField;
    @FXML
    private TextField ageField;
    @FXML
    private ChoiceBox<String> courseField;
    @FXML
    private TextField module1Field;
    @FXML
    private TextField module2Field;
    @FXML
    private TextField module3Field;
    @FXML
    private ImageView imageView2;

    public void initialize() {
        courseField.getItems().addAll("Software Engineer", "Computer Science");
    }

    public void setStudentDetails(List<String> studentDetails) {
        editStudentDetailsList.getItems().clear();
        editStudentDetailsList.getItems().addAll(studentDetails);

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
        ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(200), imageView2);
        scaleTransition.setToX(1.6);  // Slightly zoom in (60% larger)
        scaleTransition.setToY(1.6);
        scaleTransition.play();
    }

    private void zoomOut() {
        ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(400), imageView2);
        scaleTransition.setToX(1.0);  // Zoom out to original size
        scaleTransition.setToY(1.0);
        scaleTransition.play();
    }

    @FXML
    private void handleImageClick2() throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("editremove.fxml"));
        Stage stage = (Stage) imageView2.getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
    @FXML
    public void submitStudentChanges() {
        String name = nameField.getText().trim();
        String ageText = ageField.getText().trim();
        String course = courseField.getValue();
        String module1Text = module1Field.getText().trim();
        String module2Text = module2Field.getText().trim();
        String module3Text = module3Field.getText().trim();

        if (name.isEmpty() || ageText.isEmpty() || course == null || module1Text.isEmpty() || module2Text.isEmpty() || module3Text.isEmpty()) {
            showAlert2("Missing Information", "Please fill in all fields.");
            return;
        }

        if (!validateInput(name, ageText, module1Text, module2Text, module3Text)) {
            return;
        }

        int age = Integer.parseInt(ageText);
        int module1 = Integer.parseInt(module1Text);
        int module2 = Integer.parseInt(module2Text);
        int module3 = Integer.parseInt(module3Text);

        String sql = "UPDATE st_details SET name = ?, age = ?, course = ?, module_1 = ?, module_2 = ?, module_3 = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, name);
            pstmt.setInt(2, age);
            pstmt.setString(3, course);
            pstmt.setInt(4, module1);
            pstmt.setInt(5, module2);
            pstmt.setInt(6, module3);
            pstmt.setString(7, extractStudentId());

            pstmt.executeUpdate();

            showAlert("Success", "Student details updated successfully!");


            // Reset fields after submission
            nameField.clear();
            ageField.clear();
            courseField.setValue(null);
            module1Field.clear();
            module2Field.clear();
            module3Field.clear();
            editStudentDetailsList.getItems().clear();
            navigateToEditRemovePage();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
            showAlert2("Database Error", "Failed to update student details.");
        }
    }


    private boolean validateInput(String name, String ageText, String module1Text, String module2Text, String module3Text) {
        if (!name.matches("[a-zA-Z ]+")) {
            showAlert2("Invalid Name", "Name should contain only letters and spaces.");
            return false;
        }

        if (!ageText.matches("\\d+")) {
            showAlert2("Invalid Age", "Age should be a valid number.");
            return false;
        }
        int age = Integer.parseInt(ageText);
        if (age >= 25) {
            showAlert2("Invalid Age", "Age must be less than 25.");
            return false;
        }

        if (!module1Text.matches("\\d+") || !module2Text.matches("\\d+") || !module3Text.matches("\\d+")) {
            showAlert2("Invalid Marks", "Module marks should be valid numbers.");
            return false;
        }

        return true;
    }

    private String extractStudentId() {
        // Assuming the student ID is present in the list view
        for (String detail : editStudentDetailsList.getItems()) {
            if (detail.startsWith("ID: ")) {
                return detail.substring(4);
            }
        }
        return null;
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    private void showAlert2(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    private void navigateToEditRemovePage() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("editremove.fxml"));
            Stage stage = (Stage) nameField.getScene().getWindow();  // or any other element from the current scene
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.out.println(e.getMessage());
            showAlert("Navigation Error", "Failed to navigate back to the edit page.");
        }
    }
}
