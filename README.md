# droidcon Vienna 2016 Conference App

Official app for droidcon Vienna 2016

![droidconat2016 Android screenshots][1]


## Build requirements

### Lombok

Install the Lombok plugin for your IDE before trying to build it.

### Firebase

Visit the [Firebase console](https://console.firebase.google.com/), create an app, and export its configuration to `app/google-services.json`

### Twitter

* Create an app on the [Twitter Apps console](https://apps.twitter.com/)
* Install the [Fabric](https://fabric.io/) IDE plugin, integrate Twitter Kit
* Update the `gradle.properties` file and update the following variables: `TWITTER_KEY`, `TWITTER_SECRET` with your Fabric consumer key, and consumer secret


## License

```
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```


[1]: https://raw.githubusercontent.com/Nilhcem/droidconat-2016/master/assets/screenshots/screenshots.jpg
