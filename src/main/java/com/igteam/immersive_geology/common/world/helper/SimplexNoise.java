package com.igteam.immersive_geology.common.world.helper;

public class SimplexNoise {
    private static SimplexNoise.Grad[] grad3 = new SimplexNoise.Grad[]{new SimplexNoise.Grad(1.0D, 1.0D, 0.0D), new SimplexNoise.Grad(-1.0D, 1.0D, 0.0D), new SimplexNoise.Grad(1.0D, -1.0D, 0.0D), new SimplexNoise.Grad(-1.0D, -1.0D, 0.0D), new SimplexNoise.Grad(1.0D, 0.0D, 1.0D), new SimplexNoise.Grad(-1.0D, 0.0D, 1.0D), new SimplexNoise.Grad(1.0D, 0.0D, -1.0D), new SimplexNoise.Grad(-1.0D, 0.0D, -1.0D), new SimplexNoise.Grad(0.0D, 1.0D, 1.0D), new SimplexNoise.Grad(0.0D, -1.0D, 1.0D), new SimplexNoise.Grad(0.0D, 1.0D, -1.0D), new SimplexNoise.Grad(0.0D, -1.0D, -1.0D)};
    private static SimplexNoise.Grad[] grad4 = new SimplexNoise.Grad[]{new SimplexNoise.Grad(0.0D, 1.0D, 1.0D, 1.0D), new SimplexNoise.Grad(0.0D, 1.0D, 1.0D, -1.0D), new SimplexNoise.Grad(0.0D, 1.0D, -1.0D, 1.0D), new SimplexNoise.Grad(0.0D, 1.0D, -1.0D, -1.0D), new SimplexNoise.Grad(0.0D, -1.0D, 1.0D, 1.0D), new SimplexNoise.Grad(0.0D, -1.0D, 1.0D, -1.0D), new SimplexNoise.Grad(0.0D, -1.0D, -1.0D, 1.0D), new SimplexNoise.Grad(0.0D, -1.0D, -1.0D, -1.0D), new SimplexNoise.Grad(1.0D, 0.0D, 1.0D, 1.0D), new SimplexNoise.Grad(1.0D, 0.0D, 1.0D, -1.0D), new SimplexNoise.Grad(1.0D, 0.0D, -1.0D, 1.0D), new SimplexNoise.Grad(1.0D, 0.0D, -1.0D, -1.0D), new SimplexNoise.Grad(-1.0D, 0.0D, 1.0D, 1.0D), new SimplexNoise.Grad(-1.0D, 0.0D, 1.0D, -1.0D), new SimplexNoise.Grad(-1.0D, 0.0D, -1.0D, 1.0D), new SimplexNoise.Grad(-1.0D, 0.0D, -1.0D, -1.0D), new SimplexNoise.Grad(1.0D, 1.0D, 0.0D, 1.0D), new SimplexNoise.Grad(1.0D, 1.0D, 0.0D, -1.0D), new SimplexNoise.Grad(1.0D, -1.0D, 0.0D, 1.0D), new SimplexNoise.Grad(1.0D, -1.0D, 0.0D, -1.0D), new SimplexNoise.Grad(-1.0D, 1.0D, 0.0D, 1.0D), new SimplexNoise.Grad(-1.0D, 1.0D, 0.0D, -1.0D), new SimplexNoise.Grad(-1.0D, -1.0D, 0.0D, 1.0D), new SimplexNoise.Grad(-1.0D, -1.0D, 0.0D, -1.0D), new SimplexNoise.Grad(1.0D, 1.0D, 1.0D, 0.0D), new SimplexNoise.Grad(1.0D, 1.0D, -1.0D, 0.0D), new SimplexNoise.Grad(1.0D, -1.0D, 1.0D, 0.0D), new SimplexNoise.Grad(1.0D, -1.0D, -1.0D, 0.0D), new SimplexNoise.Grad(-1.0D, 1.0D, 1.0D, 0.0D), new SimplexNoise.Grad(-1.0D, 1.0D, -1.0D, 0.0D), new SimplexNoise.Grad(-1.0D, -1.0D, 1.0D, 0.0D), new SimplexNoise.Grad(-1.0D, -1.0D, -1.0D, 0.0D)};
    private static short[] p = new short[]{151, 160, 137, 91, 90, 15, 131, 13, 201, 95, 96, 53, 194, 233, 7, 225, 140, 36, 103, 30, 69, 142, 8, 99, 37, 240, 21, 10, 23, 190, 6, 148, 247, 120, 234, 75, 0, 26, 197, 62, 94, 252, 219, 203, 117, 35, 11, 32, 57, 177, 33, 88, 237, 149, 56, 87, 174, 20, 125, 136, 171, 168, 68, 175, 74, 165, 71, 134, 139, 48, 27, 166, 77, 146, 158, 231, 83, 111, 229, 122, 60, 211, 133, 230, 220, 105, 92, 41, 55, 46, 245, 40, 244, 102, 143, 54, 65, 25, 63, 161, 1, 216, 80, 73, 209, 76, 132, 187, 208, 89, 18, 169, 200, 196, 135, 130, 116, 188, 159, 86, 164, 100, 109, 198, 173, 186, 3, 64, 52, 217, 226, 250, 124, 123, 5, 202, 38, 147, 118, 126, 255, 82, 85, 212, 207, 206, 59, 227, 47, 16, 58, 17, 182, 189, 28, 42, 223, 183, 170, 213, 119, 248, 152, 2, 44, 154, 163, 70, 221, 153, 101, 155, 167, 43, 172, 9, 129, 22, 39, 253, 19, 98, 108, 110, 79, 113, 224, 232, 178, 185, 112, 104, 218, 246, 97, 228, 251, 34, 242, 193, 238, 210, 144, 12, 191, 179, 162, 241, 81, 51, 145, 235, 249, 14, 239, 107, 49, 192, 214, 31, 181, 199, 106, 157, 184, 84, 204, 176, 115, 121, 50, 45, 127, 4, 150, 254, 138, 236, 205, 93, 222, 114, 67, 29, 24, 72, 243, 141, 128, 195, 78, 66, 215, 61, 156, 180};
    private static short[] perm = new short[512];
    private static short[] permMod12 = new short[512];
    private static final double F2;
    private static final double G2;
    private static final double F3 = 0.3333333333333333D;
    private static final double G3 = 0.16666666666666666D;
    private static final double F4;
    private static final double G4;

