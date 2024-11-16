# kraken-launcher

Loads the latest version of the Kraken Client from S3. This allows updates to be made to the Kraken client independently of user downloads.
With this tool users no longer need to constantly download the newest version of the Kraken Client to play the game. Instead they can download
this launcher JAR once, and it will automatically pull the latest Kraken client from S3 for users. 

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