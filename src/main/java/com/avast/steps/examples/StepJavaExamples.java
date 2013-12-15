package com.avast.steps.examples;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static com.avast.steps.StepJava.*;

/**
 * User: zslajchrt
 * Date: 12/1/13
 * Time: 12:39 PM
 */
public class StepJavaExamples {

    public static final String shakespeareRoot = "/Users/zslajchrt/Documents/Projects/Avast/trunk/clockwork/src/test/data/shakespeare/";
    public static final String sourceFile = shakespeareRoot + "README";
    public static final String sourceFile2 = shakespeareRoot + "glossary";
    public static final String source = "file://" + sourceFile;
    public static final String source2 = "file://" + sourceFile2;
    public static final List<String> webSites = Arrays.asList("http://www.lidovky.cz", "http://www.idnes.cz");

    public static void main(String[] args) throws Throwable {
        StepFunction<String> stepFn = new StepFunction<String>() {

            final BufferedReader reader = new BufferedReader(new FileReader(sourceFile));
            final Closeable closeable = new Closeable() {
                @Override
                public void close() throws IOException {
                    System.out.println("Closed");
                    reader.close();
                }
            };

            @Override
            public Step<String> apply() throws Exception {
                String line = reader.readLine();
                if (line == null) {
                    return noStep();
                } else {
                    return new NextStep<String>(line, this, closeable);
                }
            }

        };

        stepFn.apply().foreach(new Function1V<String>() {
            @Override
            public void apply(String line) throws Exception {
                System.out.println(line);
            }
        });

        StepFunction<String> wsSteps = fromIterator(webSites.iterator(), new Closeable() {
            @Override
            public void close() throws IOException {
                System.out.println("Closed2");
            }
        });
        wsSteps.apply().foreach(new Function1V<String>() {
            @Override
            public void apply(String arg) throws Exception {
                System.out.println(arg);
            }
        });
    }

}
