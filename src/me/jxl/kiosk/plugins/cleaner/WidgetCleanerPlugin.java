package me.jxl.kiosk.plugins.cleaner;

import java.util.HashMap;
import java.util.Map;

public class WidgetCleanerPlugin {
    private Object host;
    private boolean isAutomationEnabled = true;
    private String targetModePreference = "Black";
    private boolean hideClockPreference = true;
    private boolean hideForecastPreference = true;
    private boolean hideEntityPreference = true;
    private boolean widgetsAreHidden = false;

    public void start(Object hostHandle, Map<String, Object> settings) {
        this.host = hostHandle;
        configure(settings);
    }

    public void configure(Map<String, Object> settings) {
        if (settings.containsKey("enabled")) {
            this.isAutomationEnabled = (boolean) settings.get("enabled");
        }
        if (settings.containsKey("target_mode")) {
            this.targetModePreference = (String) settings.get("target_mode");
        }
        if (settings.containsKey("hide_clock")) {
            this.hideClockPreference = (boolean) settings.get("hide_clock");
        }
        if (settings.containsKey("hide_forecast")) {
            this.hideForecastPreference = (boolean) settings.get("hide_forecast");
        }
        if (settings.containsKey("hide_entity")) {
            this.hideEntityPreference = (boolean) settings.get("hide_entity");
        }
    }

    public void onEvent(String event, Object payload) {
        if (!isAutomationEnabled || host == null) {
            return;
        }

        if ("screensaver.started".equals(event) || "screensaver.changed".equals(event)) {
            if (payload instanceof Map) {
                Map<?, ?> data = (Map<?, ?>) payload;
                String currentMode = (String) data.get("mode");

                if (targetModePreference != null && targetModePreference.equalsIgnoreCase(currentMode)) {
                    applyWidgetCleaning();
                } else if (widgetsAreHidden) {
                    restoreWidgetCleaning();
                }
            }
        } 
        else if ("screensaver.stopped".equals(event)) {
            restoreWidgetCleaning();
        }
    }

    private void applyWidgetCleaning() {
        if (widgetsAreHidden) return;

        Map<String, Object> updates = new HashMap<>();
        
        if (hideClockPreference) {
            updates.put("widget.small_clock.enabled", false);
            updates.put("widget.small_clock.show_date", false);
        }
        if (hideForecastPreference) {
            updates.put("widget.weather_forecast.enabled", false);
        }
        if (hideEntityPreference) {
            updates.put("widget.ha_entity.enabled", false);
        }
        
        if (!updates.isEmpty()) {
            try {
                // Use reflection to interact with the host handle safely at runtime
                host.getClass().getMethod("updateSettings", Map.class).invoke(host, updates);
                host.getClass().getMethod("log", String.class).invoke(host, "Target screensaver (" + targetModePreference + ") matched. Cleaning selected overlays.");
                widgetsAreHidden = true;
            } catch (Exception e) {
                // Gracefully fallback if the runtime handle acts differently
            }
        }
    }

    private void restoreWidgetCleaning() {
        if (!widgetsAreHidden) return;

        Map<String, Object> updates = new HashMap<>();
        
        if (hideClockPreference) {
            updates.put("widget.small_clock.enabled", true);
            updates.put("widget.small_clock.show_date", true);
        }
        if (hideForecastPreference) {
            updates.put("widget.weather_forecast.enabled", true);
        }
        if (hideEntityPreference) {
            updates.put("widget.ha_entity.enabled", true);
        }
        
        if (!updates.isEmpty()) {
            try {
                host.getClass().getMethod("updateSettings", Map.class).invoke(host, updates);
                host.getClass().getMethod("log", String.class).invoke(host, "Target screensaver exited. Overlays cleanly restored.");
                widgetsAreHidden = false;
            } catch (Exception e) {
                // Quietly pass
            }
        }
    }

    public void execute(String command, Map<String, Object> arguments) {}

    public void stop() {
        this.host = null;
    }
}
