/*
 * Copyright 2016 Rudy Alex Kohn [s133235@student.dtu.dk]
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.undyingideas.thor.skafottet.support.utility;

/**
 * Created on 14-01-2016, 16:36.
 * Project : skafottet
 * Copy from https://github.com/VerbalExpressions/JavaVerbalExpressions
 *
 * Modified to be a bit more Android friendly.
 * Currently not used
 * @author rudz
 */

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.String.valueOf;

public class VerbalExpression {

    private final Pattern pattern;

    @SuppressWarnings("ReturnOfThis")
    public static class Builder {

        private static final Pattern patEndOfLine = Pattern.compile("$", Pattern.LITERAL);
        private static final Pattern patSanitize = Pattern.compile("[\\W]");
        private static final Pattern patStartOfLine = Pattern.compile("^", Pattern.LITERAL);
        private StringBuilder prefixes = new StringBuilder();
        private final StringBuilder source = new StringBuilder();
        private StringBuilder suffixes = new StringBuilder();
        private int modifiers = Pattern.MULTILINE;

        /**
         * Package private. Use {@link #regex()} to build a new one
         *
         * @since 1.2
         */
        Builder() {
        }

        /**
         * Escapes any non-word char with two backslashes
         * used by any method, except {@link #add(String)}
         *
         * @param pValue - the string for char escaping
         * @return sanitized string value
         */
        private static String sanitize(final String pValue) {
            return patSanitize.matcher(pValue).replaceAll("\\\\$0");
        }

        /**
         * Counts occurrences of some substring in whole string
         * Same as org.apache.commons.lang3.StringUtils#countMatches(String, java.lang.String)
         * by effect. Used to count braces for {@link #or(String)} method
         *
         * @param where - where to find
         * @param what  - what needs to count matches
         * @return 0 if nothing found, count of occurrences instead
         */
        private static int countOccurrencesOf(final String where, final String what) {
            return (where.length() - where.replace(what, "").length()) / what.length();
        }

        public VerbalExpression build() {
            return new VerbalExpression(Pattern.compile(prefixes.append(source).append(suffixes).toString(), modifiers));
        }

        /**
         * Append literal expression
         * Everything added to the expression should go trough this method
         * (keep in mind when creating your own methods).
         * All existing methods already use this, so for basic usage, you can just ignore this method.
         * <p/>
         * Example:
         * regex().add("\n.*").build() // produce exact "\n.*" regexp
         *
         * @param pValue - literal expression, not sanitized
         * @return this builder
         */
        public Builder add(final String pValue) {
            source.append(pValue);
            return this;
        }

        /**
         * Append a regex from builder and wrap it with unnamed group (?: ... )
         *
         * @param regex - VerbalExpression.Builder, that not changed
         * @return this builder
         * @since 1.2
         */
        public Builder add(final Builder regex) {
            return group().add(regex.build().toString()).endGr();
        }

        /**
         * Enable or disable the expression to start at the beginning of the line
         *
         * @param pEnable - enables or disables the line starting
         * @return this builder
         */
        public Builder startOfLine(final boolean pEnable) {
            prefixes.append(pEnable ? "^" : "");
            if (!pEnable) {
                prefixes = new StringBuilder(patStartOfLine.matcher(prefixes.toString()).replaceAll(""));
            }
            return this;
        }

        /**
         * Mark the expression to start at the beginning of the line
         * Same as {@link #startOfLine(boolean)} with true arg
         *
         * @return this builder
         */
        public Builder startOfLine() {
            return startOfLine(true);
        }

        /**
         * Enable or disable the expression to end at the last character of the line
         *
         * @param pEnable - enables or disables the line ending
         * @return this builder
         */
        public Builder endOfLine(final boolean pEnable) {
            suffixes.append(pEnable ? '$' : "");
            if (!pEnable) {
                suffixes = new StringBuilder(patEndOfLine.matcher(suffixes.toString()).replaceAll(""));
            }
            return this;
        }

        /**
         * Mark the expression to end at the last character of the line
         * Same as {@link #endOfLine(boolean)} with true arg
         *
         * @return this builder
         */
        public Builder endOfLine() {
            return endOfLine(true);
        }

