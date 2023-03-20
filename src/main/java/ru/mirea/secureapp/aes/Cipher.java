package ru.mirea.secureapp.aes;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Base64;
import java.util.Random;

import static ru.mirea.secureapp.aes.Constants.*;

public class Cipher {
    private byte[] key;

    private int[] w; // The storage vector for the expansion of the key creation
    private int Nk;
    private int Nr;

    public Cipher(Type type) {
        init(null, type);
    }

    public Cipher(byte[] key) {
        init(key, null);
    }

    public Cipher(String keyBase64) {
        init(Base64.getDecoder().decode(keyBase64), null);
    }

    private void init(byte[] key, Type type) {
        if (key != null) {
            this.key = new byte[key.length];
            System.arraycopy(key, 0, this.key, 0, key.length);

            switch (key.length) {
                case 16 -> {
                    Nk = 4;
                    Nr = 10;
                }
                case 24 -> {
                    Nk = 6;
                    Nr = 12;
                }
                case 32 -> {
                    Nk = 8;
                    Nr = 14;
                }
                default -> throw new IllegalArgumentException("AES-2011 only supports 128, 192 and 256 bit keys!");
            }
        } else if (type != null) {
            switch (type) {
                case AES_128 -> {
                    Nk = 4;
                    Nr = 10;
                }
                case AES_192 -> {
                    Nk = 6;
                    Nr = 12;
                }
                case AES_256 -> {
                    Nk = 8;
                    Nr = 14;
                }
                default -> throw new IllegalArgumentException("AES-2011 only supports 128, 192 and 256 bit keys!");
            }
            Random random = new Random();
            this.key = new byte[Nk * 4];
            for (int i = 0; i < this.key.length; i++) {
                this.key[i] = (byte) random.nextInt(0, 128);
            }
        } else {
            throw new RuntimeException("Key or type weren't provided!");
        }
        w = new int[N_B * (Nr + 1)];

        // Key expansion
        expandKey();
    }

    public String getKeyBase64() {
        return Base64.getEncoder().encodeToString(key);
    }

    public byte[] getKey(){
        return key;
    }

