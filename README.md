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

Loads the latest version of the [Kraken Client](https://kraken-plugins.duckdns.org/download) from S3. This allows updates to be made to the Kraken client independently of user downloads.
With this tool users no longer need to constantly download the newest version of the [Kraken Client](https://kraken-plugins.duckdns.org/download) to play the game. Instead, they can download
this launcher JAR once, and it will automatically pull the latest [Kraken client](https://kraken-plugins.duckdns.org/download) from S3 for users.

This should be the primary way users launch the game if they intend to use Kraken plugins.

Currently, all plugins for the client are released for free at [our website here](https://kraken-plugins.duckdns.org). Some features this client offers are:

- Discord authentication and sign up / sign in
- Auto plugin loading (and automatic plugin & client updates)
- Jagex account compatibility
- Native Jagex Launcher Compatibility
- RuneLite mode (Run the client as normal RuneLite without any Kraken plugins)
- Native RuneLite Client (no injection or client modification)
- 18+ bossing, raids, and skilling plugins

Kraken Plugins are not "bots". They do not perform any type of automation for you. Instead they show you useful information about the boss or raid encounter for example:
- Hydra attack counter
- Highlight skull locations for P2 wardens
- Tick counter for Soteseg green ball
- etc...

Although the Kraken client is safe and doesn't modify RuneLite in any way the plugins are unofficial. **We are not responsible for any bans you may incur for using this client.**
For more information about the Kraken Client see: [Kraken Client](#about-kraken-client).

# Current Plugins

To view, download, and enable plugins,
check out our website at [kraken-plugins.duckdns.org](https://kraken-plugins.duckdns.org/plugins)

## Installation

Perform the following to install:
- Download and install [RuneLite](https://runelite.net/) if you haven't already.
- Download the `KrakenInstaller.exe` from [Kraken Plugins Website](https://kraken-plugins.duckdns.org/download).
- Run the `KrakenInstaller.exe` file (you may have to click "More options" and trust the executable.
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

## Development

To get started with development on the kraken launcher, clone this repository with:

`git clone https://github.com/cbartram/kraken-launcher.git`

You can build and run the jar with:

```shell
./gradlew build

java -Ddeveloper-mode=true -jar ./target/RuneLite.jar
```

If you would like to run in developer mode to load Kraken plugins from the `/dev` prefix in S3 pass:

```shell
java -Ddeveloper-mode=true -jar ./target/RuneLite.jar
```

### Prerequisites

Install Gradle in order to build and run this program:

- [Gradle](https://gradle.org/install/)

## Deployment

Deployment for this is handled through InnoSetup to create a .exe installer. The installer expects a pre-built jar artifact in `/build/libs/RuneLite.jar`

- Create a build with `./gradlew clean build`, this creates the `RuneLite.jar` file in `build/libs`
- Run `./gradlew filterInnosetup` to generate a .iss setup file for Kraken
- Run [InnoSetup](https://jrsoftware.org/isinfo.php)
- Load the `kraken64.iss` file into InnoSetup from `/build/filtered-resources`
- Click the play button to build `KrakenSetup.exe`
- Upload `KrakenSetup.exe` to S3 and make sure the frontend is updated to pull the latest version down!

## Built With

- [Java](https://www.java.org/) - Programming Language Used
- [InnoSetup](https://jrsoftware.org/isinfo.php) - Executable build software
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