    public SimplexNoise() {
    }

    private static int fastfloor(double x) {
        int xi = (int)x;
        return x < (double)xi ? xi - 1 : xi;
    }

    private static double dot(SimplexNoise.Grad g, double x, double y) {
        return g.x * x + g.y * y;
    }

    private static double dot(SimplexNoise.Grad g, double x, double y, double z) {
        return g.x * x + g.y * y + g.z * z;
    }

    private static double dot(SimplexNoise.Grad g, double x, double y, double z, double w) {
        return g.x * x + g.y * y + g.z * z + g.w * w;
    }

    public static double noise(double xin, double yin) {
        double s = (xin + yin) * F2;
        int i = fastfloor(xin + s);
        int j = fastfloor(yin + s);
        double t = (double)(i + j) * G2;
        double X0 = (double)i - t;
        double Y0 = (double)j - t;
        double x0 = xin - X0;
        double y0 = yin - Y0;
        byte i1;
        byte j1;
        if (x0 > y0) {
            i1 = 1;
            j1 = 0;
        } else {
            i1 = 0;
            j1 = 1;
        }

        double x1 = x0 - (double)i1 + G2;
        double y1 = y0 - (double)j1 + G2;
        double x2 = x0 - 1.0D + 2.0D * G2;
        double y2 = y0 - 1.0D + 2.0D * G2;
        int ii = i & 255;
        int jj = j & 255;
        int gi0 = permMod12[ii + perm[jj]];
        int gi1 = permMod12[ii + i1 + perm[jj + j1]];
        int gi2 = permMod12[ii + 1 + perm[jj + 1]];
        double t0 = 0.5D - x0 * x0 - y0 * y0;
        double n0;
        if (t0 < 0.0D) {
            n0 = 0.0D;
        } else {
            t0 *= t0;
            n0 = t0 * t0 * dot(grad3[gi0], x0, y0);
        }

        double t1 = 0.5D - x1 * x1 - y1 * y1;
        double n1;
        if (t1 < 0.0D) {
            n1 = 0.0D;
        } else {
            t1 *= t1;
            n1 = t1 * t1 * dot(grad3[gi1], x1, y1);
        }

        double t2 = 0.5D - x2 * x2 - y2 * y2;
        double n2;
        if (t2 < 0.0D) {
            n2 = 0.0D;
        } else {
            t2 *= t2;
            n2 = t2 * t2 * dot(grad3[gi2], x2, y2);
        }

        return 70.0D * (n0 + n1 + n2);
    }

