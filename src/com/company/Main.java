package com.company;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

class Matrix {
    private String filePath;
    public int rowsCount;
    public int colsCount;
    private double matrixArray[][];
    private Scanner scanner;
    private double inverse[][];

    public Matrix(int n) {
        rowsCount = n;
        colsCount = n;
        matrixArray = new double[n][n];
        inverse = new double[n][n];
    }

    public Matrix(double[][] matrArray) {
        int n = matrArray.length;
        rowsCount = n;
        colsCount = n;
        matrixArray = matrArray;
        inverse = new double[n][n];
    }

    public Matrix(int rCount, int cCount) {
        rowsCount = rCount;
        colsCount = cCount;
        matrixArray = new double[rCount][cCount];
        inverse = new double[rCount][cCount];
    }

    public void setMatrixArray(int i, int j, double num) {
        this.matrixArray[i][j] = num;
    }

    public void setMatrixInverse(double[][] inverse) {
        int rows = inverse.length;
        int cols = inverse[0].length;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                this.inverse[i][j] = inverse[i][j];
            }
        }
    }

    public void setMatrixArray(double[][] matrixArray) {
        int rows = matrixArray.length;
        int cols = matrixArray[0].length;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                this.matrixArray[i][j] = matrixArray[i][j];
            }
        }
    }

    public double[][] getMatrixInverse() {
        return this.inverse;
    }

    public int getLength() {
        return rowsCount;
    }

    public Matrix(String filePath) throws FileNotFoundException {
        this.filePath = filePath;
        this.scanner = new Scanner(new File(this.filePath));
        this.rowsCount = scanner.nextInt();
        this.colsCount = scanner.nextInt();
        this.matrixArray = null;
    }

    public void getMatrixFromFile() {
        matrixArray = new double[this.rowsCount][this.colsCount];
        inverse = new double[this.rowsCount][this.colsCount];
        for (int i = 0; i < this.rowsCount; i++) {
            for (int j = 0; j < this.colsCount; j++) {
                matrixArray[i][j] = scanner.nextDouble();
            }
        }
    }

    public void printMatrix() {
        if (this.matrixArray != null) {
            for (int i = 0; i < this.rowsCount; i++) {
                for (int j = 0; j < this.colsCount; j++) {
                    System.out.print(matrixArray[i][j] + " ");
                }
                System.out.println();
            }
        }
        System.out.println();
    }

    public void printInverseMatrix() {
        if (this.inverse != null) {
            for (int i = 0; i < this.rowsCount; i++) {
                for (int j = 0; j < this.colsCount; j++) {
                    System.out.print(this.inverse[i][j] + " ");
                }
                System.out.println();
            }
        }
        System.out.println();
    }

    public static Matrix infinitesimalGenerator(Matrix[] arrD) {
        Matrix result = new Matrix(arrD[0].getLength());
        int length = arrD.length;
        for (int i = 0; i < length; i++) {
            result = sumTwoMatrix(result, arrD[i]);
        }
        return result;
    }

    public static Matrix sumTwoMatrix(Matrix A, Matrix B) {
        int size1 = A.rowsCount;
        int size2 = B.colsCount;
        Matrix sum = new Matrix(size1, size2);
        for (int i = 0; i < size1; i++) {
            for (int j = 0; j < size2; j++) {
                sum.matrixArray[i][j] = A.matrixArray[i][j] + B.matrixArray[i][j];
            }
        }
        return sum;
    }

    public static Matrix subTwoMatrix(Matrix A, Matrix B) {
        int size = A.matrixArray.length;
        Matrix sub = new Matrix(A.matrixArray.length);
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                sub.matrixArray[i][j] = A.matrixArray[i][j] - B.matrixArray[i][j];
            }
        }
        return sub;
    }

    public double[] vectorMultipleMatrix(double[] vector) {
        int length = this.colsCount;
        double[] result = new double[length];
        //if (vector.length !== this.rowsCount) throw Exception();
        for (int i = 0; i < length; i++) {
            double tmp = 0;
            for (int j = 0; j < this.rowsCount; j++) {
                tmp += vector[j] * this.matrixArray[j][i];
            }
            result[i] = tmp;

        }
        return result;

    }

    public static double vectorMultipleVector(double[] v1, double[] v2) {
        double result = 0;
        for (int i = 0; i < v1.length; i++) {
            result += v1[i] * v2[i];
        }
        return result;
    }

    public static double[] calculateLamda(double[] tau, Matrix[] Di) {
        int count = Di.length - 1;
        double[] e = new double[Di[0].getLength()];
        for (int y = 0; y < e.length; y++) {
            e[y] = 1;
        }
        double[] lambda = new double[count];
        for (int i = 0; i < count; i++) {
            double[] tmp = Di[i + 1].vectorMultipleMatrix(tau);
            lambda[i] = vectorMultipleVector(tmp, e);
        }
        return lambda;
    }

    public static double[] calculateDispersion(double[] lambda, Matrix[] Di, double[] tau) {
        int n = lambda.length;
        double[] result = new double[n];
        for (int i = 0; i < n; i++) {
            Matrix sum = sumTwoMatrix(Di[0], Di[i + 1]);
            sum = sum.multipleMatrixOnValue(-1);
            Matrix.calculateInverseMatrix(sum);
            Matrix tmp = new Matrix(sum.getMatrixInverse());

            double[] vectorMulMatrix = tmp.vectorMultipleMatrix(tau);
            double[] e = new double[vectorMulMatrix.length];
            for (int j = 0; j < e.length; j++) {
                e[j] = 1;
            }
            double minuend = vectorMultipleVector(vectorMulMatrix, e);
            minuend = minuend * 2 / lambda[i];
            double subtrahend = Math.pow(1 / (lambda[i]), 2);

            result[i] = minuend - subtrahend;
        }
        return result;
    }

    public static double[] matrixMultipleVector(Matrix A, double[] vector) {
        int row = A.rowsCount;
        int col = vector.length;
        double[] result = new double[row];
        for (int i = 0; i < row; i++) {
            double sum = 0;
            for (int j = 0; j < col; j++) {
                sum += A.matrixArray[i][j] * vector[j];
            }
            result[i] = sum;
        }
        return result;
    }

    public static double[] matrixMultipleE(Matrix A) {
        int row = A.rowsCount;
        int col = A.colsCount;
        double[] result = new double[row];
        for (int i = 0; i < row; i++) {
            double sum = 0;
            for (int j = 0; j < col; j++) {
                sum += A.matrixArray[i][j];
            }
            result[i] = sum;
        }
        return result;
    }

    public static double[] sumTwoVectors(double[] a, double[] b) {
        double[] sum = new double[a.length];
        for (int i = 0; i < a.length; i++) {
            sum[i] = a[i] + b[i];
        }
        return sum;
    }

    public static Matrix matrixMultipleMatrix(Matrix A, Matrix B) {
        int rows = A.rowsCount;
        int cols = B.colsCount;
        int N = A.colsCount;
        double sum;
        Matrix result = new Matrix(rows, cols);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                sum = 0;
                for (int k = 0; k < N; k++) {
                    sum += A.matrixArray[i][k] * B.matrixArray[k][j];
                }
                result.setMatrixArray(i, j, sum);
            }
        }
        return result;
    }

    public static double[] calculateKoeffKorrel(double[] lamda, Matrix[] Di, double[] tau, double[] dispersion) {
        int n = lamda.length;
        double[] result = new double[n];
        for (int i = 0; i < n; i++) {
            Matrix sum = sumTwoMatrix(Di[0], Di[i + 1]);
            Matrix.calculateInverseMatrix(sum);
            Matrix inverse = new Matrix(sum.getMatrixInverse());
            double[] res1 = inverse.vectorMultipleMatrix(tau);
            double[] res2 = Di[i].vectorMultipleMatrix(res1);
            double[] res3 = inverse.vectorMultipleMatrix(res2);
            double[] e = new double[res1.length];
            for (int j = 0; j < e.length; j++) {
                e[j] = 1;
            }
            double res4 = Matrix.vectorMultipleVector(res3, e);
            double minuend = res4 / lamda[i];
            double subtrahend = Math.pow(1 / (lamda[i]), 2);
            result[i] = (minuend - subtrahend) * (1 / dispersion[i]);
        }
        return result;
    }

    public static double[] Gauss(Matrix matrix, int z) {
        double[] f = new double[matrix.rowsCount];
        int n = f.length;
//        System.out.println("n = "+ n);
        f[0] = 1;
//        System.out.println("f:");
//        for (int u = 0; u < f.length; u++){
//            System.out.print(f[u] + " ");
//        }
//        System.out.println();
        //Matrix res = new Matrix(matrix.matrixArray);
//        System.out.println("af_tr");
//        res.printMatrix();

//        System.out.println("f:");
//        for (int i = 0; i < f.length; i++) {
//            System.out.print(f[i] + " ");
//        }
//        System.out.println();
        Matrix res = new Matrix(matrix.matrixArray);
        if (z == 1) {
            for (int t = 0; t < res.rowsCount; t++) {
                res.setMatrixArray(t, 0, 1);
            }
        }

        res = Matrix.transposeMatrix(matrix);


        double[] x = new double[n];
        double tmp;
        for (int i = 0; i < n; i++) {
            tmp = res.matrixArray[i][i];
//            System.out.println("res[" + i + "][" + i + "]" + tmp);
            f[i] /= tmp;
//            System.out.println("f[o] = " + f[0]);
            for (int j = 0; j < n; j++) {
                res.matrixArray[i][j] /= tmp;
            }
//            System.out.println("После деления");
//            res.printMatrix();
//
//            System.out.println("f[o] = " + f[0]);
//            System.out.println("f[1] = " + f[1]);

            for (int j = i + 1; j < n; j++) {
                tmp = res.matrixArray[j][i];
                f[j] -= tmp * f[i];
                for (int k = i; k < n; k++) {
                    res.matrixArray[j][k] -= tmp * res.matrixArray[i][k];
                }
            }
//            System.out.println("После зануления");
//
//            System.out.println("f[o] = " + f[0]);
//            System.out.println("f[1] = " + f[1]);
//            res.printMatrix();

        }
//        System.out.println("f[o] = " + f[0]);
//        System.out.println("f[1] = " + f[1]);
//        System.out.println("res_обратный ход");
//        res.printMatrix();

        /*обратный ход*/
        x[n - 1] = f[n - 1];
        for (int i = n - 2; i >= 0; i--) {
            x[i] = f[i];
            for (int j = n - 1; j > i; j--) {
                x[i] = f[i] - res.matrixArray[i][j] * f[i + 1];
            }
        }
//        System.out.println("x:");
//        for (int i = 0; i < x.length; i++) {
//            System.out.print(x[i] + " ");
//        }
//        System.out.println();
        return x;
    }


    public static double[][] calculateInverseMatrix(Matrix matrix) {
        Matrix remember = new Matrix(matrix.getLength());
        remember.setMatrixArray(matrix.matrixArray);
        int n = matrix.getLength();
        Matrix test = new Matrix(matrix.matrixArray);
        //создаем единичную матрицу для формирования обратной
        for (int i = 0; i < n; i++) {
            test.inverse[i][i] = 1;
        }

        double del;

        for (int i = 0; i < n; i++) {
            del = test.matrixArray[i][i];
            //делим строку
            for (int j = 0; j < n; j++) {
                test.matrixArray[i][j] /= del;
                test.inverse[i][j] /= del;
            }

            double[] str = new double[n];
            double[] str2 = new double[n];
            for (int w = 0; w < n; w++) {
                str[w] = test.matrixArray[i][w];
                str2[w] = test.inverse[i][w];
            }

            //зануляем элементы под единицей
            for (int t = i + 1; t < n; t++) { //выбираем строку для зануления
                del = test.matrixArray[t][i];
                double[] change1 = Matrix.multipleVectorValue(str, del);
                double[] change2 = Matrix.multipleVectorValue(str2, del);
                for (int k = 0; k < n; k++) { //зануляем элемент и изменяем строку
                    test.matrixArray[t][k] = test.matrixArray[t][k] - change1[k];
                    test.inverse[t][k] = test.inverse[t][k] - change2[k];
                }
            }
        }

        /*обратный ход*/
        double[] str = new double[n];

        for (int i = n - 1; i > 0; i--) {//столбцы
            for (int w = 0; w < n; w++) {
                str[w] = test.inverse[i][w];
            }
            for (int j = i - 1; j >= 0; j--) {//строки
                del = test.matrixArray[j][i];
                test.matrixArray[j][i] = 0;
                double[] tmp = str;
                for (int w = 0; w < n; w++) {
                    tmp[w] = str[w];
                    test.inverse[j][w] -= del * tmp[w];
                }
            }
        }
        matrix.setMatrixArray(remember.matrixArray);
        matrix.setMatrixInverse(test.inverse);

        return test.getMatrixInverse();
    }

    public static Matrix transposeMatrix(Matrix A) {
        Matrix T = new Matrix(A.rowsCount, A.colsCount);
        for (int i = 0; i < A.rowsCount; i++)
            for (int j = 0; j < A.colsCount; j++) {
                T.setMatrixArray(i, j, A.matrixArray[j][i]);
            }
        return T;
    }

    private static double[] multipleVectorValue(double[] vector, double value) {
        double[] tmp = new double[vector.length];
        for (int j = 0; j < vector.length; j++) {
            tmp[j] = vector[j];
        }
        for (int i = 0; i < tmp.length; i++) {
            tmp[i] *= value;
        }
        return tmp;
    }

    private static double[][] getInverseMatrix(Matrix matrix) {
        int n = matrix.getLength();
        for (int i = n - 2; i > 0; i--) {
            for (int j = 0; j < n; j++) {
                double del = matrix.matrixArray[i][j];
                double[] tmp = matrix.inverse[i - 1];
                for (int k = 0; k < n; k++) {
                    tmp[k] *= del;
                    matrix.inverse[i][k] += tmp[k];
                }
                matrix.inverse[i][j] = 0;
            }
        }
        return matrix.inverse;
    }

    public Matrix multipleMatrixOnValue(double a) {
        Matrix tmp = new Matrix(this.rowsCount, this.colsCount);
        for (int i = 0; i < this.rowsCount; i++)
            for (int j = 0; j < this.colsCount; j++) {
                tmp.setMatrixArray(i, j, this.matrixArray[i][j]);
            }

        for (int i = 0; i < this.getLength(); i++) {
            for (int j = 0; j < this.getLength(); j++) {
                tmp.matrixArray[i][j] = a * tmp.matrixArray[i][j];
            }
        }
        return tmp;
    }

    public static Matrix kroneckerMultiple(Matrix A, Matrix B) {
        int k = B.getLength();
        Matrix result = new Matrix(A.getLength() * B.getLength());
        for (int ia = 0; ia < A.getLength(); ia++) {
            for (int ja = 0; ja < A.getLength(); ja++) {

                for (int ib = 0; ib < B.getLength(); ib++) {
                    for (int jb = 0; jb < B.getLength(); jb++) {
                        result.matrixArray[k * ia + ib][k * ja + jb] = A.matrixArray[ia][ja] * B.matrixArray[ib][jb];
                    }
                }

            }
        }
        return result;
    }

    public static Matrix kroneckerMultipleRectangleMatrix(Matrix A, Matrix B) {
        Matrix result = new Matrix(A.rowsCount * B.rowsCount, A.colsCount * B.colsCount);
        int rowsOffset = B.rowsCount;
        int colsOffset = B.colsCount;
        for (int ia = 0; ia < A.rowsCount; ia++) {
            for (int ja = 0; ja < A.colsCount; ja++) {

                for (int ib = 0; ib < B.rowsCount; ib++) {
                    for (int jb = 0; jb < B.colsCount; jb++) {
                        result.matrixArray[rowsOffset * ia + ib][colsOffset * ja + jb] = A.matrixArray[ia][ja] * B.matrixArray[ib][jb];
                    }
                }

            }
        }

        return result;
    }

    public static Matrix I(int n) {
        Matrix I = new Matrix(n);
        for (int i = 0; i < n; i++) {
            I.matrixArray[i][i] = 1;
        }
        return I;
    }

    private static Matrix createDiagonalMatrix(int n, double m_from, double[] mu) {
        Matrix res = new Matrix(n);
        n--;
        for (int i = 0; i <= n; i++, m_from++) {
            res.matrixArray[i][i] = (n - m_from) * mu[1] + m_from * mu[0];
        }
        return res;
    }

    public static Matrix[][] infGeneratorQ(Matrix[] Di, double[] mu, Matrix[][] Q, int N, int W) {

        //диагональные матрицы
        Q[0][0] = Di[0];

        for (int n = 1; n < N; n++) {
            Matrix diagonal = new Matrix(n + 1);
            for (int m = 0; m < n + 1; m++) {
                diagonal.matrixArray[m][m] = (n - m) * mu[1] + m * mu[0];
            }
            Q[n][n] = subTwoMatrix(kroneckerMultiple(I(n + 1), Di[0]), kroneckerMultiple(diagonal, Matrix.I(W + 1)));
        }

        Matrix mul1, mul2;
        Matrix infGen = Matrix.infinitesimalGenerator(Di);
        mul1 = Matrix.kroneckerMultiple(I(N + 1), infGen);
        mul2 = Matrix.kroneckerMultiple(createDiagonalMatrix(N + 1, 0, mu), I(W + 1));

        Q[N][N] = Matrix.subTwoMatrix(mul1, mul2);

        //поддиагональные матрицы
        for (int n = 1; n <= N; n++) {
            Matrix M1 = new Matrix(n + 1, n);
            Matrix M2 = new Matrix(n + 1, n);
            for (int j = 0; j < n; j++) {
                M1.matrixArray[j][j] = (n - j) * mu[1];
            }
            for (int j = 1; j <= n; j++) {
                M2.matrixArray[j][j - 1] = j * mu[0];
            }
            Matrix tmp = Matrix.kroneckerMultipleRectangleMatrix(M1, I(W + 1));
            M2 = Matrix.kroneckerMultipleRectangleMatrix(M2, I(W + 1));
            Q[n][n - 1] = Matrix.sumTwoMatrix(tmp, M2);
        }

        //создать наддиагональную матрицу
        for (int n = 0; n < N; n++) {
            Matrix Q_tmp = new Matrix((W + 1) * (n + 1), (W + 1) * (n + 2));
            int D_i;
            int size_D = W + 1;
            for (int k = 0; k < n + 1; k++) {
                for (int t = k; t <= k + 1; t++) {
                    if (k == t) D_i = 2;
                    else D_i = 1;
                    //записываем маленькую матрицу в большую
                    for (int i = 0; i < size_D; i++) {
                        for (int j = 0; j < size_D; j++) {
                            Q_tmp.matrixArray[k * size_D + i][t * size_D + j] = Di[D_i].matrixArray[i][j];
                        }
                    }
                }
            }
            Q[n][n + 1] = Q_tmp;
        }

        return Q;
    }

    public static Matrix[] G_i(Matrix[][] inf_gen_Q, int N) {
        Matrix[] G = new Matrix[N];

        Matrix tmp = inf_gen_Q[N][N].multipleMatrixOnValue(-1);
        //находим последнее G[N-1]
        Matrix.calculateInverseMatrix(tmp);
//        tmp.printMatrix();
//        System.out.println("Inversion (-Q[N][N]):");
//        tmp.printInverseMatrix();

        Matrix inversion = new Matrix(tmp.getLength());
        inversion.setMatrixArray(tmp.getMatrixInverse());
        G[N - 1] = Matrix.matrixMultipleMatrix(inversion, inf_gen_Q[N][N - 1]);
//        System.out.println("Firstly G[" + (N - 1) + "]");
//        G[N - 1].printMatrix();


        //уравнение обратной рекурсии
        for (int i = N - 2; i >= 0; i--) {
//            System.out.println("Для умножения G[" + (i + 1) + "]");
//            G[i + 1].printMatrix();
            Matrix mul = Matrix.matrixMultipleMatrix(inf_gen_Q[i + 1][i + 2], G[i + 1]);

            Matrix sum = Matrix.sumTwoMatrix(inf_gen_Q[i + 1][i + 1], mul);
            sum = sum.multipleMatrixOnValue(-1);


            Matrix.calculateInverseMatrix(sum);

            Matrix inversionM = new Matrix(sum.getMatrixInverse());

            Matrix result = Matrix.matrixMultipleMatrix(inversionM, inf_gen_Q[i + 1][i]);
            G[i] = result;
//
//            System.out.println("Result G[" + i + "]");
//            G[i].printMatrix();
        }
        return G;
    }

    public static Matrix[][] new_inf_gen_Q(Matrix[][] Q, Matrix[] G, int N) {
        for (int i = 0; i < N; i++) {
            Matrix mul = Matrix.matrixMultipleMatrix(Q[i][i + 1], G[i]);
            Matrix sum = Matrix.sumTwoMatrix(Q[i][i], mul);
            Q[i][i] = sum;
        }
        return Q;
    }

    public static Matrix[] Fi(Matrix[][] Q, int W) {
        int N = Q.length;
        Matrix[] F = new Matrix[N];
        F[0] = I(W + 1);
        for (int i = 1; i < N; i++) {
            Matrix for_inverse = Q[i][i];
            for_inverse = for_inverse.multipleMatrixOnValue(-1);
            Matrix.calculateInverseMatrix(for_inverse);
            Matrix inv = new Matrix(for_inverse.getMatrixInverse());

            Matrix tmp = Matrix.matrixMultipleMatrix(F[i - 1], Q[i - 1][i]);
            Matrix result = Matrix.matrixMultipleMatrix(tmp, inv);

            F[i] = result;
        }

        return F;
    }

    public static double[] calculateQ0(Matrix[] F, Matrix[][] Q) {
        int N = F.length;
        int rows = F[0].rowsCount;

        double[] sum = new double[rows];
        for (int i = 0; i < N; i++) {
            double[] tmp = Matrix.matrixMultipleE(F[i]);
            sum = Matrix.sumTwoVectors(sum, tmp);

        }
        Matrix tmp = Q[0][0];
//        System.out.println();
//        System.out.println("tmp");
//        tmp.printMatrix();


        // заменяем вектор-столбец
        for (int k = 0; k < tmp.getLength(); k++) {
            tmp.setMatrixArray(k, 0, sum[k]);
        }
//
//        System.out.println();
//        System.out.println("tmp_after_transpose");
//        tmp.printMatrix();

        double[] res = Matrix.Gauss(tmp, 0);
//
//        System.out.println("result");
//        for (int z = 0; z < N; z++) {
//            System.out.print(res[z] + " ");
//        }

        //System.out.println("ПРОВЕРКА " + Matrix.vectorMultipleVector(res, sum));

        return res;
    }

