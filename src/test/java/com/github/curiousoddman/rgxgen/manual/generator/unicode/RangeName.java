package com.github.curiousoddman.rgxgen.manual.generator.unicode;

import java.util.Locale;
import java.util.regex.Pattern;

public class RangeName {
    private static final Pattern CHARS_TO_REMOVE = Pattern.compile("[)(,']+");
    public final         String  sectionName;
    public final         String  subrangeName;
    public final         String  combinedName;

    public RangeName(String sectionName, String subrangeName, int index) {
        this.sectionName = makeValidJavaConstantName(sectionName);
        this.subrangeName = makeValidJavaConstantName(subrangeName);
        if (index > 0) {
            combinedName = this.sectionName + '_' + this.subrangeName + '_' + index;
        } else {
            combinedName = this.sectionName + '_' + this.subrangeName;
        }
    }

    private static String makeValidJavaConstantName(String subrangeName) {
        String replaced = subrangeName
                .replace(' ', '_')
                .replace('-', '_');
        return CHARS_TO_REMOVE
                .matcher(replaced)
                .replaceAll("")
                .toUpperCase(Locale.ROOT);
    }

    public RangeName(String sectionName, String subrangeName) {
        this(sectionName, subrangeName, -1);
    }
}
