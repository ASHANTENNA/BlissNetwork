# BlissNetwork

BlissNetwork is an Android application targeting SDK 29 designed to reliably manage and connect to Wi-Fi networks on BlissOS using external USB network adapters. 

## ⚠️ The Problem

By default, BlissOS is optimized for internal Wi-Fi cards. While USB Wi-Fi adapters are recognized and function initially, a system limitation prevents continuous network scanning. The OS typically scans for available networks only once immediately after a successful boot. After this initial scan, the system's native Wi-Fi manager fails to discover new networks or refresh the list, making it impossible to switch networks without rebooting the machine.

## 💡 The Solution

This project bypasses the standard Android Wi-Fi management framework by utilizing **root access** to manually trigger network scans and handle connections. Based on the application's architecture, it utilizes a `RootUtil` class to execute privileged system commands. Additionally, it integrates directly into the system's drop-down menu using Quick Settings tiles for both Wi-Fi and Bluetooth. 

## ✨ Features

*   **Root-Powered Management:** Forcibly scans and connects to available Wi-Fi networks on demand using root privileges via the `RootUtil` component.
*   **Wi-Fi Quick Tile:** Implements a `WifiFixTile` service to provide a convenient Quick Settings toggle for immediate Wi-Fi management.
*   **Bluetooth Quick Tile:** Implements a `BluetoothFixTile` service, providing additional Quick Settings support for Bluetooth adapter connections.
*   **Target Environment:** Built with a target SDK version of 29 (Android 10) under the `com.thenew.blissnetwork` package namespace.

## 📋 Requirements

*   **OS:** BlissOS (Tested on v15.9.1)
*   **Privileges:** Root access is **strictly required**. 
*   **Hardware:** A compatible USB Wi-Fi adapter.
*   **SDK:** Target SDK 29 (Android 10).

## 🚀 Installation & Usage

1.  Download the latest APK from the Releases page or build the project from source using the included Gradle wrappers.
2.  Install the APK on your BlissOS machine.
3.  Launch the `MainActivity` application interface.
4.  When prompted by your root manager (e.g., KernelSU or Magisk), grant **Superuser** permissions to the app.
5.  Pull down your notification shade and add the Wi-Fi Fix and Bluetooth Fix tiles to your Quick Settings panel for easy access.

## 🛠️ Building from Source

Clone the repository and open it in Android Studio:

```bash
git clone https://github.com/ASHANTENNA/BlissNetwork.git
