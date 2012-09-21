# HoloEverywhere
## What is it?
Bringing Holo Theme from Android 4.0 to 1.6 and above.
## How to use?
```
git clone git://github.com/ChristopheVersieux/HoloEverywhere.git
cd HoloEverywhere
git submodule update --init
```
* Import HoloEverywhere and ActionBarSherlock into Eclipse
* Add HoloEverywhere project as library into your project (Properties/Android/Library/Add)
* Add android:theme="@style/Holo.Theme.Sherlock" in your application manifest, you may also want to use light theme: Holo.Theme.Sherlock.Light
* Extend the Application object from com.WazaBe.Holo.Everywhere.Application (optional)
* Extend the Activities from com.WazaBe.HoloEverywhere.sherlock.S\*\*\*Activity (example com.WazaBe.HoloEverywhere.sherlock.SListActivity)

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

##### THE BEER-WARE LICENSE
As long as you retain this notice you can do whatever you want with this stuff.
If we meet some day, and you think this stuff is worth it, you can buy me a beer in return.
I also accept Paypal donations to christophe.versieux@gmail.com (you may offer me multiple beers as they are very cheap in Belgium)
