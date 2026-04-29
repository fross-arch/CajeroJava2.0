package miplata;

import miplata.config.Config;
import miplata.userinterface.MenuApp;

public class Main {

    public static void main(String[] args) {
        MenuApp menuApp = Config.createMenuApp();
        menuApp.showMainMenu();
    }
}
