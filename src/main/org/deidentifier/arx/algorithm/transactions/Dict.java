package org.deidentifier.arx.algorithm.transactions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * This class represents a bidirectional dictionary that maps strings <-> int. Used to convert databases to internal
 * int representation
 */
public class Dict {

    private Map<String, Integer> m = new HashMap<>();
    private String[] reps;


    /**
     * Builds the dictionary based on a string hierarchy. Works in a breadth first fashion, so the labels that are less
     * generalized have smaller ids
     *
     * @param hierarchy the hierarchy that the dict should be built on
     */
    public Dict(String[][] hierarchy) {
        ArrayList<String> reps = new ArrayList<>(hierarchy[0].length);
        Map<String, Integer> keys = new HashMap<>(hierarchy[0].length * hierarchy.length);

        int idcounter = 0;
        int generalizationLevel = 0;
        for (int i = 0; i < hierarchy[0].length; i++) {
            for (String[] item : hierarchy) {
                String key = item[generalizationLevel];

                Integer genlevel = keys.put(key, generalizationLevel);
                if (genlevel != null && genlevel != generalizationLevel) // Hierarchy contains same item on different levels. This is not supported
                    throw new RuntimeException(String.format("Item %s already contained at level %d. Replace item with distinct value.\n", key, generalizationLevel));

                if (item.length > generalizationLevel && !m.containsKey(key)) {
                    m.put(key, idcounter++);
                    reps.add(idcounter - 1, key);
                }
            }
            generalizationLevel++;
        }
        this.reps = reps.toArray(new String[0]);
    }

    /**
     * @param s the string representation of an item
     * @return the integer representation of an item. null if not in this dict
     */
    public int getRepresentation(String s) {
        return m.get(s);
    }


    /**
     * @param i the integer representation of an item
     * @return the string representation of an item
     */
    public String getString(int i) {
        return reps[i];
    }

    /**
     * @param t a database with string items
     * @return the database converted with items in integer representation
     */
    public int[][] convertTransactions(String[][] t) {
        int[][] it = new int[t.length][];
        for (int i = 0; i < t.length; i++) {
            it[i] = new int[t[i].length];
            for (int j = 0; j < t[i].length; j++) {
                it[i][j] = m.get(t[i][j]);
            }
        }
        return it;
    }

    /**
     * @param t a database with string items
     * @return the database converted with items in integer representation
     */
    public String[][] convertTransactions(int[][] t) {
        String[][] stringDB = new String[t.length][];
        for (int i = 0; i < t.length; i++) {
            stringDB[i] = new String[t[i].length];
            for (int j = 0; j < t[i].length; j++) {
                stringDB[i][j] = reps[t[i][j]];
            }
        }
        return stringDB;
    }
}


