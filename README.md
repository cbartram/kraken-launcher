[![Contributors][contributors-shield]][contributors-url]
[![Forks][forks-shield]][forks-url]
[![Stargazers][stars-shield]][stars-url]
[![Issues][issues-shield]][issues-url]
[![MIT License][license-shield]][license-url]

<!-- PROJECT LOGO -->
<br />
<div align="center">
  <a href="https://github.com/cbartram/kraken-loader-plugin">
    <img src="src/main/resources/net/runelite/launcher/kraken.png" alt="Logo" width="128" height="128">
  </a>

<h3 align="center">Kraken Launcher</h3>

  <p align="center">
   A RuneLite launcher which side-loads the Kraken essential plugins.
    <br />
</div>

This project is a modified fork of the [RuneLite launcher](https://github.com/runelite/launcher).

Loads the latest version of the [Kraken Client](https://github.com/cbartram/kraken-client) from S3. This allows updates to be made to the Kraken client independently of user downloads.
With this tool users no longer need to constantly download the newest version of the [Kraken Client](https://github.com/cbartram/kraken-client) to play the game. Instead, they can download
this launcher JAR once, and it will automatically pull the latest [Kraken client](https://github.com/cbartram/kraken-client) from S3 for users.

This should be the primary way users launch the game if they intend to use Kraken plugins.

Currently, all plugins for the client are released for free at [our discord here](https://discord.gg/Jxfxr3Zme2). Some features this client offers are:

- Discord authentication and sign up / sign in
- Auto plugin loading (and automatic plugin & client updates)
- Jagex account compatibility
- Native Jagex Launcher Compatibility
- RuneLite mode (Run the client as normal RuneLite without any Kraken plugins)
- Native RuneLite Client (no injection or client modification)

Although the Kraken client is safe and doesn't modify RuneLite in any way the plugins are unofficial. **We are not responsible for any bans you may incur for using this client.**
For more information about the Kraken Client see: [Kraken Client](#about-kraken-client).

# Current Plugins

To view, download, and enable plugins,
check out our website at [kraken-plugins.duckdns.org](https://kraken-plugins.duckdns.org/plugins)

## Installation

Perform the following to install:
- On Windows go to `C:\Users\<YOUR_NAME>\AppData\Local\RuneLite`
- Rename `RuneLite.jar` to `RuneLite-backup.jar` This is the official version of RuneLite if you every want to swap back.
- Go to our [releases page](https://github.com/cbartram/kraken-launcher/releases) and download the latest version of the launcher jar file. It will be called `kraken-launcher-<version>.jar`.
- Move the downloaded `kraken-launcher-<version>.jar` to `C:\Users\<YOUR_NAME>\AppData\Local\RuneLite` 
- Rename `kraken-launcher-<version>.jar` to `RuneLite.jar`
- Run with `RuneLite.exe` or via jagex launcher (see [Jagex launcher details below](#jagex-launcher--jagex-accounts))

## Jagex Launcher & Jagex Accounts

The Kraken Launcher is compatible with the Jagex accounts as well as the Jagex launcher. In order to use the Kraken Launcher with Jagex accounts, follow this guide:

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

## Getting Started

To get started with development on the kraken launcher, clone this repository with:

`git clone https://github.com/cbartram/kraken-launcher.git`

You can build and run the jar with:

```shell
mvn clean package

java -jar ./target/RuneLite.jar
```

If you would like to run in developer mode to load Kraken plugins from the `/dev` prefix in S3 pass:

```shell
java -Ddeveloper-mode=true -jar ./target/RuneLite.jar
```

### Prerequisites

Install gradle in order to build and run this program:

- [Gradle](https://gradle.org/install/)

## Running the tests

Run tests with:

`mvn clean test`

## Deployment

Deployment for this is handled through Circle CI. It builds the JAR file and creates a GitHub release for the newest
version of the launcher.

## Built With

- [Java](https://www.java.org/) - Programming Language Used
- [Gradle](https://gradle.org/) - Build Tool

## Contributing

Please read [CONTRIBUTING.md](CONTRIBUTING.md) for details on our code
of conduct, and the process for submitting pull requests to us.

## Versioning

We use [Semantic Versioning](http://semver.org/) for versioning. For the versions
available, see the [tags on this
repository](https://github.com/cbartram/kraken-launcher/tags).

## License

This project is licensed under the [CC0 1.0 Universal](LICENSE.md)
Creative Commons License - see the [LICENSE.md](LICENSE.md) file for
details

## Acknowledgments

- RuneLite for making a great software! The SplashScreen, and most of this codebase was taken from RuneLite!

[contributors-shield]: https://img.shields.io/github/contributors/cbartram/kraken-launcher.svg?style=for-the-badge
[contributors-url]: https://github.com/cbartram/kraken-launcher/graphs/contributors
[forks-shield]: https://img.shields.io/github/forks/cbartram/kraken-launcher.svg?style=for-the-badge
[forks-url]: https://github.com/cbartram/kraken-launcher/network/members
[stars-shield]: https://img.shields.io/github/stars/cbartram/kraken-launcher.svg?style=for-the-badge
[stars-url]: https://github.com/cbartram/kraken-launcher/stargazers
[issues-shield]: https://img.shields.io/github/issues/cbartram/kraken-launcher.svg?style=for-the-badge
[issues-url]: https://github.com/cbartram/kraken-launcher/issues
[license-shield]: https://img.shields.io/github/license/cbartram/kraken-launcher.svg?style=for-the-badge
[license-url]: https://github.com/cbartram/kraken-launcher/blob/master/LICENSE.txt