/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014. Tristan John Whitcher
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.tronner.util;

import com.tronner.dispatcher.Commands;

import java.util.ArrayList;
import java.util.List;

/**
 * Tronner - LString
 *
 * @author Tristan on 8/7/2014.
 */
public class LString {

    private boolean prepped = false;

    private List<String> args = new ArrayList<String>();

    private String s;

    public LString(String s) {
        this.s = s;
    }

    public void c(String word, String color) {
        c(word, color, "");
    }

    public void c(String word, String color, String backToColor) {
        s = s.replace(word, color+word+backToColor);
    }

    public void prep() {
        boolean inArg = false;
        String write = "";

        for(int i = 0; i < s.length(); i++) {
            if(s.charAt(i) == '[' && !inArg) {
                inArg = true;
                write += s.charAt(i);
            } else if(s.charAt(i) == ']' && inArg) {
                inArg = false;
                write += s.charAt(i);
                args.add(write);
                write = "";
            } else if(inArg) {
                write += s.charAt(i);
            }
        }

        for(int i = 0; i < args.size(); i++) {
            s = s.replace(args.get(i), "["+i+"]");
        }
        prepped = true;
    }

    public String parse(boolean output, String... args) {
        if(!prepped) prep();
        String out = s;
        for(int i = 0; i < args.length; i++) {
            out = out.replace("["+i+"]", args[i]);
        }
        if(output)
            Commands.CONSOLE_MESSAGE(out);
        return out;
    }

    public String parse(String... args) {
        return parse(true, args);
    }

    public String getString() {
        return s;
    }

}
