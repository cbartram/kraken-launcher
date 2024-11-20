[![Contributors][contributors-shield]][contributors-url]
[![Forks][forks-shield]][forks-url]
[![Stargazers][stars-shield]][stars-url]
[![Issues][issues-shield]][issues-url]
[![MIT License][license-shield]][license-url]


<!-- PROJECT LOGO -->
<br />
<div align="center">
  <a href="https://github.com/cbartram/kraken-loader-plugin">
    <img src="src/main/resources/com/kraken/images/kraken.png" alt="Logo" width="120" height="120">
  </a>

<h3 align="center">Kraken Launcher</h3>

  <p align="center">
   A RuneLite launcher which side-loads the Kraken essential plugins.
    <br />
</div>

Loads the latest version of the [Kraken Client](https://github.com/cbartram/kraken-client) from S3. This allows updates to be made to the Kraken client independently of user downloads.
With this tool users no longer need to constantly download the newest version of the [Kraken Client](https://github.com/cbartram/kraken-client) to play the game. Instead, they can download
this launcher JAR once, and it will automatically pull the latest [Kraken client](https://github.com/cbartram/kraken-client) from S3 for users.

This should be the primary way users launch the game if they intend to use Kraken plugins.

Currently, all plugins for the client are released for free at [our discord here](https://discord.gg/Jxfxr3Zme2). Some features this client offers are:

- Discord authentication and sign up / sign in
- Auto plugin loading (and automatic plugin & client updates)
- Jagex account compatibility
- Native RuneLite Client (no injection or client modification)

Although the Kraken client is safe and doesn't modify RuneLite in any way the plugins are unofficial. **We are not responsible for any bans you may incur for using this client.**
For more information about the Kraken Client see: [Kraken Client](#about-kraken-client).

# QuickStart

To get started with the launcher and get yourself some plugins join [our discord here](https://discord.gg/Jxfxr3Zme2) and create a new ticket!
You can view our currently available [plugins below](#current-plugins).

# Current Plugins

Currently, we have the following plugins enabled on the client:

| Plugin Name       | Plugin Description                                                               | Version |
|-------------------|----------------------------------------------------------------------------------|---------|
| Alchemical Hydra  | Tracks your prayers, special attacks and when to switch for Hydra.               | 1.0.2   |
| Cerberus          | Tracks ghosts, Cerberus prayer rotations, and more.                              | 1.0.0   |
| Effect Timers     | Tracks freeze, teleblock, and other timers!                                      | 1.0.0   |
| Chambers of Xeric | Tracks Olm rotations, specials, tick counters, and various boss helpers for CoX. | 1.0.4   |

## Getting Started

To get started with development on the kraken launcher, clone this repository with:

`git clone https://github.com/cbartram/kraken-launcher.git`

You can build the jar and run with:

```shell
gradle shadowJar

java -jar ./build/libs/kraken-launcher-<version>-all.jar
```

If you would like to run in developer mode to load Kraken plugins from the `/dev` prefix in S3 pass:

```shell
java -Ddeveloper-mode=true -jar ./build/libs/kraken-launcher-<version>-all.jar
```

Any args passed to the Kraken launcher jar will also be passed directly to the RuneLite client.

### Prerequisites

Install gradle in order to build and run this program:

- [Gradle](https://gradle.org/install/)

## Build & Dependencies

The [Kraken Client](https://github.com/cbartram/kraken-client) has several dependencies and a few custom images that it loads for its plugins. Its essential
that any dependencies the Kraken client has are shared with this repository because the launcher will launch the Kraken client using the same java classpath.

If any dependencies are not synced between the launcher and client then the client will fail.

## Jagex Launcher & Jagex Accounts

The Kraken Launcher is compatible with the Jagex accounts, but is not yet compatible with the launcher. In order to use the Kraken Launcher with Jagex accounts follow this guide:

- For Windows, run `RuneLite (configure)` from the start menu. Otherwise, pass `--configure` to the launcher (i.e. `/Applications/RuneLite.app/Contents/MacOS/RuneLite --configure` on Mac).
- In the Client arguments input box add `--insecure-write-credentials`
- Click Save
- Launch RuneLite normally via the Jagex launcher. RuneLite will write your launcher credentials to .runelite/credentials.properties.
- Launch Kraken via the Kraken Launcher, and it will use the saved credentials allowing you to use your Jagex account with Kraken plugins.

If you want to use a non-jagex account with Kraken you can delete the credentials.properties file to return your Kraken Client back to normal.
If for any reason you need to invalidate the credentials, you can use the "End sessions" button under account settings on runescape.com.

## Running the tests

Coming soon.

## Deployment

Add additional notes to deploy this on a live system

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

- RuneLite for making a great software! The SplashScreen was also taken from RuneLite!

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
