package es.arnaugris.checks;

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
     * Method to calculate levenshtein distance between 2 words
     * @param original Original string
     * @param toCompare String to compare
     * @return Distance between strings
     */
    public int levenshtein(String original, String toCompare) {
        original = original.toLowerCase();
        toCompare = toCompare.toLowerCase();

        int m = original.length();
        int n = toCompare.length();

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
                if(original.charAt(i-1) == toCompare.charAt(j-1))
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
