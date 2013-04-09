/*
 * (C) 2012 Netherlands eScience Center/Biomarker Boosting consortium. 
 * 
 * This code is under development. 
 *  
 */
// source: 

package nl.esciencecenter.ptk.util;

import java.util.regex.Pattern;

/**
 * Source code from: http://www.rgagnon.com/javadetails/java-0515.html
 * <p>
 * If you find this article useful, consider making a small donation to show
 * your support for this Web site and its content.
 * <p>
 * Written and compiled by R Gagnon (C)1998-2005
 * <p>
 * Bugfixed and updated by Piter T. de Boer.
 */
public class Wildcard2Regex
{
    public static void main(String args[])
    {
        testWildcards(); 
    }
    
    //@Test
    public static void  testWildcards()
    {
        String test = "123ABC";
        //System.out.println(test);
        testWildcard("1*", test,true);
        testWildcard("?2*", test,true);
        testWildcard("??2*", test,false);
        testWildcard("*A*", test,true);
        testWildcard("*Z*", test,false);
        testWildcard("123*", test,true);
        testWildcard("123", test,false);
        testWildcard("*ABC", test,true);
        testWildcard("*abc", test,false);
        testWildcard("ABC*", test,false);
        
        // output : 123ABC true true false true false true false true false
        // false
    }
    
    private static void testWildcard(String pattern,String sourceString,boolean matches)
    {
        boolean result=Pattern.matches(wildcardToRegex(pattern), sourceString); 
        if (matches!=result)
            System.err.printf("Invalid match: Pattern:'%s' on string '%s' should return:%s\n",pattern,sourceString,matches); 
    }

    public static String wildcardToRegex(String wildcard)
    {
        StringBuffer s = new StringBuffer(wildcard.length());
        s.append('^');
        for (int i = 0, is = wildcard.length(); i < is; i++)
        {
            char c = wildcard.charAt(i);
            switch (c)
            {
                case '*':
                    s.append(".*");
                    break;
                case '?':
                    s.append(".");
                    break;
                // escape special regexp-characters
                case '(':
                case ')':
                case '[':
                case ']':
                case '$':
                case '^':
                case '.':
                case '{':
                case '}':
                case '|':
                case '\\':
                    s.append("\\");
                    s.append(c);
                    break;
                default:
                    s.append(c);
                    break;
            }
        }

        s.append('$');
        return (s.toString());
    }
    
    
    public Wildcard2Regex()
    {
    }

} // class

