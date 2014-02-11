/*
 * The MIT License
 *
 * Copyright (c) 2011, Axel Haustant
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package jenkins.plugins.extracolumns;

import org.kohsuke.stapler.DataBoundConstructor;

import hudson.Extension;
import hudson.model.AbstractItem;
import hudson.views.ListViewColumnDescriptor;
import hudson.views.ListViewColumn;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DescriptionColumn extends ListViewColumn {

    private boolean displayName;
    private boolean trim;
    private int displayLength; //numbers of lines to display
    private int columnWidth;
    private boolean isForceWidth;
    private boolean regex;
    private String expression;
    private int group;

    private final static String SEPARATOR = "<br/>";
    private final static String SEPARATORS_REGEX = "(?i)<br\\s*/>|<br>";

    @DataBoundConstructor
    public DescriptionColumn(boolean displayName, boolean trim, int displayLength, int columnWidth, boolean isForceWidth, boolean regex, String expression, int group) {
        super();
        this.displayName = displayName;
        this.trim = trim;
        this.displayLength = displayLength;
        this.columnWidth = columnWidth;
        this.isForceWidth = isForceWidth;
        this.regex = regex;
        this.expression = expression;
        this.group = group;
    }

    public DescriptionColumn() {
        this(false, false, 1, 80, false, false, "", 0);
    }
    
    public boolean isDisplayName() {
        return displayName;
    }

    public boolean isTrim() {
        return trim;
    }

    public int getDisplayLength() {
        return displayLength;
    }

    public int getColumnWidth() {
        return columnWidth;
    }
    
    public boolean isForceWidth() {
        return isForceWidth;
    }
    
    public boolean isRegex() {
        return regex;
    }
    
    public String getExpression() {
        return expression;
    }
    
    public int getGroup() {
        return group;
    }

    public String getToolTip(@SuppressWarnings("rawtypes") AbstractItem job) {
        return formatDescription(job, false);
    }
    
    public String getDescription(@SuppressWarnings("rawtypes") AbstractItem job){
        return formatDescription(job, isTrim());
    }
    
    private String formatDescription(@SuppressWarnings("rawtypes") AbstractItem job, boolean trimIt) {
        if (job == null) {
            return null;
        }
        if (job.getDescription() == null) {
            return "";
        }

        if(regex) {
            Pattern pattern = Pattern.compile(expression);
            Matcher matcher = pattern.matcher(job.getDescription());
            return matcher.group(group);
        } else {
            StringBuffer sb = new StringBuffer();
            if (!trimIt) {
                sb.append(job.getDescription());
            } else {
                String[] parts = job.getDescription().split(SEPARATORS_REGEX);
                for (int i = 0; i < displayLength && i < parts.length; i++) {
                    if (i != 0) {
                        sb.append(SEPARATOR);
                    }
                    sb.append(parts[i]);
                }
            }

            return sb.toString();
        }
    }

    @Extension
    public static class DescriptorImpl extends ListViewColumnDescriptor {

        @Override
        public boolean shownByDefault() {
            return false;
        }

        @Override
        public String getDisplayName() {
            return Messages.DescriptionColumn_DisplayName();
        }

        @Override
        public String getHelpFile() {
            return "/plugin/extra-columns/help-description-column.html";
        }
    }
}
