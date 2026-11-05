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
    private boolean hideWeatherPreference = true;
    private boolean hideForecastPreference = true;
    private boolean hideBatteryPreference = true;
    private boolean hideAlarmPreference = true;
    private boolean hideUpNextPreference = true;
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
        if (settings.containsKey("hide_clock")) { this.hideClockPreference = (boolean) settings.get("hide_clock"); }
        if (settings.containsKey("hide_weather")) { this.hideWeatherPreference = (boolean) settings.get("hide_weather"); }
        if (settings.containsKey("hide_forecast")) { this.hideForecastPreference = (boolean) settings.get("hide_forecast"); }
        if (settings.containsKey("hide_battery")) { this.hideBatteryPreference = (boolean) settings.get("hide_battery"); }
        if (settings.containsKey("hide_alarm")) { this.hideAlarmPreference = (boolean) settings.get("hide_alarm"); }
        if (settings.containsKey("hide_up_next")) { this.hideUpNextPreference = (boolean) settings.get("hide_up_next"); }
        if (settings.containsKey("hide_entity")) { this.hideEntityPreference = (boolean) settings.get("hide_entity"); }
    }

    @Override
    public void onEvent(String event, Map<String, Object> payload) {
        if (!isAutomationEnabled || host == null) {
            return;
        }

        if ("screensaver.started".equals(event) || "screensaver.changed".equals(event)) {
            if (payload != null) {
                String currentMode = (String) payload.get("mode");
                
                if (isTargetMatch(currentMode)) {
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

    private boolean isTargetMatch(String currentMode) {
        if (currentMode == null || targetModePreference == null) return false;
        String cleanCurrent = currentMode.replace("_", " ").trim().toLowerCase();
        String cleanTarget = targetModePreference.replace("_", " ").trim().toLowerCase();
        return cleanCurrent.equalsIgnoreCase(cleanTarget);
    }

    private void applyWidgetCleaning() {
        if (widgetsAreHidden) return;

        Map<String, Object> updates = new HashMap<>();
        
        if (hideClockPreference) {
            updates.put("widget.small_clock.enabled", false);
            updates.put("widget.small_clock.show_date", false);
        }
        if (hideWeatherPreference) { updates.put("widget.weather.enabled", false); }
        if (hideForecastPreference) { updates.put("widget.weather_forecast.enabled", false); }
        if (hideBatteryPreference) { updates.put("widget.battery.enabled", false); }
        if (hideAlarmPreference) { updates.put("widget.next_alarm.enabled", false); }
        if (hideUpNextPreference) { updates.put("widget.up_next.enabled", false); }
        if (hideEntityPreference) { updates.put("widget.ha_entity.enabled", false); }
        
        if (!updates.isEmpty()) {
            host.updateSettings(updates);
            host.log("Screensaver matched. Hiding selected overlays.");
            widgetsAreHidden = true;
        }
    }

    private void restoreWidgetCleaning() {
        if (!widgetsAreHidden) return;

        Map<String, Object> updates = new HashMap<>();
        
        updates.put("widget.small_clock.enabled", true);
        updates.put("widget.small_clock.show_date", true);
        updates.put("widget.weather.enabled", true);
        updates.put("widget.weather_forecast.enabled", true);
        updates.put("widget.battery.enabled", true);
        updates.put("widget.next_alarm.enabled", true);
        updates.put("widget.up_next.enabled", true);
        updates.put("widget.ha_entity.enabled", true);
        
        host.updateSettings(updates);
        host.log("Screensaver exited. Overlays cleanly restored.");
        widgetsAreHidden = false;
    }

    @Override
    public void execute(String command, Map<String, Object> arguments) {}

    @Override
    public void stop() {
        this.host = null;
    }
}
