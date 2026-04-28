package decorater;

import java.util.Locale;
import java.util.Optional;
import java.util.stream.IntStream;


/*
What is the Decorator in the Java
It adding behaviour without alerting the class itself. 
or it faciliates the addition of behaviours to individual objects without inheriting from them.


wanted the arguement an object with the additional functionality 
and if I want to rewrite or alter the existing code(OCP)
and want to keep new functionality separate
and need to interact with the existing structures

and there are two options:
inherit from required object if possible and some classes should be final
build the decorator which simply references the decorated object


there one way to go if we are not using the inheritance that is to aggregate the actual object
whenver u have to expand the functionality
*/

class MagicString{

    public static String copyValueOf(char[] data) {
        return String.copyValueOf(data);
    }

    public static String copyValueOf(char[] data, int offset, int count) {
        return String.copyValueOf(data, offset, count);
    }

    public static String format(Locale l, String format, Object... args) {
        return String.format(l, format, args);
    }

    public static String format(String format, Object... args) {
        return String.format(format, args);
    }

    public static String valueOf(char c) {
        return String.valueOf(c);
    }

    public static String valueOf(int i) {
        return String.valueOf(i);
    }

    public static String valueOf(long l) {
        return String.valueOf(l);
    }
    private String string;

    public MagicString(String string)
    {
        this.string = string;
    }

    public long getNumberofVowels()
    {
        return string.chars()
        .mapToObj( c -> (char) c)
        .filter(c -> "aeiou".contains(c.toString()))
        .count();
    }

    // so this is the gist of the decorator pattern
    ///////////////////
    public char charAt(int index) {
        return string.charAt(index);
    }

    public IntStream chars() {
        return string.chars();
    }

    public int codePointAt(int index) {
        return string.codePointAt(index);
    }

    public int codePointBefore(int index) {
        return string.codePointBefore(index);
    }

    public int codePointCount(int beginIndex, int endIndex) {
        return string.codePointCount(beginIndex, endIndex);
    }

    public IntStream codePoints() {
        return string.codePoints();
    }

    public int compareTo(String anotherString) {
        return string.compareTo(anotherString);
    }

    public int compareToIgnoreCase(String str) {
        return string.compareToIgnoreCase(str);
    }

    public String concat(String str) {
        return string.concat(str);
    }

    public boolean contains(CharSequence s) {
        return string.contains(s);
    }

    public boolean contentEquals(CharSequence cs) {
        return string.contentEquals(cs);
    }

    public boolean contentEquals(StringBuffer sb) {
        return string.contentEquals(sb);
    }

    public Optional<String> describeConstable() {
        return string.describeConstable();
    }

    public boolean endsWith(String suffix) {
        return string.endsWith(suffix);
    }

    public boolean equals(Object anObject) {
        return string.equals(anObject);
    }

    public boolean equalsIgnoreCase(String anotherString) {
        return string.equalsIgnoreCase(anotherString);
    }

    public String formatted(Object... args) {
        return string.formatted(args);
    }

    public byte[] getBytes() {
        return string.getBytes();
    }

    public int hashCode() {
        return string.hashCode();
    }

    public String indent(int n) {
        return string.indent(n);
    }

    public int indexOf(int ch) {
        return string.indexOf(ch);
    }

    public String intern() {
        return string.intern();
    }

    public boolean isBlank() {
        return string.isBlank();
    }

    public boolean isEmpty() {
        return string.isEmpty();
    }

    public int length() {
        return string.length();
    }

    public String strip() {
        return string.strip();
    }

    public String stripIndent() {
        return string.stripIndent();
    }

    public String stripLeading() {
        return string.stripLeading();
    }

    public char[] toCharArray() {
        return string.toCharArray();
    }

    public String toLowerCase() {
        return string.toLowerCase();
    }

    public String toString() {
        // return string.toString();
       return string; 
    }

    public String toUpperCase() {
        return string.toUpperCase();
    }

    public String trim() {
        return string.trim();
    }

    
}

public class Test2 {
    public static void main(String[] args) {
        
       MagicString s =  new MagicString("hello");
        System.out.println(s + " has " + s.getNumberofVowels() + " vowels");
    }
}
