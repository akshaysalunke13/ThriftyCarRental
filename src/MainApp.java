
import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class MainApp extends Application {

	private Stage primaryStage;
	private BorderPane mainView;

	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;
		this.primaryStage.setTitle("Thrifty Car Rental");
		showMainView();
	}

	private void showMainView() {
		try {
			// Load Main View from FXML file.
			FXMLLoader loader = new FXMLLoader();

			loader.setLocation(this.getClass().getResource("/view/MainWindow.fxml"));
			mainView = (BorderPane) loader.load();

			// Show the scene containing the root layout.
			Scene scene = new Scene(mainView);
			primaryStage.setScene(scene);
			primaryStage.show();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Stage getPrimaryStage() {
		return primaryStage;
	}

	public static void main(String[] args) {
		launch(args);
	}
}
