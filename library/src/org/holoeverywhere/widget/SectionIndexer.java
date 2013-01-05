
package org.holoeverywhere.widget;

public interface SectionIndexer {
    public int getPositionForSection(int section);

    public int getSectionForPosition(int position);

    public Object[] getSections();
}
