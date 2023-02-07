package zearch.minhash;

import java.io.IOException;
import java.util.Random;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;


public class MinHasher {

    public static final int COUNT = 2048;

    private static final int gramSize = 9;

    private static final int max = 0x00ffffff;
    public MinHasher() {
        assert(COUNT % 2 == 0);
    }

    public int[] computeHashes(String content) throws IOException {
        int[] hashes = new int[COUNT];
        for (int h = 0; h < COUNT; h+=2) {
            hashes[h] = Integer.MAX_VALUE;
            hashes[h+1] = Integer.MIN_VALUE;
        }

        String gram = "";
        char ch;
        CharacterIterator it = new StringCharacterIterator(content);
        while ((ch = it.next()) != CharacterIterator.DONE) {
            if (!Character.isLetterOrDigit(ch) && ch != ' ')
                continue;
            gram = gram + ch;
            int l = gram.length();
            if (l > gramSize)
                gram = gram.substring(l - gramSize);
            for (int h = 0; h < COUNT; h+=2) {
                int hash = computeStringHash(gram+h);
                hashes[h] = Math.min(hash, hashes[h]);
                hashes[h+1] = Math.max(hash, hashes[h+1]);
            }
        }

        for (int h = 0; h < COUNT; h+=2) {
            hashes[h] = hashes[h] & 0xff;
            hashes[h+1] = (max - hashes[h+1]) & 0xff;
        }

        return hashes;
    }

    private int computeStringHash(String str) {
        return str.hashCode() & max;
    }
}
