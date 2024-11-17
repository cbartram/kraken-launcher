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

<h3 align="center">Kraken Client</h3>

  <p align="center">
   A RuneLite launcher which side-loads the Kraken essential plugins.
    <br />
</div>

# Kraken Launcher

Loads the latest version of the [Kraken Client](https://github.com/cbartram/kraken-client) from S3. This allows updates to be made to the Kraken client independently of user downloads.
With this tool users no longer need to constantly download the newest version of the [Kraken Client](https://github.com/cbartram/kraken-client) to play the game. Instead, they can download
this launcher JAR once, and it will automatically pull the latest [Kraken client](https://github.com/cbartram/kraken-client) from S3 for users.

This should be the primary way users launch the game if they intend to use Kraken plugins.

Although the Kraken client is safe and doesn't modify RuneLite in any way the plugins are unofficial. **We are not responsible for any bans you may incur for using this client.**
For more information about the Kraken Client see: [Kraken Client](#about-kraken-client).

## Getting Started

To get started clone this repository with:

`git clone https://github.com/cbartram/kraken-launcher.git`

You can build the jar and run with:

```shell
gradle shadowJar

java -jar ./build/libs/kraken-launcher-<version>-all.jar
```

### Prerequisites

Install gradle in order to build and run this program:

- [Gradle](https://gradle.org/install/)


## Build & Dependencies

The [Kraken Client](https://github.com/cbartram/kraken-client) has several dependencies and a few custom images that it loads for its plugins. Its essential
that any dependencies the Kraken client has are shared with this repository because the launcher will launch the Kraken client using the same java classpath.

If any dependencies are not synced between the launcher and client then the client will fail.

## Jagex Launcher

The Kraken Launcher is compatible with the Jagex launcher. In order to use the Kraken Launcher from the Jagex Launcher perform the following
steps based on your OS.

### Windows

- Go to your RuneLite directory: `C:\Users\<YOUR_USER>\AppData\Local\RuneLite`
- Copy the `kraken-launcher-<version>-all.jar` file to your RuneLite directory (listed above)
- Re-name the `kraken-launcher-<version>-all.jar` to `RuneLite.jar`.
- When launching RuneLite through the Jagex Launcher it will launch the Kraken Client instead.

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