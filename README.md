# Screensaver Widget Cleaner Plugin

An automated utility plugin for [Kiosk Satellite](https://github.com/jxlarrea/kiosk-satellite) that keeps your active display clean and completely clutter-free. 

## Features
- **Dynamic Event Monitoring:** Automatically hooks into Kiosk Satellite's native screensaver lifecycle actions.
- **Target Selection Profiles:** Choose any of the 14 built-in screensaver display views to track (such as Black, Dim, Clock, or Photo Gallery) right from the settings panel.
- **Selective Management:** Individual boolean configurations let you choose exactly which layouts the plugin handles (Small Clock, Date line, Weather Forecast rows, or Custom HA Entity badges).
- **Automated Clean Up:** Restores every hidden overlay back to full active visibility instantly the moment the kiosk screen is touched or wakes up.

## Installation
Since this plugin leverages verified automated packages, it must be loaded directly into your kiosk space:
1. Navigate to the **Releases** tab on the right side of this repository page.
2. Download the packaged stability asset container (`.zip`) from the latest stable tag version.
3. Open your kiosk display panel or your **Remote Admin Dashboard**.
4. Go to **Plugin Manager > Developer Tools > Install from ZIP**.
5. Select your file, review the code authorization security notice, click **Trust and install**, and turn on the active toggle button.

## Configuration
All controls populate cleanly in their own layout space. Go to your active configuration dashboard and find the **Widget Automation Setup** group category row to tweak your preferences live!
