package org.ulpgc.dacd.control;

public class Main {
    public static void main(String[] args) {
        GUIExecutor swingGUIExecutor = new SwingGUIExecutor();
        GUIController controller =  new GUIController(swingGUIExecutor);
        controller.runTask();
    }
}