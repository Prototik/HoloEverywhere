![HoloEverywhere](http://holoeverywhere.org/github-res/logo.png "HoloEverywhere")

## What is it?
Bringing Holo Theme from Android 4.1 to 2.1 and above.
## Links
[![Play Store](http://holoeverywhere.org/github-res/play_store_button.png)][Play Store]  
[![Donate](http://holoeverywhere.org/github-res/donate_button.png)][Donate]
## How to use? ([Maven][Build with Maven])

* You should clone git repo, init and update submodules:

```
git clone --branch stable git://github.com/ChristopheVersieux/HoloEverywhere.git HoloEverywhere
cd HoloEverywhere
git submodule update --init --recursive
```
Git will be swear on detached HEAD state, ignore it.

You also may use git GUI programs

* Import HoloEverywhere from `library` folder and ActionBarSherlock from `contrib/ActionBarSherlock/library` folder into Eclipse

* Add HoloEverywhere project as library into your project (Properties/Android/Library/Add)

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

## Contact
[Christophe](https://plus.google.com/108315424589085456181/posts "Google Plus")

[Sergey](mailto:prototypegamez@gmail.com "Send email to Sergey")

### Notice for developers
If you make changes in styles.xml - use the [resbuilder](https://github.com/ChristopheVersieux/HoloEverywhere/tree/master/resbuilder), otherwise pull request will not be accepted.
And before commiting your changes check HEAD - it should be reference on master branch.
```
$ git branch
* master
```
If you see it:
```
$ git branch
* (no branch)
  master
```
Your HEAD in detached state. Stash your changes, switch branch and apply stash:
```
git stash
git checkout master
git stash apply
```

### Demo notice
In demo using music "Winter Dawn" by machinimasound.com. Licensed under Creative Commons "Attribution 3.0"

### License
LGPLv3, full text of license see [here][License]

[Play Store]: https://play.google.com/store/apps/details?id=org.holoeverywhere.demo "Play Store"
[Donate]: https://www.paypal.com/cgi-bin/webscr?cmd=_donations&business=X7E7U7HNR36YN&lc=US&item_name=HoloEverywhere&currency_code=USD&bn=PP%2dDonationsBF%3adonate_button%2epng%3aNonHosted "Donate"
[Build with Maven]: https://github.com/ChristopheVersieux/HoloEverywhere/wiki/Maven "Build with maven"
[License]: https://raw.github.com/ChristopheVersieux/HoloEverywhere/master/LICENSE "LGPLv3"
