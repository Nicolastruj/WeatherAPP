package org.ulpgc.dacd.control;

public class GUIController {
    private final GUIExecutor executor;

    public GUIController(GUIExecutor executor){
        this.executor = executor;
    }
    public void runTask(){
        task();
    }
    public void task(){
        executor.execute();
    }
}
