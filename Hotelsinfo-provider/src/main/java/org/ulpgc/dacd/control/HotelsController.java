package org.ulpgc.dacd.control;

public class HotelsController {
    private final HotelsProvider provider;
    private final HotelsStore storer;
    public HotelsController(HotelsProvider provider, HotelsStore storer){
        this.provider = provider;
        this.storer = storer;
    }
}
