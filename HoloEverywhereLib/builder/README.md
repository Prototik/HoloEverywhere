# Builder?
### What is it?

Builder will generate a styles.xml with the necessary styles.

### Why? So everything works fine.

Yes, but styles.xml contains many same attributes and was the largest in size.
It's hard to edit and develop.

### How does it work?

Very simple - preprocessing XML.
You define the same data blocks, names them, and in the right place to refer to these names.

### Can be an example?

Please:
Source file:
```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
  <define name="MyData">
    <item name="android:windowBackground">@drawable/my_background</item>
    <item name="android:listViewStyle">@style/MyListViewStyle</item>
  </define>

  <style name="MyTheme">
    <include name="MyData"/>
  </style>
  <style name="MySecondTheme">
    <include name="MyData"/>
    <item name="android:textColor">#FF999999</item>
  </style>
</resources>
```
Final file:
```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
  <style name="MyTheme">
    <item name="android:windowBackground">@drawable/my_background</item>
    <item name="android:listViewStyle">@style/MyListViewStyle</item>
  </style>
  <style name="MySecondTheme">
    <item name="android:windowBackground">@drawable/my_background</item>
    <item name="android:listViewStyle">@style/MyListViewStyle</item>
    <item name="android:textColor">#FF999999</item>
  </style>
</resources>
```