//    public static double[] vectorMultipleMatrix(double[] b, Matrix A) {
//        double[] res = new double[A.colsCount];
//        double sum;
//        for (int i = 0; i < A.colsCount; i++) {
//            sum = 0;
//            for (int j = 0; j < b.length; j++) {
//                sum += b[j] * A.matrixArray[j][i];
//            }
//            res[i] = sum;
//        }
//
//        return res;
//    }


    private static double[] createE(int n) {
        double[] e = new double[n];
        for (int i = 0; i < n; i++) {
            e[i] = 1;
        }
        return e;
    }

    public static double[][] calculateQi(Matrix[] F, double[] q0) {
        //размещаем вектора qi в матрице горизонтально
        int N = F.length;
        int l = F[N - 1].colsCount;
        double[][] qi = new double[N][l];
        qi[0] = q0;
        for (int i = 1; i < N; i++) {
            qi[i] = F[i].vectorMultipleMatrix(q0);
        }
//        // найдем норму q[i]
//        double[] qi_norma = new double[N];
//        for (int w = 0; w < N; w++) {
//            double[] e = Matrix.createE(qi[w].length);
//            qi_norma[w] = Matrix.vectorMultipleVector(qi[w], e);
//
//        }
//
//        System.out.println("Распределение числа занятых приборов в системе:");
//        System.out.println("q[i] = q[i]*e");
//        for (int j = 0; j < qi_norma.length; j++) {
//            System.out.println("q[" + j + "] : " + qi_norma[j]);
//        }
//        System.out.println();
        double[] qi_norma = Matrix.distribution_occupied_devices(qi);


//        System.out.println("Распределение числа занятых приборов в системе:");
//        System.out.println("q[i] = q[i]*e");
//        for (int j = 0; j < qi_norma.length; j++) {
//            System.out.println("q[" + j + "] : " + qi_norma[j]);
//        }
//        System.out.println();

        return qi;
    }

    //распределение числа занятых приборов в системе
    public static double[] distribution_occupied_devices(double[][] qi) {
        int N = qi.length;
        double[] qi_norma = new double[N];
        for (int w = 0; w < N; w++) {
            double[] e = Matrix.createE(qi[w].length);
            qi_norma[w] = Matrix.vectorMultipleVector(qi[w], e);
        }
        return qi_norma;
    }

    public static double N_busy(double[] qi) {
        double N = 0;
        for (int i = 1; i < qi.length; i++) {
            N += i * qi[i];
        }
        return N;
    }

    public static double[][] vectors_conditions(double[][] qi, int W) {
        int N = qi.length;
        double[][] qi_ = new double[N][];
        for (int i = 0; i < N; i++) {
            double[] e = Matrix.createE(i + 1);
            Matrix tmp = Matrix.kroneckerVectorOnMatrix(e, Matrix.I(W + 1));//??
            qi_[i] = tmp.vectorMultipleMatrix(qi[i]);
        }
        return qi_;
    }

    //среднее число занятых приборов

    private static Matrix kroneckerVectorOnMatrix(double[] vector, Matrix M) {
        Matrix e_matrix = new Matrix(1, vector.length);
        for (int i = 0; i < vector.length; i++) {
            e_matrix.setMatrixArray(0, i, vector[i]);
        }
        Matrix res = Matrix.kroneckerMultiple(e_matrix, M);

        return res;
    }

}

