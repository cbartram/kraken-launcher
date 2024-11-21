# QuickStart

To get started with the launcher and get yourself some plugins join [our discord here](https://discord.gg/Jxfxr3Zme2) and create a new ticket!
You can view our currently available [plugins below](#current-plugins).

## Installation

Perform the following to install:
- On Windows go to `C:\Users\<YOUR_NAME>\AppData\Local\RuneLite`
- Rename `RuneLite.jar` to `RuneLite-backup.jar` This is the official version of RuneLite if you every want to swap back.
- Go to our [releases page](https://github.com/cbartram/kraken-launcher/releases) and download the latest version of the launcher jar file. It will be called `kraken-launcher-<version>.jar`.
- Move the downloaded `kraken-launcher-<version>.jar` to `C:\Users\<YOUR_NAME>\AppData\Local\RuneLite`
- Rename `kraken-launcher-<version>.jar` to `RuneLite.jar`
- Run with `RuneLite.exe` or via jagex launcher (see [Jagex launcher details below](#jagex-launcher--jagex-accounts))

## Jagex Launcher & Jagex Accounts

The Kraken Launcher is compatible with the Jagex accounts as well as the Jagex launcher. In order to use the Kraken Launcher with Jagex accounts follow this guide:

- For Windows, run `RuneLite (configure)` from the start menu. Otherwise, pass `--configure` to the launcher (i.e. `/Applications/RuneLite.app/Contents/MacOS/RuneLite --configure` on Mac).
- In the Client arguments input box add `--insecure-write-credentials`
- Click Save
- Launch RuneLite normally via the Jagex launcher. RuneLite will write your launcher credentials to .runelite/credentials.properties.
- On your next launch it will use the saved credentials allowing you to use your Jagex account with Kraken plugins.

If you want to use a non-jagex account with Kraken you can delete the credentials.properties file to return your Kraken Client back to normal.
If for any reason you need to invalidate the credentials, you can use the "End sessions" button under account settings on runescape.com.

## RuneLite Mode

If you would like to run RuneLite like normal without any Kraken plugins loading:

- Click the windows start menu
- Type "RuneLite (Configure)" and hit enter
- In the dialogue box click the "RuneLite mode" checkbox
- Click Save

Your next client run will not load any Kraken plugins and leave RuneLite untouched.