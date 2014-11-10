package jte.game;

import jte.files.JTEFileLoader;
import jte.ui.JTEGameSetupUI;
import jte.ui.JTEUI;

/**
 * @author Brian Yang
 */
public class JTEGameStateManager {

    private JTEGameData currentGame;
    private JTEGameInfo info;
    private JTEGameSetupUI.JTEGameState gameState;
    private JTEFileLoader fileHandler;
    private JTEUI ui;

    public JTEGameStateManager(JTEUI ui) {
        this.ui = ui;
        fileHandler = new JTEFileLoader(ui);
        loadGameInfo();
    }

    public void loadGameInfo() {
        info = new JTEGameInfo(fileHandler.loadCities());
    }

    public JTEGameInfo getInfo() {
        return info;
    }
}
