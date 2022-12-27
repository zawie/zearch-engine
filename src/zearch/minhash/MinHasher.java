package zearch.minhash;

import java.io.IOException;
import java.io.Reader;
import java.util.Random;

public class MinHasher {

    public static final int COUNT = 2048;
    private int[] seeds;
    private static final int SEED = 1234567890;
    public MinHasher() {
        assert(COUNT % 2 == 0);
        this.seeds = new int[COUNT];
        Random rand = new Random();
        rand.setSeed(SEED);
        for (int i = 0; i < COUNT; i++)
            this.seeds[i] = rand.nextInt();
    }

    public int[] computeHashes(Reader content) throws IOException {
        int[] hashes = new int[COUNT];
        for (int h = 0; h < COUNT; h+=2) {
            hashes[h] = Integer.MAX_VALUE;
            hashes[h+1] = Integer.MIN_VALUE;
        }

        char[] gram = new char[3];
        int index = 3;
        int c;
        while((c = content.read()) >= 0) {
            gram[index % 3] = Character.toLowerCase((char) c);
            for (int h = 0; h < COUNT; h+=2) {
                int i0 = index % 3;
                int i1 = (index - 1) % 3;
                int i2 = (index - 2) % 3;
                int hash = computeHash(gram[i0], gram[i1], gram[i2], seeds[h]);
                hashes[h] = Math.min(hash, hashes[h]);
                hashes[h+1] = Math.max(hash, hashes[h+1]);
            }
        }
        return hashes;
    }

    private int computeHash(char char1, char char2, char char3, int seed) {
        int[] chars = new int[]{seed, char1, char2, char3};
        int h = 0;
        for(int i = 0; i < chars.length; i++) {
            // CRC variant: Do a 5-bit left circular shift of h. Then XOR in ki.
            int c = chars[i];
            int highOrder = h & 0xf8000000;     // extract high-order 5 bits from h
            h = h << 5;                         // shift h left by 5 bits
            h = h ^ (highOrder >> 27);          // move the highOrder 5 bits to the low-order
            h = h ^ c;                          // XOR into h
        }
        return h & 0xff; // extract bottom byte
    }
}
