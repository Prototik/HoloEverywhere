[HoloEverywhere](https://raw.github.com/ChristopheVersieux/HoloEverywhere/master/website/logo.png)

## What is it?
Bringing Holo Theme from Android 4.1 to 2.1 and above.
## Links
[![Play Store](https://raw.github.com/ChristopheVersieux/HoloEverywhere/master/website/play_store_button.png)][Play Store]  
[![Donate](https://raw.github.com/ChristopheVersieux/HoloEverywhere/master/website/donate_button.png)][Donate]
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
![Screenshot 1](https://raw.github.com/ChristopheVersieux/HoloEverywhere/master/website/screen1.png "Screenshot 1")
![Screenshot 2](https://raw.github.com/ChristopheVersieux/HoloEverywhere/master/website/screen2.png "Screenshot 2")
![Screenshot 3](https://raw.github.com/ChristopheVersieux/HoloEverywhere/master/website/screen3.png "Screenshot 3")
![Screenshot 4](https://raw.github.com/ChristopheVersieux/HoloEverywhere/master/website/screen4.png "Screenshot 4")
![Screenshot 5](https://raw.github.com/ChristopheVersieux/HoloEverywhere/master/website/screen5.png "Screenshot 5")
![Screenshot 6](https://raw.github.com/ChristopheVersieux/HoloEverywhere/master/website/screen6.png "Screenshot 6")
![Screenshot 7](https://raw.github.com/ChristopheVersieux/HoloEverywhere/master/website/screen7.png "Screenshot 7")
![Screenshot 8](https://raw.github.com/ChristopheVersieux/HoloEverywhere/master/website/screen8.png "Screenshot 8")
![Screenshot 9](https://raw.github.com/ChristopheVersieux/HoloEverywhere/master/website/screen9.png "Screenshot 9")
![Screenshot 10](https://raw.github.com/ChristopheVersieux/HoloEverywhere/master/website/screen10.png "Screenshot 10")

## Contact
[Christophe](https://plus.google.com/108315424589085456181/posts "Google Plus")

[Sergey](mailto:prototypegamez@gmail.com "Send email to Sergey")

### Notice for developers
If you make changes in styles.xml - use the [Builder](https://github.com/ChristopheVersieux/HoloEverywhere/tree/master/builder), otherwise pull request will not be accepted.
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

Copyright (c) 2012 Christophe Versieux, Sergey Shatunov

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

[Play Store]: https://play.google.com/store/apps/details?id=org.holoeverywhere.demo "Play Store"
[Donate]: https://www.paypal.com/cgi-bin/webscr?cmd=_donations&business=X7E7U7HNR36YN&lc=US&item_name=HoloEverywhere&currency_code=USD&bn=PP%2dDonationsBF%3adonate_button%2epng%3aNonHosted "Donate"
[Build with Maven]: https://github.com/ChristopheVersieux/HoloEverywhere/wiki/Maven "Build with maven"
