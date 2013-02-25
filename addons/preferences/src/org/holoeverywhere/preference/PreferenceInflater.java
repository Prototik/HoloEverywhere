
package org.holoeverywhere.preference;

import java.io.IOException;

import org.holoeverywhere.internal.GenericInflater;
import org.holoeverywhere.util.XmlUtils;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;

public class PreferenceInflater extends GenericInflater<Preference, PreferenceGroup> {
    private static final String EXTRA_TAG_NAME = "extra";
    private static final String INTENT_TAG_NAME = "intent";
    private PreferenceManager mPreferenceManager;

    public PreferenceInflater(Context context,
            PreferenceManager preferenceManager) {
        super(Preference.context(context));
        init(preferenceManager);
    }

    public PreferenceInflater(PreferenceInflater original,
            PreferenceManager preferenceManager, Context newContext) {
        super(original, Preference.context(newContext));
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
                intent = Intent.parseIntent(getContext().getResources(),
                        parser, attrs);
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
            getContext().getResources().parseBundleExtra(
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