    public static double noise(double xin, double yin, double zin) {
        double s = (xin + yin + zin) * 0.3333333333333333D;
        int i = fastfloor(xin + s);
        int j = fastfloor(yin + s);
        int k = fastfloor(zin + s);
        double t = (double)(i + j + k) * 0.16666666666666666D;
        double X0 = (double)i - t;
        double Y0 = (double)j - t;
        double Z0 = (double)k - t;
        double x0 = xin - X0;
        double y0 = yin - Y0;
        double z0 = zin - Z0;
        byte i1;
        byte j1;
        byte k1;
        byte i2;
        byte j2;
        byte k2;
        if (x0 >= y0) {
            if (y0 >= z0) {
                i1 = 1;
                j1 = 0;
                k1 = 0;
                i2 = 1;
                j2 = 1;
                k2 = 0;
            } else if (x0 >= z0) {
                i1 = 1;
                j1 = 0;
                k1 = 0;
                i2 = 1;
                j2 = 0;
                k2 = 1;
            } else {
                i1 = 0;
                j1 = 0;
                k1 = 1;
                i2 = 1;
                j2 = 0;
                k2 = 1;
            }
        } else if (y0 < z0) {
            i1 = 0;
            j1 = 0;
            k1 = 1;
            i2 = 0;
            j2 = 1;
            k2 = 1;
        } else if (x0 < z0) {
            i1 = 0;
            j1 = 1;
            k1 = 0;
            i2 = 0;
            j2 = 1;
            k2 = 1;
        } else {
            i1 = 0;
            j1 = 1;
            k1 = 0;
            i2 = 1;
            j2 = 1;
            k2 = 0;
        }

        double x1 = x0 - (double)i1 + 0.16666666666666666D;
        double y1 = y0 - (double)j1 + 0.16666666666666666D;
        double z1 = z0 - (double)k1 + 0.16666666666666666D;
        double x2 = x0 - (double)i2 + 0.3333333333333333D;
        double y2 = y0 - (double)j2 + 0.3333333333333333D;
        double z2 = z0 - (double)k2 + 0.3333333333333333D;
        double x3 = x0 - 1.0D + 0.5D;
        double y3 = y0 - 1.0D + 0.5D;
        double z3 = z0 - 1.0D + 0.5D;
        int ii = i & 255;
        int jj = j & 255;
        int kk = k & 255;
        int gi0 = permMod12[ii + perm[jj + perm[kk]]];
        int gi1 = permMod12[ii + i1 + perm[jj + j1 + perm[kk + k1]]];
        int gi2 = permMod12[ii + i2 + perm[jj + j2 + perm[kk + k2]]];
        int gi3 = permMod12[ii + 1 + perm[jj + 1 + perm[kk + 1]]];
        double t0 = 0.6D - x0 * x0 - y0 * y0 - z0 * z0;
        double n0;
        if (t0 < 0.0D) {
            n0 = 0.0D;
        } else {
            t0 *= t0;
            n0 = t0 * t0 * dot(grad3[gi0], x0, y0, z0);
        }

        double t1 = 0.6D - x1 * x1 - y1 * y1 - z1 * z1;
        double n1;
        if (t1 < 0.0D) {
            n1 = 0.0D;
        } else {
            t1 *= t1;
            n1 = t1 * t1 * dot(grad3[gi1], x1, y1, z1);
        }

        double t2 = 0.6D - x2 * x2 - y2 * y2 - z2 * z2;
        double n2;
        if (t2 < 0.0D) {
            n2 = 0.0D;
        } else {
            t2 *= t2;
            n2 = t2 * t2 * dot(grad3[gi2], x2, y2, z2);
        }

        double t3 = 0.6D - x3 * x3 - y3 * y3 - z3 * z3;
        double n3;
        if (t3 < 0.0D) {
            n3 = 0.0D;
        } else {
            t3 *= t3;
            n3 = t3 * t3 * dot(grad3[gi3], x3, y3, z3);
        }

        return 32.0D * (n0 + n1 + n2 + n3);
    }

