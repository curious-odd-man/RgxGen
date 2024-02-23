package com.github.curiousoddman.rgxgen.manual.generator.unicode;

import java.util.Locale;

public class RangeName {
    public final String sectionName;
    public final String subrangeName;
    public final String combinedName;

    public RangeName(String sectionName, String subrangeName, int index) {
        this.sectionName = sectionName.replace(' ', '_').toUpperCase(Locale.ROOT);
        this.subrangeName = subrangeName.replace(' ', '_').toUpperCase(Locale.ROOT);
        if (index > 0) {
            combinedName = this.sectionName + '_' + this.subrangeName + '_' + index;
        } else {
            combinedName = this.sectionName + '_' + this.subrangeName;
        }
    }

    public RangeName(String sectionName, String subrangeName) {
        this(sectionName, subrangeName, -1);
    }
}
