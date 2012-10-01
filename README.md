# HoloEverywhere v1.2.4
## What is it?
Bringing Holo Theme from Android 4.1 to 1.6 and above.
## How to use?

```
git clone git://github.com/ChristopheVersieux/HoloEverywhere.git
cd HoloEverywhere
git submodule update --init
```

* You should clone git repo, init and update submodules:

```
git clone git://github.com/ChristopheVersieux/HoloEverywhere.git HoloEverywhere
cd HoloEverywhere
git submodule update --init
```

You also may use git GUI programs
* Import HoloEverywhere from root folder and ActionBarSherlock from contrib folder into Eclipse

* Add HoloEverywhere project as library into your project (Properties/Android/Library/Add)

* Add next theme declaration:

```
android:theme="@style/Holo.Theme.Sherlock"
```

in your application manifest
Example:

```
<application
  android:name=".Application"
  android:icon="@drawable/icon"
  android:label="@string/app_name"
  android:theme="@style/Holo.Theme.Sherlock" >
```

Also you can use Holo.Theme.Sherlock.Light for light theme and Holo.Theme.Sherlock.Light.DarkActionBar for light theme with dark action bar.

* Extend the Activities from com.WazaBe.HoloEverywhere.sherlock.S***Activity

Example:

```
public class MainActivity extends com.WazaBe.HoloEverywhere.sherlock.SListActivity { ...
```
Also you should cast view to with the same name from package com.WazaBe.HoloEverywhere.widget, if possible. This, for example, ProgressBar and Spinner.

## Contact
Christophe: [Google Plus](https://plus.google.com/108315424589085456181/posts "Google Plus")

Sergey: [GMail](mailto:prototypegamez@gmail.com "Send email to Sergey")

## Notice for developers
If you make changes in styles.xml - use the Builder, otherwise pull request will not be accepted.

## Screenshots
![Screenshot 1](https://raw.github.com/ChristopheVersieux/HoloEverywhere/master/website/screen1.png "Screenshot 1")
![Screenshot 2](https://raw.github.com/ChristopheVersieux/HoloEverywhere/master/website/screen2.png "Screenshot 2")
![Screenshot 3](https://raw.github.com/ChristopheVersieux/HoloEverywhere/master/website/screen3.png "Screenshot 3")
![Screenshot 4](https://raw.github.com/ChristopheVersieux/HoloEverywhere/master/website/screen4.png "Screenshot 4")
![Screenshot 5](https://raw.github.com/ChristopheVersieux/HoloEverywhere/master/website/screen5.png "Screenshot 5")
![Screenshot 6](https://raw.github.com/ChristopheVersieux/HoloEverywhere/master/website/screen6.png "Screenshot 6")
![Screenshot 7](https://raw.github.com/ChristopheVersieux/HoloEverywhere/master/website/screen7.png "Screenshot 7")
![Screenshot 8](https://raw.github.com/ChristopheVersieux/HoloEverywhere/master/website/screen8.png "Screenshot 8")

### License

Copyright (c) 2012 Christophe Versieux, Sergey Shatunov

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
