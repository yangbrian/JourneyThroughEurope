package jte.ui;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import jte.files.PropertiesManager;

/**
 * @author
 */
public class JTESplashUI extends StackPane {
    private Button newGameButton;
    private Button loadGameButton;

    private Button aboutButton;
    private Button quitButton;

    private ImageView background;
    private VBox menu;

    public JTESplashUI() {
        menu = new VBox();
        menu.setPadding(new Insets(30));

        newGameButton = new Button(PropertiesManager.getValue("NEW"));
        newGameButton.getStyleClass().add("button-large");
        loadGameButton = new Button(PropertiesManager.getValue("LOAD"));
        loadGameButton.getStyleClass().add("button-large");
        aboutButton = new Button(PropertiesManager.getValue("ABOUT"));
        aboutButton.getStyleClass().add("button-large");
        quitButton = new Button(PropertiesManager.getValue("QUIT"));
        quitButton.getStyleClass().add("button-large");

        menu.getChildren().addAll(newGameButton, loadGameButton, aboutButton, quitButton);

        menu.setAlignment(Pos.BOTTOM_CENTER);
        menu.setSpacing(25);

        ImageView splash = new ImageView(new Image("file:images/splash.jpg"));

        this.getChildren().addAll(splash, menu);
    }

    public Button getAboutButton() {
        return aboutButton;
    }

    public Button getQuitButton() {
        return quitButton;
    }

    public Button getLoadGameButton() {
        return loadGameButton;
    }

    public Button getNewGameButton() {
        return newGameButton;
    }
}
