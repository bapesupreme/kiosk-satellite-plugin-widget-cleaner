package me.jxl.kiosk.plugins.cleaner;

import me.jxl.kiosk.plugins.KioskPlugin;
import me.jxl.kiosk.plugins.PluginHost;
import java.util.HashMap;
import java.util.Map;

public class WidgetCleanerPlugin implements KioskPlugin {
    private PluginHost host;
    private boolean isAutomationEnabled = true;
    private String targetModePreference = "Black";
    private boolean hideClockPreference = true;
    private boolean hideForecastPreference = true;
    private boolean hideEntityPreference = true;
    private boolean widgetsAreHidden = false;

    @Override
    public void start(PluginHost hostHandle, Map<String, Object> settings) {
        this.host = hostHandle;
        configure(settings);
    }

    @Override
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
    @Override
    public void onEvent(String event, Map<String, Object> payload) { // Changed signature
        if (!isAutomationEnabled || host == null) {
            return;
        }

        if ("screensaver.started".equals(event) || "screensaver.changed".equals(event)) {
            if (payload != null) {
                String currentMode = (String) payload.get("mode");

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
            host.updateSettings(updates);
            host.log("Target screensaver (" + targetModePreference + ") matched. Cleaning selected overlays.");
            widgetsAreHidden = true;
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
            host.updateSettings(updates);
            host.log("Target screensaver exited. Overlays cleanly restored.");
            widgetsAreHidden = false;
        }
    }

    @Override
    public void execute(String command, Map<String, Object> arguments) {}

    @Override
    public void stop() {
        this.host = null;
    }
}
