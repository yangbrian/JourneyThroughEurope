package jte.ui;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

/**
 * @author
 */
public class JTESplashUI extends BorderPane {
    private Button newGameButton;
    private Button loadGameButton;
    private Button aboutButton;
    private Button quitButton;

    private ImageView background;
    private VBox menu;

    public JTESplashUI() {
        menu = new VBox();
        menu.setPadding(new Insets(30, 30, 30, 30));

        newGameButton = new Button("New Game");
        loadGameButton = new Button("Load Game");
        aboutButton = new Button("About Game");
        quitButton = new Button("Quit Game");

        menu.getChildren().addAll(newGameButton, loadGameButton, aboutButton, quitButton);

        this.setCenter(menu);
    }
}
