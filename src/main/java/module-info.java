module com.example.student_managemnt {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.desktop;


    opens com.example.student_managemnt to javafx.fxml;
    exports com.example.student_managemnt;
}