        /**
         * Add a string to the expression
         *
         * @param pValue - the string to be looked for (sanitized)
         * @return this builder
         */
        public Builder then(final String pValue) {
            return add("(?:" + sanitize(pValue) + ')');
        }

        /**
         * Add a string to the expression
         * Syntax sugar for {@link #then(String)} - use it in case:
         * regex().find("string") // when it goes first
         *
         * @param value - the string to be looked for (sanitized)
         * @return this builder
         */
        public Builder find(final String value) {
            return then(value);
        }

        /**
         * Add a string to the expression that might appear once (or not)
         * Example:
         * The following matches all strings that contain http:// or https://
         * VerbalExpression regex = regex()
         * .find("http")
         * .maybe("s")
         * .then("://")
         * .anythingBut(" ").build();
         * regex.test("http://")    //true
         * regex.test("https://")   //true
         *
         * @param pValue - the string to be looked for
         * @return this builder
         */
        public Builder maybe(final String pValue) {
            return then(pValue).add("?");
        }

        /**
         * Add a regex to the expression that might appear once (or not)
         * Example:
         * The following matches all names that have a prefix or not.
         * VerbalExpression.Builder namePrefix = regex().oneOf("Mr.", "Ms.");
         * VerbalExpression name = regex()
         *	.maybe(namePrefix)
         *	.space()
         *	.zeroOrMore()
         *	.word()
         *	.oneOrMore()
         *	.build();
         * regex.test("Mr. Bond/")    //true
         * regex.test("James")   //true
         *
         * @param regex - the string to be looked for
         * @return this builder
         */
        public Builder maybe(final Builder regex) {
            return group().add(regex).endGr().add("?");
        }

        /**
         * Add expression that matches anything (includes empty string)
         *
         * @return this builder
         */
        public Builder anything() {
            return add("(?:.*)");
        }

        /**
         * Add expression that matches anything, but not passed argument
         *
         * @param pValue - the string not to match
         * @return this builder
         */
        public Builder anythingBut(final String pValue) {
            return add("(?:[^" + sanitize(pValue) + "]*)");
        }

        /**
         * Add expression that matches something that might appear once (or more)
         *
         * @return this builder
         */
        public Builder something() {
            return add("(?:.+)");
        }

        public Builder somethingButNot(final String pValue) {
            return add("(?:[^" + sanitize(pValue) + "]+)");
        }

        /**
         * Add universal line break expression
         *
         * @return this builder
         */
        public Builder lineBreak() {
            return add("(?:\\n|(?:\\r\\n))");
        }

        /**
         * Shortcut for {@link #lineBreak()}
         *
         * @return this builder
         */
        public Builder br() {
            return lineBreak();
        }

        /**
         * Add expression to match a tab character ('\u0009')
         *
         * @return this builder
         */
        public Builder tab() {
            return add("(?:\\t)");
        }

        /**
         * Add word, same as [a-zA-Z_0-9]+
         *
         * @return this builder
         */
        public Builder word() {
            return add("(?:\\w+)");
        }


        /*
           --- Predefined character classes
         */

        /**
         * Add word character, same as [a-zA-Z_0-9]
         *
         * @return this builder
         */
        public Builder wordChar() {
            return add("(?:\\w)");
        }


        /**
         * Add non-word character: [^\w]
         *
         * @return this builder
         */
        public Builder nonWordChar() {
            return add("(?:\\W)");
        }

        /**
         * Add non-digit: [^0-9]
         *
         * @return this builder
         */
        public Builder nonDigit() {
            return add("(?:\\D)");
        }

        /**
         * Add same as [0-9]
         *
         * @return this builder
         */
        public Builder digit() {
            return add("(?:\\d)");
        }

        /**
         * Add whitespace character, same as [ \t\n\x0B\f\r]
         *
         * @return this builder
         */
        public Builder space() {
            return add("(?:\\s)");
        }

        /**
         * Add non-whitespace character: [^\s]
         *
         * @return this builder
         */
        public Builder nonSpace() {
            return add("(?:\\S)");
        }