    public String encrypt(byte[] text) {
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        for (int i = 0; i < text.length; i += BLOCK_LENGTH) {
            try {
                result.write(encryptBlock(Arrays.copyOfRange(text, i, i + BLOCK_LENGTH)));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return Base64.getEncoder().encodeToString(result.toByteArray());
    }

    private byte[] encryptBlock(byte[] text) {
        int[][] state = createState(text);

        addRoundKey(state, 0);

        for (int actual = 1; actual < Nr; actual++) {
            subBytes(state, false);
            shiftRows(state, false);
            mixColumns(state, false);
            addRoundKey(state, actual);
        }
        subBytes(state, false);
        shiftRows(state, false);
        addRoundKey(state, Nr);

        return stateToResult(state, text.length);
    }

    public String decrypt(String encryptedText) {
        byte[] text = Base64.getDecoder().decode(encryptedText);

        ByteArrayOutputStream result = new ByteArrayOutputStream();
        for (int i = 0; i < text.length; i += BLOCK_LENGTH) {
            try {
                result.write(decryptBlock(Arrays.copyOfRange(text, i, i + BLOCK_LENGTH)));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result.toString().trim();
    }

    private byte[] decryptBlock(byte[] text) {
        int[][] state = createState(text);

        addRoundKey(state, Nr);

        for (int actual = Nr - 1; actual > 0; actual--) {
            shiftRows(state, true);
            subBytes(state, true);
            addRoundKey(state, actual);
            mixColumns(state, true);
        }
        shiftRows(state, true);
        subBytes(state, true);
        addRoundKey(state, 0);

        return stateToResult(state, text.length);
    }

    // Cypher functions
    private void expandKey() {
        int temp;
        int i = 0;

        while (i < Nk) {
            w[i] = 0;
            w[i] |= key[i * 4] << 24;
            w[i] |= key[i * 4 + 1] << 16;
            w[i] |= key[i * 4 + 2] << 8;
            w[i] |= key[i * 4 + 3];
            i++;
        }

        i = Nk;

        while (i < N_B * (Nr + 1)) {
            temp = w[i - 1];
            if (i % Nk == 0) {
                temp = subWord(rotWord(temp), false) ^ (roundConstant(i / Nk));
            } else if (Nk > 6 && i % Nk == 4) {
                temp = subWord(temp, false);
            }
            w[i] = w[i - Nk] ^ temp;
            i++;
        }
    }

    private int[][] createState(byte[] text) {
        int[][] state = new int[4][N_B];

        for (int i = 0; i < N_B; i++) {
            for (int j = 0; j < 4; j++) {
                state[j][i] = text[i * N_B + j] & 0xff;
            }
        }

        return state;
    }

    private byte[] stateToResult(int[][] state, int textLength) {
        byte[] out = new byte[textLength];

        for (int i = 0; i < N_B; i++) {
            for (int j = 0; j < 4; j++) {
                out[i * N_B + j] = (byte) (state[j][i] & 0xff);
            }
        }

        return out;
    }

    private void addRoundKey(int[][] state, int round) {
        for (int r = 0; r < 4; r++) {
            for (int c = 0; c < N_B; c++) {
                state[r][c] ^= ((w[round * N_B + c] << (r * 8)) >>> 24);
            }
        }
    }

    private void subBytes(int[][] state, boolean inverse) {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < N_B; j++) {
                state[i][j] = subWord(state[i][j], inverse) & 0xFF;
            }
        }
    }

    private void shiftRows(int[][] state, boolean inverse) {
        for (int i = 1; i < 4; i++) { // skipping row 0
            for (int s = 0; s < i; s++) {
                int tmp = inverse ? state[i][N_B - 1] : state[i][0];

                if (inverse) {
                    for (int k = N_B - 1; k > 0; k--) {
                        state[i][k] = state[i][k - 1];
                    }

                    state[i][0] = tmp;
                } else {
                    for (int k = 1; k < N_B; k++) {
                        state[i][k - 1] = state[i][k];
                    }

                    state[i][N_B - 1] = tmp;
                }
            }
        }
    }

    private void mixColumns(int[][] state, boolean inverse) {
        int[] a = inverse ? new int[]{0x0e, 0x09, 0x0d, 0x0b} : new int[]{0x02, 0x01, 0x01, 0x03};
        int temp0, temp1, temp2, temp3;

        for (int c = 0; c < N_B; c++) {
            temp0 = mult(a[0], state[0][c]) ^ mult(a[3], state[1][c]) ^ mult(a[2], state[2][c]) ^ mult(a[1], state[3][c]);
            temp1 = mult(a[1], state[0][c]) ^ mult(a[0], state[1][c]) ^ mult(a[3], state[2][c]) ^ mult(a[2], state[3][c]);
            temp2 = mult(a[2], state[0][c]) ^ mult(a[1], state[1][c]) ^ mult(a[0], state[2][c]) ^ mult(a[3], state[3][c]);
            temp3 = mult(a[3], state[0][c]) ^ mult(a[2], state[1][c]) ^ mult(a[1], state[2][c]) ^ mult(a[0], state[3][c]);

            state[0][c] = temp0;
            state[1][c] = temp1;
            state[2][c] = temp2;
            state[3][c] = temp3;
        }
    }

    // Help functions
    private static int xTime(int b) {
        if ((b & 0x80) == 0) {
            return b << 1;
        }
        return (b << 1) ^ 0x11b;
    }

    private static int mult(int a, int b) {
        int sum = 0;
        while (a != 0) {
            if ((a & 1) != 0)
                sum ^= b; // add b if lowest bits of a and b are 1

            b = xTime(b); // bit shift b
            a = a >>> 1;
        }
        return sum;
    }

    private int subWord(int word, boolean inverse) {
        int[] AES_BOX = inverse ? RS_BOX : S_BOX;
        int subWord = 0;
        for (int i = 0; i <= 24; i += 8) {
            int iByte = word << i >>> 24;
            subWord |= AES_BOX[iByte] << (24 - i);
        }
        return subWord;
    }

    private int rotWord(int word) {
        return (word << 8) | ((word & 0xff000000) >>> 24);
    }

    private int roundConstant(int i) {
        int xHighByte = 0x02;
        if (i == 1) {
            xHighByte = 0x01;
        } else if (i > 1) {
            i--;
            while (i - 1 > 0) {
                xHighByte = mult(xHighByte, 0x02);
                i--;
            }
        }

        return xHighByte << 24;
    }
}
