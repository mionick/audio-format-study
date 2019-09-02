# Audio Research 

This project is mean to examine the Audio file formats, re-familiarize myself with Java (specifically Java 8), and get more experience manipulating and working with binary data. 
Main goal as of now is to successfully decode and play an mp3 audio file though, I'm going to start with a simpler .wav format. 

## Lessons
### ID3 Tags

An MP3 file, like other audio formats can be wrapped in a ID3 Tag.
The ID3 specification is here: http://id3.org/id3v2.4.0-structure

It consists of a header which gives the size, one or more frames of tag data, and some padding.
It can be mostly read as text data with a few special characters, which makes it fairly easy to identify in a text editor. 

The ID3 tag can either be prepended or appended to the audio data. 

Once that has been stripped off, the rest of the data is the actual MP3 format. 

## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes. See deployment for notes on how to deploy the project on a live system.

### Prerequisites

What things you need to install the software and how to install them

```
Give examples
```

### Installing

A step by step series of examples that tell you how to get a development env running

Say what the step will be

```
Give the example
```

And repeat

```
until finished
```

End with an example of getting some data out of the system or using it for a little demo

## Running the tests

Explain how to run the automated tests for this system

### Break down into end to end tests

Explain what these tests test and why

```
Give an example
```

### And coding style tests

Explain what these tests test and why

```
Give an example
```

## Deployment

Add additional notes about how to deploy this on a live system

## Built With

* [Dropwizard](http://www.dropwizard.io/1.0.2/docs/) - The web framework used
* [Maven](https://maven.apache.org/) - Dependency Management
* [ROME](https://rometools.github.io/rome/) - Used to generate RSS Feeds

## Contributing

Please read [CONTRIBUTING.md](https://gist.github.com/PurpleBooth/b24679402957c63ec426) for details on our code of conduct, and the process for submitting pull requests to us.

## Versioning

We use [SemVer](http://semver.org/) for versioning. For the versions available, see the [tags on this repository](https://github.com/your/project/tags). 

## Authors

* **Billie Thompson** - *Initial work* - [PurpleBooth](https://github.com/PurpleBooth)

See also the list of [contributors](https://github.com/your/project/contributors) who participated in this project.

## License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details

## Acknowledgments

* Hat tip to anyone whose code was used
* Inspiration
* etc