        /*
           --- / end of predefined character classes
         */


        public Builder anyOf(final String pValue) {
            add('[' + sanitize(pValue) + ']');
            return this;
        }

        /**
         * Shortcut to {@link #anyOf(String)}
         *
         * @param value - CharSequence every char from can be matched
         * @return this builder
         */
        public Builder any(final String value) {
            return anyOf(value);
        }

        /**
         * Add expression to match a range (or multiply ranges)
         * Usage: .range(from, to [, from, to ... ])
         * Example: The following matches a hexadecimal number:
         * regex().range( "0", "9", "a", "f") // produce [0-9a-f]
         *
         * @param pArgs - pairs for range
         * @return this builder
         */
        public Builder range(final String... pArgs) {
            final StringBuilder value = new StringBuilder("[");
            for (int firstInPairPosition = 1; firstInPairPosition < pArgs.length; firstInPairPosition += 2) {
                final String from = sanitize(pArgs[firstInPairPosition - 1]);
                final String to = sanitize(pArgs[firstInPairPosition]);

                value.append(from).append('-').append(to);
            }
            value.append(']');

            return add(value.toString());
        }

        public Builder addModifier(final char pModifier) {
            switch (pModifier) {
                case 'd':
                    modifiers |= Pattern.UNIX_LINES;
                    break;
                case 'i':
                    modifiers |= Pattern.CASE_INSENSITIVE;
                    break;
                case 'x':
                    modifiers |= Pattern.COMMENTS;
                    break;
                case 'm':
                    modifiers |= Pattern.MULTILINE;
                    break;
                case 's':
                    modifiers |= Pattern.DOTALL;
                    break;
                case 'u':
                    modifiers |= Pattern.UNICODE_CASE;
                    break;
                case 'U':
                    modifiers |= Pattern.UNICODE_CASE; //UNICODE_CHARACTER_CLASS;
                    break;
                default:
                    break;
            }

            return this;
        }

        public Builder removeModifier(final char pModifier) {
            switch (pModifier) {
                case 'd':
                    modifiers &= ~Pattern.UNIX_LINES;
                    break;
                case 'i':
                    modifiers &= ~Pattern.CASE_INSENSITIVE;
                    break;
                case 'x':
                    modifiers &= ~Pattern.COMMENTS;
                    break;
                case 'm':
                    modifiers &= ~Pattern.MULTILINE;
                    break;
                case 's':
                    modifiers &= ~Pattern.DOTALL;
                    break;
                case 'u':
                    modifiers &= ~Pattern.UNICODE_CASE;
                    break;
                case 'U':
                    modifiers &= ~Pattern.UNICODE_CASE; //UNICODE_CHARACTER_CLASS;
                    break;
                default:
                    break;
            }

            return this;
        }

        public Builder withAnyCase(final boolean pEnable) {
            if (pEnable) {
                addModifier('i');
            } else {
                removeModifier('i');
            }
            return this;
        }

        /**
         * Turn ON matching with ignoring case
         * Example:
         * // matches "a"
         * // matches "A"
         * regex().find("a").withAnyCase()
         *
         * @return this builder
         */
        public Builder withAnyCase() {
            return withAnyCase(true);
        }

        public Builder searchOneLine(final boolean pEnable) {
            if (pEnable) {
                removeModifier('m');
            } else {
                addModifier('m');
            }
            return this;
        }

        /**
         * Convenient method to show that string usage count is exact count, range count or simply one or more
         * Usage:
         * regex().multiply("abc")                                  // Produce (?:abc)+
         * regex().multiply("abc", null)                            // Produce (?:abc)+
         * regex().multiply("abc", (int)from)                       // Produce (?:abc){from}
         * regex().multiply("abc", (int)from, (int)to)              // Produce (?:abc){from, to}
         * regex().multiply("abc", (int)from, (int)to, (int)...)    // Produce (?:abc)+
         *
         * @param pValue - the string to be looked for
         * @param count  - (optional) if passed one or two numbers, it used to show count or range count
         * @return this builder
         * @see #oneOrMore()
         * @see #then(String)
         * @see #zeroOrMore()
         */
        public Builder multiple(final String pValue, final int... count) {
            if (count == null) {
                return then(pValue).oneOrMore();
            }
            switch (count.length) {
                case 1:
                    return then(pValue).count(count[0]);
                case 2:
                    return then(pValue).count(count[0], count[1]);
                default:
                    return then(pValue).oneOrMore();
            }
        }

