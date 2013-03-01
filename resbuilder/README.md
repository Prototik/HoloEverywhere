## ResBuilder
`ResBuilder` - maven module, which makes life easier in some aspects of development HoloEverywhere.

### Styles
First ever task of `ResBuilder` was a styles compiling basic on blocks.  
You define blocks with specified attrs - and resbuilder create theme for your applications.
Example:
```json
{
  "blocks": {
    "MyTheme": {
      "android:textSize": "14sp"
    },
    "MyThemeDark | MyTheme": {
      "android:windowBackground": "@drawable/bg_dark"
    },
    "MyThemeLight | MyTheme": {
      "android:windowBackground": "@drawable/bg_light"    
    }
  },
  "data": {
    "AppTheme | MyThemeDark < Holo.Theme": {},
    "AppTheme.Light | MyThemeLight < Holo.Theme.Light": {}
  }
}
```
will be compiled to:
```xml
<?xml version="1.0" encoding="utf-8"?><resources>
  <style name="AppTheme" parent="Holo.Theme">
    <item name="android:textSize">14sp</item>
    <item name="android:windowBackground">@drawable/bg_dark</item>
  </style>
  <style name="AppTheme.Light" parent="Holo.Theme.Light">
    <item name="android:textSize">14sp</item>
    <item name="android:windowBackground">@drawable/bg_light</item>
  </style>
</resources>
```

Of course, in this example using of `ResBuilder` a redundant. But if you compare library/res/values​​/styles.xml between it's parent library/resbuilder/styles-v4.json...

### Strings
Grab strings l10n from android sdk and can complement them.

### Attrs
Responsible for the attributes and styleable blocks.
