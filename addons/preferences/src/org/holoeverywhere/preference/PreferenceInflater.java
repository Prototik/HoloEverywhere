
package org.holoeverywhere.preference;

import java.io.IOException;

import org.holoeverywhere.internal.GenericInflater;
import org.holoeverywhere.util.XmlUtils;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.net.Uri;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.TypedValue;

public class PreferenceInflater extends GenericInflater<Preference, PreferenceGroup> {
    private static final String EXTRA_TAG_NAME = "extra";
    private static final String INTENT_TAG_NAME = "intent";

    private static void parseBundleExtra(Resources resources, String tagName, AttributeSet attrs,
            Bundle outBundle) throws XmlPullParserException {
        TypedArray sa = resources.obtainAttributes(attrs, R.styleable.Extra);
        String name = sa.getString(R.styleable.Extra_name);
        if (name == null) {
            sa.recycle();
            throw new XmlPullParserException("<" + tagName
                    + "> requires an holo:name attribute at "
                    + attrs.getPositionDescription());
        }
        TypedValue v = sa.peekValue(R.styleable.Extra_value);
        if (v != null) {
            if (v.type == TypedValue.TYPE_STRING) {
                CharSequence cs = v.coerceToString();
                outBundle.putCharSequence(name, cs);
            } else if (v.type == TypedValue.TYPE_INT_BOOLEAN) {
                outBundle.putBoolean(name, v.data != 0);
            } else if (v.type >= TypedValue.TYPE_FIRST_INT
                    && v.type <= TypedValue.TYPE_LAST_INT) {
                outBundle.putInt(name, v.data);
            } else if (v.type == TypedValue.TYPE_FLOAT) {
                outBundle.putFloat(name, v.getFloat());
            } else {
                sa.recycle();
                throw new XmlPullParserException("<" + tagName
                        + "> only supports string, integer, float, color, and boolean at "
                        + attrs.getPositionDescription());
            }
        } else {
            sa.recycle();
            throw new XmlPullParserException("<" + tagName
                    + "> requires an holo:value attribute at " + attrs.getPositionDescription());
        }
        sa.recycle();
    }

    private static Intent parseIntent(Resources resources, XmlPullParser parser, AttributeSet attrs)
            throws XmlPullParserException, IOException {
        Intent intent = new Intent();
        TypedArray sa = resources.obtainAttributes(attrs,
                R.styleable.Intent);
        intent.setAction(sa.getString(R.styleable.Intent_action));
        String data = sa.getString(R.styleable.Intent_data);
        String mimeType = sa.getString(R.styleable.Intent_mimeType);
        intent.setDataAndType(data != null ? Uri.parse(data) : null, mimeType);
        String packageName = sa.getString(R.styleable.Intent_targetPackage);
        String className = sa.getString(R.styleable.Intent_targetClass);
        if (packageName != null && className != null) {
            intent.setComponent(new ComponentName(packageName, className));
        }
        sa.recycle();
        int outerDepth = parser.getDepth();
        int type;
        while ((type = parser.next()) != XmlPullParser.END_DOCUMENT
                && (type != XmlPullParser.END_TAG || parser.getDepth() > outerDepth)) {
            if (type == XmlPullParser.END_TAG || type == XmlPullParser.TEXT) {
                continue;
            }
            String nodeName = parser.getName();
            if (nodeName.equals("category")) {
                sa = resources.obtainAttributes(attrs, R.styleable.IntentCategory);
                String cat = sa.getString(R.styleable.IntentCategory_name);
                sa.recycle();
                if (cat != null) {
                    intent.addCategory(cat);
                }
                XmlUtils.skipCurrentTag(parser);
            } else if (nodeName.equals("extra")) {
                Bundle bundle = new Bundle();
                parseBundleExtra(resources,
                        "extra", attrs, bundle);
                intent.putExtras(bundle);
                XmlUtils.skipCurrentTag(parser);
            } else {
                XmlUtils.skipCurrentTag(parser);
            }
        }
        return intent;
    }

    private PreferenceManager mPreferenceManager;

    public PreferenceInflater(Context context, PreferenceManager preferenceManager) {
        super(PreferenceInit.context(context));
        init(preferenceManager);
    }

    public PreferenceInflater(PreferenceInflater original,
            PreferenceManager preferenceManager, Context newContext) {
        super(original, PreferenceInit.context(newContext));
        init(preferenceManager);
    }

    @Override
    public PreferenceInflater cloneInContext(Context newContext) {
        return new PreferenceInflater(this, mPreferenceManager, newContext);
    }

    private void init(PreferenceManager preferenceManager) {
        mPreferenceManager = preferenceManager;
        registerPackage(PreferenceInit.PACKAGE);
    }

    @Override
    protected boolean onCreateCustomFromTag(XmlPullParser parser,
            Preference parentPreference, AttributeSet attrs)
            throws XmlPullParserException {
        final String tag = parser.getName();
        if (tag.equals(PreferenceInflater.INTENT_TAG_NAME)) {
            Intent intent = null;
            try {
                intent = parseIntent(getContext().getResources(), parser, attrs);
            } catch (IOException e) {
                XmlPullParserException ex = new XmlPullParserException(
                        "Error parsing preference");
                ex.initCause(e);
                throw ex;
            }
            if (intent != null) {
                parentPreference.setIntent(intent);
            }

            return true;
        } else if (tag.equals(PreferenceInflater.EXTRA_TAG_NAME)) {
            parseBundleExtra(getContext().getResources(),
                    PreferenceInflater.EXTRA_TAG_NAME, attrs,
                    parentPreference.getExtras());
            try {
                XmlUtils.skipCurrentTag(parser);
            } catch (IOException e) {
                XmlPullParserException ex = new XmlPullParserException(
                        "Error parsing preference");
                ex.initCause(e);
                throw ex;
            }
            return true;
        }

        return false;
    }

    @Override
    protected PreferenceGroup onMergeRoots(PreferenceGroup givenRoot,
            boolean attachToGivenRoot, PreferenceGroup xmlRoot) {
        if (givenRoot == null) {
            xmlRoot.onAttachedToHierarchy(mPreferenceManager);
            return xmlRoot;
        } else {
            return givenRoot;
        }
    }
}