        /**
         * Adds "+" char to regexp
         * Same effect as {@link #atLeast(int)} with "1" argument
         * Also, used by {@link #multiple(String, int...)} when second argument is null, or have length more than 2
         *
         * @return this builder
         * @since 1.2
         */
        public Builder oneOrMore() {
            return add("+");
        }

        /**
         * Adds "*" char to regexp, means zero or more times repeated
         * Same effect as {@link #atLeast(int)} with "0" argument
         *
         * @return this builder
         * @since 1.2
         */
        public Builder zeroOrMore() {
            return add("*");
        }

        /**
         * Add count of previous group
         * for example:
         * .find("w").count(3) // produce - (?:w){3}
         *
         * @param count - number of occurrences of previous group in expression
         * @return this Builder
         */
        public Builder count(final int count) {
            source.append('{').append(count).append('}');
            return this;
        }

        /**
         * Produce range count
         * for example:
         * .find("w").count(1, 3) // produce (?:w){1,3}
         *
         * @param from - minimal number of occurrences
         * @param to   - max number of occurrences
         * @return this Builder
         * @see #count(int)
         */
        public Builder count(final int from, final int to) {
            source.append('{').append(from).append(',').append(to).append('}');
            return this;
        }

        /**
         * Produce range count with only minimal number of occurrences
         * for example:
         * .find("w").atLeast(1) // produce (?:w){1,}
         *
         * @param from - minimal number of occurrences
         * @return this Builder
         * @see #count(int)
         * @see #oneOrMore()
         * @see #zeroOrMore()
         * @since 1.2
         */
        public Builder atLeast(final int from) {
            return add("{").add(valueOf(from)).add(",}");
        }

        /**
         * Add a alternative expression to be matched
         *
         * Issue #32
         *
         * @param pValue - the string to be looked for
         * @return this builder
         */
        public Builder or(final String pValue) {
            prefixes.append("(?:");

            final int opened = countOccurrencesOf(prefixes.toString(), "(");
            final int closed = countOccurrencesOf(suffixes.toString(), ")");

            if (opened >= closed) {
                suffixes = new StringBuilder(")" + suffixes);
            }

            add(")|(?:");
            if (pValue != null) {
                then(pValue);
            }
            return this;
        }

        /**
         * Adds an alternative expression to be matched
         * based on an array of values
         *
         * @param pValues - the strings to be looked for
         * @return this builder
         * @since 1.3
         */
        public Builder oneOf(final String... pValues) {
            if(pValues != null && pValues.length > 0) {
                add("(?:");
                for(int i = 0; i < pValues.length; i++) {
                    final String value = pValues[i];
                    add("(?:");
                    add(value);
                    add(")");
                    if(i < pValues.length - 1) {
                        add("|");
                    }
                }
                add(")");
            }
            return this;
        }

        /**
         * Adds capture - open brace to current position and closed to suffixes
         *
         * @return this builder
         */
        public Builder capture() {
            suffixes.append(')');
            return add("(");
        }

        /**
         * Shortcut for {@link #capture()}
         *
         * @return this builder
         * @since 1.2
         */
        public Builder capt() {
            return capture();
        }

        /**
         * Same as {@link #capture()}, but don't save result
         * May be used to set count of duplicated captures, without creating a new saved capture
         * Example:
         * // Without group() - count(2) applies only to second capture
         * regex().group()
         * .capt().range("0", "1").endCapt().tab()
         * .capt().digit().count(5).endCapt()
         * .endGr().count(2);
         *
         * @return this builder
         * @since 1.2
         */
        public Builder group() {
            suffixes.append(')');
            return add("(?:");
        }