    public static double noise(double x, double y, double z, double w) {
        double s = (x + y + z + w) * F4;
        int i = fastfloor(x + s);
        int j = fastfloor(y + s);
        int k = fastfloor(z + s);
        int l = fastfloor(w + s);
        double t = (double)(i + j + k + l) * G4;
        double X0 = (double)i - t;
        double Y0 = (double)j - t;
        double Z0 = (double)k - t;
        double W0 = (double)l - t;
        double x0 = x - X0;
        double y0 = y - Y0;
        double z0 = z - Z0;
        double w0 = w - W0;
        int rankx = 0;
        int ranky = 0;
        int rankz = 0;
        int rankw = 0;
        if (x0 > y0) {
            ++rankx;
        } else {
            ++ranky;
        }

        if (x0 > z0) {
            ++rankx;
        } else {
            ++rankz;
        }

        if (x0 > w0) {
            ++rankx;
        } else {
            ++rankw;
        }

        if (y0 > z0) {
            ++ranky;
        } else {
            ++rankz;
        }

        if (y0 > w0) {
            ++ranky;
        } else {
            ++rankw;
        }

        if (z0 > w0) {
            ++rankz;
        } else {
            ++rankw;
        }

        int i1 = rankx >= 3 ? 1 : 0;
        int j1 = ranky >= 3 ? 1 : 0;
        int k1 = rankz >= 3 ? 1 : 0;
        int l1 = rankw >= 3 ? 1 : 0;
        int i2 = rankx >= 2 ? 1 : 0;
        int j2 = ranky >= 2 ? 1 : 0;
        int k2 = rankz >= 2 ? 1 : 0;
        int l2 = rankw >= 2 ? 1 : 0;
        int i3 = rankx >= 1 ? 1 : 0;
        int j3 = ranky >= 1 ? 1 : 0;
        int k3 = rankz >= 1 ? 1 : 0;
        int l3 = rankw >= 1 ? 1 : 0;
        double x1 = x0 - (double)i1 + G4;
        double y1 = y0 - (double)j1 + G4;
        double z1 = z0 - (double)k1 + G4;
        double w1 = w0 - (double)l1 + G4;
        double x2 = x0 - (double)i2 + 2.0D * G4;
        double y2 = y0 - (double)j2 + 2.0D * G4;
        double z2 = z0 - (double)k2 + 2.0D * G4;
        double w2 = w0 - (double)l2 + 2.0D * G4;
        double x3 = x0 - (double)i3 + 3.0D * G4;
        double y3 = y0 - (double)j3 + 3.0D * G4;
        double z3 = z0 - (double)k3 + 3.0D * G4;
        double w3 = w0 - (double)l3 + 3.0D * G4;
        double x4 = x0 - 1.0D + 4.0D * G4;
        double y4 = y0 - 1.0D + 4.0D * G4;
        double z4 = z0 - 1.0D + 4.0D * G4;
        double w4 = w0 - 1.0D + 4.0D * G4;
        int ii = i & 255;
        int jj = j & 255;
        int kk = k & 255;
        int ll = l & 255;
        int gi0 = perm[ii + perm[jj + perm[kk + perm[ll]]]] % 32;
        int gi1 = perm[ii + i1 + perm[jj + j1 + perm[kk + k1 + perm[ll + l1]]]] % 32;
        int gi2 = perm[ii + i2 + perm[jj + j2 + perm[kk + k2 + perm[ll + l2]]]] % 32;
        int gi3 = perm[ii + i3 + perm[jj + j3 + perm[kk + k3 + perm[ll + l3]]]] % 32;
        int gi4 = perm[ii + 1 + perm[jj + 1 + perm[kk + 1 + perm[ll + 1]]]] % 32;
        double t0 = 0.6D - x0 * x0 - y0 * y0 - z0 * z0 - w0 * w0;
        double n0;
        if (t0 < 0.0D) {
            n0 = 0.0D;
        } else {
            t0 *= t0;
            n0 = t0 * t0 * dot(grad4[gi0], x0, y0, z0, w0);
        }

        double t1 = 0.6D - x1 * x1 - y1 * y1 - z1 * z1 - w1 * w1;
        double n1;
        if (t1 < 0.0D) {
            n1 = 0.0D;
        } else {
            t1 *= t1;
            n1 = t1 * t1 * dot(grad4[gi1], x1, y1, z1, w1);
        }

        double t2 = 0.6D - x2 * x2 - y2 * y2 - z2 * z2 - w2 * w2;
        double n2;
        if (t2 < 0.0D) {
            n2 = 0.0D;
        } else {
            t2 *= t2;
            n2 = t2 * t2 * dot(grad4[gi2], x2, y2, z2, w2);
        }

        double t3 = 0.6D - x3 * x3 - y3 * y3 - z3 * z3 - w3 * w3;
        double n3;
        if (t3 < 0.0D) {
            n3 = 0.0D;
        } else {
            t3 *= t3;
            n3 = t3 * t3 * dot(grad4[gi3], x3, y3, z3, w3);
        }

        double t4 = 0.6D - x4 * x4 - y4 * y4 - z4 * z4 - w4 * w4;
        double n4;
        if (t4 < 0.0D) {
            n4 = 0.0D;
        } else {
            t4 *= t4;
            n4 = t4 * t4 * dot(grad4[gi4], x4, y4, z4, w4);
        }

        return 27.0D * (n0 + n1 + n2 + n3 + n4);
    }

    static {
        for(int i = 0; i < 512; ++i) {
            perm[i] = p[i & 255];
            permMod12[i] = (short)(perm[i] % 12);
        }

        F2 = 0.5D * (Math.sqrt(3.0D) - 1.0D);
        G2 = (3.0D - Math.sqrt(3.0D)) / 6.0D;
        F4 = (Math.sqrt(5.0D) - 1.0D) / 4.0D;
        G4 = (5.0D - Math.sqrt(5.0D)) / 20.0D;
    }

    private static class Grad {
        double x;
        double y;
        double z;
        double w;

        Grad(double x, double y, double z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        Grad(double x, double y, double z, double w) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.w = w;
        }
    }
}
