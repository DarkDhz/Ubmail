package es.arnaugris.utils;

public class Levenshtein {

    // Singleton Instance
    private static volatile Levenshtein instance = null;

    private Levenshtein() {

    }

    public static Levenshtein getInstance() {
        // To ensure only one instance is created
        if (instance == null) {
            synchronized (Levenshtein.class) {
                if (instance == null) {
                    instance = new Levenshtein();
                }
            }
        }
        return instance;
    }

    /**
     * Calculate the Levenshtein distance between two strings. Basically, the number of
     * changes that need to be made to convert one string into another. Very useful when
     * determining string similarties.
     * @param stringOne
     * @param stringTwo
     * @param caseSensitive Should differences in case be treated as changes.
     * @return The Levenshtein distance
     */
    public int levenshtein(String stringOne, String stringTwo, boolean caseSensitive) {
        // if we want to ignore case sensitivity, lower case the strings
        if(!caseSensitive)
        {
            stringOne = stringOne.toLowerCase();
            stringTwo = stringTwo.toLowerCase();
        }

        // store length
        int m = stringOne.length();
        int n = stringTwo.length();

        // matrix to store differences
        int[][] deltaM = new int[m+1][n+1];

        for(int i = 1;i <= m; i++)
        {
            deltaM[i][0] = i;
        }

        for(int j = 1;j <= n; j++)
        {
            deltaM[0][j] = j;
        }

        for(int j=1;j<=n;j++)
        {
            for(int i=1;i<=m;i++)
            {
                if(stringOne.charAt(i-1) == stringTwo.charAt(j-1))
                {
                    deltaM[i][j] = deltaM[i-1][j-1];
                }
                else
                {
                    deltaM[i][j] = Math.min(
                            deltaM[i-1][j]+1,
                            Math.min(
                                    deltaM[i][j-1]+1,
                                    deltaM[i-1][j-1]+1
                            )
                    );
                }
            }
        }

        return deltaM[m][n];

    }
}
