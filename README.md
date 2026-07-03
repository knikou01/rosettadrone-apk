# RosettaDrone APK

A fork of [RosettaDrone](https://github.com/RosettaDrone/rosettadrone) that provides a ready to install APK, so there's no need to build from source.

RosettaDrone is a MAVLink wrapper for DJI drones, allowing you to control DJI hardware using ground control stations like QGroundControl.

For older android devices and the DJI smart controller, see the android7-smart-controller branch.

## What this fork adds

- First-launch setup screen that guides you through getting your API keys
- Keys are stored securely on your device
- Android 14 compatibility fixes
- Updated target SDK for modern devices

## Installation

1. Download the latest APK from the [Releases](../../releases) page
2. On your Android device, enable **Install from unknown sources** in Settings
3. Open the APK to install
4. On first launch, follow the on-screen instructions to set up your DJI and Google Maps API keys

## Getting your API keys

**DJI key**: free, create an account at [developer.dji.com](https://developer.dji.com), create an app with package name `sq.rogue.rosettadrone`, and copy the API key.

**Google Maps key**: free tier available, create a project at [console.cloud.google.com](https://console.cloud.google.com), enable Maps SDK for Android, and generate an API key under Credentials.

## Original project

All drone control logic is from the original [RosettaDrone](https://github.com/RosettaDrone/rosettadrone) project. This fork only adds the setup flow and compatibility fixes.

## License

BSD 3-Clause: see [LICENSE](LICENSE)
