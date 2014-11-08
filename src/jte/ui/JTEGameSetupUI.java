package jte.ui;

import javafx.scene.control.ComboBox;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;

/**
 * @author Brian Yang
 */
public class JTEGameSetupUI extends FlowPane {

    public enum JTEGameState {
        SPLASH_SCREEN, PLAYER_SELECT, GAME_IN_PROGRESS, GAME_OVER
    }

    private HBox toolbar;

    public JTEGameSetupUI() {
        toolbar = new HBox();
        ComboBox emailComboBox = new ComboBox();
        emailComboBox.getItems().addAll(
                "jacob.smith@example.com",
                "isabella.johnson@example.com",
                "ethan.williams@example.com",
                "emma.jones@example.com",
                "michael.brown@example.com"
        );

        toolbar.getChildren().add(emailComboBox);
    }

    public void createSelections(int players) {

    }
}