public class Main {
    public static void main(String[] args) {
        Matrix D0 = null;
        Matrix D1 = null;
        Matrix D2 = null;
        try {
            D0 = new Matrix("inputData/D0.txt");
            D1 = new Matrix("inputData/D1.txt");
            D2 = new Matrix("inputData/D2.txt");
        } catch (FileNotFoundException exception) {
        }
        ;
        D0.getMatrixFromFile();
        D1.getMatrixFromFile();
        D2.getMatrixFromFile();
//
//
//        System.out.println("D0:");
//        D0.printMatrix();
//        System.out.println("D1:");
//        D1.printMatrix();
//        System.out.println("D2:");
//        D2.printMatrix();
//        System.out.println();


        Scanner sc;
        int N = 0;
        int W = 1;
        double[] mu = new double[2];
        Matrix infGenerator;
        Matrix[] arrD;
        try {
            sc = new Scanner(new File("inputData/N.txt"));
            N = sc.nextInt();

        } catch (FileNotFoundException exception) {
        }

        try {
            sc = new Scanner(new File("inputData/W.txt"));
            W = sc.nextInt();

        } catch (FileNotFoundException exception) {
        }

        try {
            Scanner sc2 = new Scanner(new File("inputData/Mu.txt"));

            for (int i = 0; i < 2; i++) {

                mu[i] = sc2.nextDouble();
            }

        } catch (FileNotFoundException exception) {
        }

        for (int i = 0; i < mu.length; i++) {
            System.out.println("mu [" + (i + 1) + "] = " + mu[i]);
        }
        System.out.println();

        arrD = new Matrix[3];
        arrD[0] = D0;
        arrD[1] = D1;
        arrD[2] = D2;

        infGenerator = Matrix.infinitesimalGenerator(arrD);

        Matrix tmp = new Matrix(N);
        double[] tau = tmp.Gauss(infGenerator, 1);

        System.out.println();
        System.out.println();

        double[] lamdaArray = Matrix.calculateLamda(tau, arrD);
        for (int j = 0; j < lamdaArray.length; j++) {
            System.out.println("lambda " + (j + 1) + " = " + lamdaArray[j] + " ");
        }
        System.out.println();


        double[] dispersion = Matrix.calculateDispersion(lamdaArray, arrD, tau);
        for (int j = 0; j < dispersion.length; j++) {
            System.out.println("dispersion " + (j) + " = " + dispersion[j] + " ");
        }
        System.out.println();

        double[] koeffKorel = Matrix.calculateKoeffKorrel(lamdaArray, arrD, tau, dispersion);
        for (int j = 0; j < koeffKorel.length; j++) {
            System.out.println("koeff korellaci " + j + " = " + koeffKorel[j] + " ");
        }
        System.out.println();



        /*////////////////////////////////////////*/
        Matrix[][] Q_inf_gen = new Matrix[N + 1][N + 1];
        Q_inf_gen = Matrix.infGeneratorQ(arrD, mu, Q_inf_gen, N, W);
//

//        for (int k = 0; k <= N; k++) {
//            if (k == 0) {
//                System.out.println("Q[" + k + "][" + k + "]:");
//                Q_inf_gen[k][k].printInverseMatrix();
//                System.out.println();
//                System.out.println("Q[" + k + "][" + (k + 1) + "]:");
//                Q_inf_gen[k][k + 1].printInverseMatrix();
//                System.out.println();
//
//            }
//            if (k == N) {
//                System.out.println("Q[" + k + "][" + (k-1) + "]:");
//                Q_inf_gen[k][k-1].printInverseMatrix();
//                System.out.println();
//                System.out.println("Q[" + k + "][" + k + "]:");
//                Q_inf_gen[k][k].printInverseMatrix();
//                System.out.println();
//            } else
//                {
//                    System.out.println("Q[" + k + "][" + (k-1) + "]:");
//                    Q_inf_gen[k][k-1].printInverseMatrix();
//                    System.out.println();
//                    System.out.println("Q[" + k + "][" + k + "]:");
//                    Q_inf_gen[k][k].printInverseMatrix();
//                    System.out.println();
//                    System.out.println("Q[" + k + "][" + (k + 1) + "]:");
//                    Q_inf_gen[k][k + 1].printInverseMatrix();
//                    System.out.println();
//                }
//        }
//
//        System.out.println("Q[0][0]:");
//        Q_inf_gen[0][0].printMatrix();
//        System.out.println();
//
//        System.out.println("Q[0][1]:");
//        Q_inf_gen[0][1].printMatrix();
//        System.out.println();
//
//
//        System.out.println("Q[1][0]:");
//        Q_inf_gen[1][0].printMatrix();
//        System.out.println();
//
//        System.out.println("Q[1][1]:");
//        Q_inf_gen[1][1].printMatrix();
//        System.out.println();
//
//        System.out.println("Q[1][2]:");
//        Q_inf_gen[1][2].printMatrix();
//        System.out.println();
//
//
//        System.out.println("Q[2][1]:");
//        Q_inf_gen[2][1].printMatrix();
//        System.out.println();
//
//        System.out.println("Q[2][2]:");
//        Q_inf_gen[2][2].printMatrix();
//        System.out.println();
////
//        System.out.println("Q[2][3]:");
//        Q_inf_gen[2][3].printMatrix();
//        System.out.println();
//
//        System.out.println("Q[3][2]:");
//        Q_inf_gen[3][2].printMatrix();
//        System.out.println();
//
//        System.out.println("Q[3][3]:");
//        Q_inf_gen[3][3].printMatrix();
//        System.out.println();
//
//        System.out.println();
//        System.out.println("Матрицы G");
        Matrix[] Gi = Matrix.G_i(Q_inf_gen, N);
//        for (int i = 0; i < Gi.length; i++) {
//            System.out.println("G[" + i + "]:");
//            Gi[i].printMatrix();
//        }


        Q_inf_gen = Matrix.new_inf_gen_Q(Q_inf_gen, Gi, N);
//
//
//        System.out.println("Q[0][0]:");
//        Q_inf_gen[0][0].printMatrix();
//        System.out.println();
//
//        System.out.println("Q[1][1]:");
//        Q_inf_gen[1][1].printMatrix();
//        System.out.println();
//
//        System.out.println("Q[2][2]:");
//        Q_inf_gen[2][2].printMatrix();
//        System.out.println();
//
//        System.out.println("Q[3][3]:");
//        Q_inf_gen[3][3].printMatrix();
//        System.out.println();
//
        Matrix[] F = Matrix.Fi(Q_inf_gen, W);
//        for (int i = 0; i < F.length; i++) {
//            System.out.println("F[" + i + "]:");
//            F[i].printMatrix();
//        }
//
//        double[] tn = Matrix.matrixMultipleE(F[1]);
//        System.out.println("fn:");
//        for (int i = 0; i < tn.length; i++) {
//            System.out.println(tn[i] + " ");
//        }
//
        double[] q0 = Matrix.calculateQ0(F, Q_inf_gen);

        double[][] qi = Matrix.calculateQi(F, q0);
//        for (int i = 0; i < F.length; i++) {
//            System.out.print("q" + "[" + i + "] = ");
//            for (int j = 0; j < qi[i].length; j++) {
//                System.out.print(qi[i][j] + " ");
//            }
//            System.out.println();
//        }

        double[] qi_norma = Matrix.distribution_occupied_devices(qi);
//
//        System.out.println();
//        System.out.println("N_busy = " + Matrix.N_busy(qi_norma));
//        double[][] t = {{1, -1, 2}, {2, 1, -3}, {5, 6, 7} };
//        Matrix poi = new Matrix(t);
//        poi.printMatrix();
//        Matrix res = Matrix.transposeMatrix(poi);
//        res.printMatrix();
//        //double[] m = Matrix.;
//        for (int p = 0; p < m.length; p++) {
//            System.out.print(m[p] + " ");
//        }
//
//        poi.setMatrixArray(t);
//        poi.printMatrix();
//        double[] m = Matrix.Gauss(poi, 0);
//        for (int p = 0; p < m.length; p++) {
//            System.out.print(m[p] + " ");
//        }
//


        double[] C_k = new double[]{1, 3, 5, 7, 9, 11, 13, 15, 17, 19};
        Matrix points = new Matrix(N, 2);
        double lambda_sum_remember = 0;
        for (int t = 0; t < lamdaArray.length; t++) {
            lambda_sum_remember += lamdaArray[t];
        }

        lambda_sum_remember *= 0.1;

        for (int i = 0; i < C_k.length; i++) {
            double lambda_sum = lambda_sum_remember*C_k[i];
            points.setMatrixArray(i, 0, lambda_sum);

            Matrix[] arrD_new = new Matrix[arrD.length];
            for (int t = 0; t < arrD.length; t++) {
                arrD_new[t] = arrD[t].multipleMatrixOnValue(C_k[i]);
            }
            Q_inf_gen = Matrix.infGeneratorQ(arrD_new, mu, Q_inf_gen, N, W);
            Gi = Matrix.G_i(Q_inf_gen, N);
            Q_inf_gen = Matrix.new_inf_gen_Q(Q_inf_gen, Gi, N);
            F = Matrix.Fi(Q_inf_gen, W);
            q0 = Matrix.calculateQ0(F, Q_inf_gen);

            qi = Matrix.calculateQi(F, q0);
            qi_norma = Matrix.distribution_occupied_devices(qi);
            points.setMatrixArray(i, 1, Matrix.N_busy(qi_norma));
        }

        points.printMatrix();
    }
}