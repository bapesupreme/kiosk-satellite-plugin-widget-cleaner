package me.jxl.kiosk.plugins;

import java.util.Map;

public interface KioskPlugin {
    void start(PluginHost host, Map<String, Object> settings);
    void configure(Map<String, Object> settings);
    void execute(String command, Map<String, Object> arguments);
    void onEvent(String event, Map<String, Object> payload); // Changed from Object to Map
    void stop();
}
