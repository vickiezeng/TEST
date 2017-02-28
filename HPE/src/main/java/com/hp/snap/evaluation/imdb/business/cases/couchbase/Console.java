package com.hp.snap.evaluation.imdb.business.cases.couchbase;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Console {
    public static void pressAnyKey() {
        try {
            System.out.print("Press [Enter] key to continue.");
            new BufferedReader(new InputStreamReader(System.in)).readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
