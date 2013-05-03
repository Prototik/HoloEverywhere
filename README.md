![HoloEverywhere](http://holoeverywhere.org/github-res/logo.png "HoloEverywhere")  
[![Build Status](https://travis-ci.org/Prototik/HoloEverywhere.png?branch=master)](https://travis-ci.org/Prototik/HoloEverywhere)

## What is it?
Bringing Holo Theme from Android 4.1 to 2.1 and above.
## Links
[![Play Store](http://holoeverywhere.org/github-res/play_store_button.png)][Play Store]  
[![Donate](http://holoeverywhere.org/github-res/donate_button.png)][Donate]

## Screenshots
![Screenshot 1](http://holoeverywhere.org/img/screenshots/1.png "Screenshot 1")
![Screenshot 2](http://holoeverywhere.org/img/screenshots/2.png "Screenshot 2")
![Screenshot 3](http://holoeverywhere.org/img/screenshots/3.png "Screenshot 3")
![Screenshot 4](http://holoeverywhere.org/img/screenshots/4.png "Screenshot 4")
![Screenshot 5](http://holoeverywhere.org/img/screenshots/5.png "Screenshot 5")
![Screenshot 6](http://holoeverywhere.org/img/screenshots/6.png "Screenshot 6")
![Screenshot 7](http://holoeverywhere.org/img/screenshots/7.png "Screenshot 7")
![Screenshot 8](http://holoeverywhere.org/img/screenshots/8.png "Screenshot 8")
![Screenshot 9](http://holoeverywhere.org/img/screenshots/9.png "Screenshot 9")
![Screenshot 10](http://holoeverywhere.org/img/screenshots/10.png "Screenshot 10")

## Featured implementations
 * [Project Euler](https://play.google.com/store/apps/details?id=ie.cathalcoffey.android.projecteuler)
 * [Meditation Assistant](https://play.google.com/store/apps/details?id=sh.ftp.rocketninelabs.meditationassistant)
 * [Ragnabase MVP](https://play.google.com/store/apps/details?id=com.ragnabase.mvp)

Write to [Sergey](mailto:prototypegamez@gmail.com) for add your application to this list.

## How to use? ([Maven][Build with Maven])

* You should clone git stable branch, init and update submodules:

```
git clone --branch stable git://github.com/Prototik/HoloEverywhere.git HoloEverywhere
cd HoloEverywhere
git submodule update --init --recursive
```
Git will be swear on detached HEAD state, ignore it.

You also may use git GUI programs

* Replace `contrib/ActionBarSherlock/library/libs/android-support-v4.jar` by `support-library/android-support-v4-r12.jar`

* Import ActionBarSherlock from `contrib/ActionBarSherlock/library` folder

* Import HoloEverywhere from `library` folder

* Add `HoloEverywhere Library` project as library into your project (Properties/Android/Library/Add)

* Extend the Activities from `org.holoeverywhere.app.***Activity`

Example:

```
import org.holoeverywhere.app.ListActivity;

public class MainActivity extends ListActivity { ...
```
Also you should cast view to with the same name from package `org.holoeverywhere.widget`, if possible. This, for example, ProgressBar and Spinner:
```
import org.holoeverywhere.widget.ProgressBar;

ProgressBar bar = (ProgressBar) findViewById(R.id.progress_bar);
```

## Contact

Sergey:
  * [Email](mailto:prototypegamez@gmail.com "Send email to Sergey")
  * [Google Plus](https://plus.google.com/103272077758668000975/posts "Google Plus")
  * [Habrahabr](http://habrahabr.ru/users/prototik/)
  
Christophe:
  * [Google Plus](https://plus.google.com/108315424589085456181/posts "Google Plus")

### License
LGPLv3, full text of license see [here][License]

[Play Store]: https://play.google.com/store/apps/details?id=org.holoeverywhere.demo "Play Store"
[Donate]: https://www.paypal.com/cgi-bin/webscr?cmd=_donations&business=X7E7U7HNR36YN&lc=US&item_name=HoloEverywhere&currency_code=USD&bn=PP%2dDonationsBF%3adonate_button%2epng%3aNonHosted "Donate"
[Build with Maven]: https://github.com/Prototik/HoloEverywhere/wiki/Maven "Build with maven"
[License]: https://raw.github.com/Prototik/HoloEverywhere/master/LICENSE "LGPLv3"
