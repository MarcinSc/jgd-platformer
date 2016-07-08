package com.gempukku.gaming.rendering.postprocess.blur;

public class GaussianBlurKernel {
    private static final float SIGMA_HELPER = 3.5676f;

    private GaussianBlurKernel() {
    }

    public static float[] create1DBlurKernel(int size) {
        float[] result = new float[1 + size];
        float sigma = size / SIGMA_HELPER;
        float norm = 1f / ((float) Math.sqrt(2 * Math.PI) * sigma);
        float coeff = 2 * sigma * sigma;
        float total = 0;
        for (int i = 0; i <= size; i++) {
            float value = norm * (float) Math.exp(-i * i / coeff);
            result[i] = value;
            total += value;
            if (i > 0)
                total += value;
        }
        for (int i = 0; i <= size; i++) {
            result[i] /= total;
        }
        return result;
    }

    public static float[] createSymmetric1DBlurKernel(int size) {
        float[] kernel = create1DBlurKernel(size);

        float[] result = new float[1 + size * 2];
        for (int i = 0; i < kernel.length; i++) {
            result[size + i] = result[i];
            result[size - i] = result[i];
        }
        return result;
    }

    public static float[][] create2DBlurKernel(int size) {
        float[] kernel = create1DBlurKernel(size);

        float[][] result = new float[1 + size][1 + size];
        for (int x = 0; x < result.length; x++) {
            for (int y = 0; y < result.length; y++) {
                result[x][y] = kernel[x] * kernel[y];
            }
        }
        return result;
    }

    public static void main(String[] args) {
        float[][] kernel = create2DBlurKernel(3);
        for (int i = 0; i < kernel.length; i++) {
            for (int j = 0; j < kernel.length; j++) {
                System.out.print(kernel[i][j] + " ");
            }
            System.out.println();
        }
    }
}
