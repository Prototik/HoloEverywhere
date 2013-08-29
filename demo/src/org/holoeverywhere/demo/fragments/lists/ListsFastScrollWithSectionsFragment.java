
package org.holoeverywhere.demo.fragments.lists;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.holoeverywhere.demo.R;
import org.holoeverywhere.util.CharSequences;
import org.holoeverywhere.widget.ArrayAdapter;

import android.widget.SectionIndexer;

public class ListsFastScrollWithSectionsFragment extends ListsFastScrollFragment {
    private static final class CharacterWrapper {
        private char c;

        private CharacterWrapper(char c) {
            this.c = c;
        }

        @Override
        public String toString() {
            return Character.toString(c);
        }
    }

    private class CustomAdapter extends ArrayAdapter<CharSequence> implements SectionIndexer {
        private final Character[] mAlphabet;
        private final CharSequence[] mData;

        public CustomAdapter(CharSequence[] data) {
            super(getActivity(), R.layout.simple_list_item_1, android.R.id.text1);
            Arrays.sort(data, CHAR_SEQUENCE_COMPARATOR);
            addAll(mData = data);
            List<Character> alphabet = new ArrayList<Character>();
            for (CharSequence s : data) {
                if (s.length() == 0) {
                    continue;
                }
                char c = s.charAt(0);
                if (!alphabet.contains(c)) {
                    alphabet.add(c);
                }
            }
            Collections.sort(alphabet, CHARACTER_COMPARATOR);
            mAlphabet = alphabet.toArray(new Character[alphabet.size()]);
        }

        @Override
        public int getPositionForSection(int section) {
            char alphabetChar = mAlphabet[section];
            for (int i = 0; i < mData.length; i++) {
                CharSequence s = mData[i];
                if (s.length() == 0) {
                    continue;
                }
                if (s.charAt(0) == alphabetChar) {
                    return i;
                }
            }
            return -1;
        }

        @Override
        public int getSectionForPosition(int position) {
            CharSequence s = mData[position];
            if (s.length() == 0) {
                return -1;
            }
            char c = s.charAt(0);
            for (int i = 0; i < mAlphabet.length; i++) {
                if (mAlphabet[i] == c) {
                    return i;
                }
            }
            return -1;
        }

        @Override
        public CharacterWrapper[] getSections() {
            CharacterWrapper[] array = new CharacterWrapper[mAlphabet.length];
            for (int i = 0; i < mAlphabet.length; i++) {
                array[i] = new CharacterWrapper(mAlphabet[i]);
            }
            return array;
        }
    }

    private static final Comparator<CharSequence> CHAR_SEQUENCE_COMPARATOR = new Comparator<CharSequence>() {
        @Override
        public int compare(CharSequence lhs, CharSequence rhs) {
            return CharSequences.compareToIgnoreCase(lhs, rhs);
        }
    };

    private static final Comparator<Character> CHARACTER_COMPARATOR = new Comparator<Character>() {
        @Override
        public int compare(Character lhs, Character rhs) {
            return lhs.compareTo(rhs);
        }
    };

    @Override
    protected CharSequence getTitle() {
        return super.getTitle() + ": Sections";
    }

    @Override
    protected CustomAdapter onObtainData() {
        return new CustomAdapter(getResources().getTextArray(R.array.countries));
    }
}