        /**
         * Close brace for previous capture and remove last closed brace from suffixes
         * Can be used to continue build regex after capture or to add multiply captures
         *
         * @return this builder
         */
        public Builder endCapture() {
            if (suffixes.indexOf(")") != -1) {
                suffixes.setLength(suffixes.length() - 1);
                return add(")");
            } else {
                throw new IllegalStateException("Can't end capture (group) when it not started");
            }
        }

        /**
         * Shortcut for {@link #endCapture()}
         *
         * @return this builder
         * @since 1.2
         */
        public Builder endCapt() {
            return endCapture();
        }

        /**
         * Closes current unnamed and unmatching group
         * Shortcut for {@link #endCapture()}
         * Use it with {@link #group()} for prettify code
         * Example:
         * regex().group().maybe("word").count(2).endGr()
         *
         * @return this builder
         * @since 1.2
         */
        public Builder endGr() {
            return endCapture();
        }
    }

    /**
     * Use builder {@link #regex()} (or {@link #regex(VerbalExpression.Builder)})
     * to create new instance of VerbalExpression
     *
     * @param pattern - {@link java.util.regex.Pattern} that constructed by builder
     */
    private VerbalExpression(final Pattern pattern) {
        this.pattern = pattern;
    }

    /**
     * Test that full string matches regular expression
     *
     * @param pToTest - string to check match
     * @return true if matches exact string, false otherwise
     */
    public boolean testExact(final String pToTest) {
        boolean ret = false;
        if (pToTest != null) {
            ret = pattern.matcher(pToTest).matches();
        }
        return ret;
    }

    /**
     * Test that full string contains regex
     *
     * @param pToTest - string to check match
     * @return true if string contains regex, false otherwise
     */
    public boolean test(final String pToTest) {
        boolean ret = false;
        if (pToTest != null) {
            ret = pattern.matcher(pToTest).find();
        }
        return ret;
    }

    /**
     * Extract full string that matches regex
     * Same as {@link #getText(String, int)} for 0 group
     *
     * @param toTest - string to extract from
     * @return group 0, extracted from text
     */
    public String getText(final String toTest) {
        return getText(toTest, 0);
    }

    /**
     * Extract exact group from string
     *
     * @param toTest - string to extract from
     * @param group  - group to extract
     * @return extracted group
     * @since 1.1
     */
    private String getText(final String toTest, final int group) {
        final Matcher m = pattern.matcher(toTest);
        final StringBuilder result = new StringBuilder();
        while (m.find()) {
            result.append(m.group(group));
        }
        return result.toString();
    }

    /**
     * Extract exact group from string and add it to list
     *
     * Example:
     * String text = "SampleHelloWorldString";
     * VerbalExpression regex = regex().capt().oneOf("Hello", "World").endCapt().maybe("String").build();
     * list = regex.getTextGroups(text, 0) //result: "Hello", "WorldString"
     * list = regex.getTextGroups(text, 1) //result: "Hello", "World"
     *
     * @param toTest - string to extract from
     * @param group  - group to extract
     * @return list of extracted groups
     */
    public List<String> getTextGroups(final String toTest, final int group) {
        final List<String> groups = new ArrayList<>();
        final Matcher m = pattern.matcher(toTest);
        while (m.find()) {
            groups.add(m.group(group));
        }
        return groups;
    }

    @Override
    public String toString() {
        return pattern.pattern();
    }

    /**
     * Creates new instance of VerbalExpression builder from cloned builder
     *
     * @param pBuilder - instance to clone
     * @return new VerbalExpression.Builder copied from passed
     * @since 1.1
     */
    public static Builder regex(final Builder pBuilder) {
        final Builder builder = new Builder();

        //Using created StringBuilder
        builder.prefixes.append(pBuilder.prefixes);
        builder.source.append(pBuilder.source);
        builder.suffixes.append(pBuilder.suffixes);
        builder.modifiers = pBuilder.modifiers;

        return builder;
    }

    /**
     * Creates new instance of VerbalExpression builder
     *
     * @return new VerbalExpression.Builder
     * @since 1.1
     */
    public static Builder regex() {
        return new Builder();
    }
}
