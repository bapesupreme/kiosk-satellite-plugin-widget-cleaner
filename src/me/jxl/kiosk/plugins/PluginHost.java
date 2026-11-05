package me.jxl.kiosk.plugins;

import java.util.Map;

public interface PluginHost {
    void updateSettings(Map<String, Object> values);
    void log(String message);
}
