import java.util.ArrayList;

public class Solve
extends decipher {
    private double[][] m;
    private double[] solution;
    private int rows;
    private int cols;
    private int[] sumR;
    private int[] sumP;
    private String[] elements2;

    Solve(String equation) throws Exception {
        super(equation);
        this.elements2 = this.elements;
        this.doAlgorithm();
    }

    void doAlgorithm() {
        this.fillMatrix();
        this.makeOnes();
        this.solveMatrix();
        while (!this.checkDecimal()) {
            this.roundSolution();
        }
    }

    @Override
    ArrayList<String> debug() {
        ArrayList<String> debugArray = super.debug();
        debugArray.add("\r\n-- Matrix --");
        int i = 0;
        while (i < this.rows) {
            String row = String.valueOf(this.elements2[i]) + ":  ";
            int j = 0;
            while (j < this.cols) {
                if (this.m[i][j] >= 0.0) {
                    row = String.valueOf(row) + " +" + this.m[i][j] + (char)(65 + j) + " ";
                } else if (this.m[i][j] < 0.0) {
                    row = String.valueOf(row) + " " + this.m[i][j] + (char)(65 + j) + " ";
                }
                ++j;
            }
            debugArray.add(row);
            ++i;
        }
        return debugArray;
    }

    private void fillMatrix() {
        this.m = super.getCoefficients();
        this.rows = this.m.length;
        this.cols = this.m[0].length;
    }

    void printMatrix() {
        int i = 0;
        while (i < this.rows) {
            System.out.print(String.valueOf(this.elements2[i]) + ":\t");
            int j = 0;
            while (j < this.cols) {
                if (this.m[i][j] >= 0.0) {
                    System.out.print(" +" + this.m[i][j] + (char)(65 + j) + " ");
                } else if (this.m[i][j] < 0.0) {
                    System.out.print(" " + this.m[i][j] + (char)(65 + j) + " ");
                }
                ++j;
            }
            System.out.println();
            ++i;
        }
        System.out.println();
    }

    private void makeOnes() {
        int i = 0;
        while (i < this.cols) {
            if (i < this.rows && i < this.cols) {
                int k;
                double largest = Math.abs(this.m[i][i]);
                int index = i;
                int j = i + 1;
                while (j < this.rows) {
                    if (Math.abs(this.m[j][i]) > largest) {
                        largest = this.m[j][i];
                        index = j;
                    }
                    ++j;
                }
                this.swapRows(i, index);
                this.makeZeros(i);
                double factor = this.m[i][i];
                if (factor != 0.0) {
                    k = 0;
                    while (k < this.cols) {
                        double[] arrd = this.m[i];
                        int n = k++;
                        arrd[n] = arrd[n] / factor;
                    }
                } else if (this.rowNoZero(i) != -1) {
                    k = 0;
                    while (k < this.cols) {
                        double[] arrd = this.m[i];
                        int n = k;
                        arrd[n] = arrd[n] + this.m[this.rowNoZero(i)][k];
                        ++k;
                    }
                    double factor1 = this.m[i][i];
                    int k2 = 0;
                    while (k2 < this.cols) {
                        double[] arrd = this.m[i];
                        int n = k2++;
                        arrd[n] = arrd[n] / factor1;
                    }
                }
            }
            while (this.rows > this.cols) {
                this.removeRow();
            }
            ++i;
        }
    }

    private int rowNoZero(int col) {
        int row = -1;
        int i = 0;
        while (i < this.rows) {
            if (this.m[i][col] != 0.0) {
                row = i;
                break;
            }
            ++i;
        }
        return row;
    }

    private void swapRows(int row1, int row2) {
        double[] temp = this.m[row1];
        this.m[row1] = this.m[row2];
        this.m[row2] = temp;
        String tempe = this.elements2[row1];
        this.elements2[row1] = this.elements2[row2];
        this.elements2[row2] = tempe;
    }

    private void makeZeros(int row) {
        double factor;
        int i;
        int j;
        if (row > 0 && row < this.cols - 1) {
            i = 0;
            while (i < row) {
                factor = this.m[row][i];
                j = 0;
                while (j < this.cols) {
                    double[] arrd = this.m[row];
                    int n = j;
                    arrd[n] = arrd[n] - factor * this.m[i][j];
                    ++j;
                }
                ++i;
            }
        }
        if (row < this.cols - 1) {
            i = 0;
            while (i < row) {
                factor = this.m[row][i];
                j = row + 1;
                while (j < this.cols) {
                    double[] arrd = this.m[row];
                    int n = j;
                    arrd[n] = arrd[n] - factor * this.m[i][j];
                    ++j;
                }
                ++i;
            }
        }
    }

    private void solveMatrix() {
        this.switchSigns();
        this.solution = new double[this.cols];
        this.solution[this.solution.length - 1] = 1.0;
        int i = this.rows - 1;
        while (i >= 0) {
            double[] arrd = this.solution;
            int n = i;
            arrd[n] = arrd[n] - this.substitute(i);
            this.solution[i] = Math.abs(this.solution[i]);
            --i;
        }
    }

    private void removeRow() {
        int rowIndex = 0;
        int smallest = this.countZeros(0);
        int i = 1;
        while (i < this.rows) {
            if (this.countZeros(i) <= smallest) {
                rowIndex = i;
                smallest = this.countZeros(i);
            }
            ++i;
        }
        if (rowIndex < this.rows - 1) {
            i = rowIndex;
            while (i < this.rows) {
                int j = 0;
                while (j < this.cols - 1) {
                    this.m[rowIndex][j] = this.m[rowIndex + 1][j];
                    ++j;
                }
                ++i;
            }
        }
        --this.rows;
    }

    private int countZeros(int row) {
        int numZeroes = 0;
        int i = 0;
        while (i < this.cols) {
            if (this.m[row][i] == 0.0) {
                ++numZeroes;
            }
            ++i;
        }
        return numZeroes;
    }

    private double substitute(int row) {
        double sum = 0.0;
        int i = this.cols - 1;
        while (i > row) {
            sum += this.m[row][i] * this.solution[i];
            --i;
        }
        return sum;
    }

    double[] showSolution() {
        return this.solution;
    }

    private void switchSigns() {
        int i = 0;
        while (i < this.rows) {
            int j = this.compoundsR.size() - 1;
            while (j < this.cols) {
                double[] arrd = this.m[i];
                int n = j++;
                arrd[n] = arrd[n] * -1.0;
            }
            ++i;
        }
    }

    private boolean removeDecimal(double number) {
        int factor = 2;
        boolean success = false;
        while (number % 1.0 != 0.0) {
            if (number * (double)factor % 1.0 == 0.0) {
                this.multiplySolution(factor);
                success = true;
                break;
            }
            if (++factor <= 20) continue;
            success = false;
            break;
        }
        return success;
    }

    private double round(double number) {
        double value = 0.01;
        if (number % 1.0 < value) {
            number = Math.floor(number);
        } else if (1.0 - number % 1.0 < value) {
            number = Math.ceil(number);
        } else if (Math.abs(number % 1.0) - 0.5 < value) {
            number = Math.floor(number) + 0.5;
        }
        return number;
    }

    private void multiplySolution(int factor) {
        int i = 0;
        while (i < this.solution.length) {
            double[] arrd = this.solution;
            int n = i++;
            arrd[n] = arrd[n] * (double)factor;
        }
    }

    private boolean checkDecimal() {
        boolean noDecimals = true;
        int i = 0;
        while (i < this.solution.length) {
            if (this.solution[i] % 1.0 != 0.0 && !this.removeDecimal(this.solution[i])) {
                this.solution[i] = this.round(this.solution[i]);
                noDecimals = false;
            }
            ++i;
        }
        return noDecimals;
    }

    private void roundSolution() {
        int i = 0;
        while (i < this.solution.length) {
            this.solution[i] = this.round(this.solution[i]);
            ++i;
        }
    }

    String subscriptCompound(String compound) {
        int i = 1;
        while (i < compound.length()) {
            if (compound.charAt(i) > '/' && compound.charAt(i) < ':') {
                String addon = "<SUB>" + compound.charAt(i) + "</SUB>";
                compound = String.valueOf(compound.substring(0, i)) + addon + compound.substring(i + 1);
                i += 10;
            }
            ++i;
        }
        return compound;
    }

    String printEquation(boolean subscript) {
        String finalEq = "";
        int i = 0;
        while (i < this.compoundsR.size()) {
            if (this.solution[i] != 1.0) {
                finalEq = this.solution[i] % 1.0 == 0.0 ? String.valueOf(finalEq) + (int)this.solution[i] + " " : String.valueOf(finalEq) + this.solution[i] + " ";
            }
            finalEq = subscript ? String.valueOf(finalEq) + this.subscriptCompound((String)this.compoundsR.get(i)) + " + " : String.valueOf(finalEq) + (String)this.compoundsR.get(i) + " + ";
            ++i;
        }
        finalEq = finalEq.substring(0, finalEq.length() - 3);
        finalEq = subscript ? String.valueOf(finalEq) + "   \u2192   " : String.valueOf(finalEq) + "  =  ";
        i = this.compoundsR.size();
        while (i < this.compoundsR.size() + this.compoundsP.size()) {
            if (this.solution[i] != 1.0) {
                finalEq = this.solution[i] % 1.0 == 0.0 ? String.valueOf(finalEq) + (int)this.solution[i] + " " : String.valueOf(finalEq) + this.solution[i] + " ";
            }
            finalEq = subscript ? String.valueOf(finalEq) + this.subscriptCompound((String)this.compoundsP.get(i - this.compoundsR.size())) + " + " : String.valueOf(finalEq) + (String)this.compoundsP.get(i - this.compoundsR.size()) + " + ";
            ++i;
        }
        finalEq = finalEq.substring(0, finalEq.length() - 3);
        return finalEq;
    }

    private void totalElements() {
        this.sumR = new int[this.elements.length];
        this.sumP = new int[this.elements.length];
        int i = 0;
        while (i < this.elements.length) {
            int sum = 0;
            int j = 0;
            while (j < ((int[])this.arraysR.get(i)).length) {
                sum = (int)((double)sum + (double)((int[])this.arraysR.get(i))[j] * this.solution[j]);
                ++j;
            }
            this.sumR[i] = sum;
            sum = 0;
            j = 0;
            while (j < ((int[])this.arraysP.get(i)).length) {
                sum = (int)((double)sum + (double)((int[])this.arraysP.get(i))[j] * this.solution[j + ((int[])this.arraysR.get(0)).length]);
                ++j;
            }
            this.sumP[i] = sum;
            ++i;
        }
    }

    boolean isBalanced() {
        boolean balanced = true;
        this.totalElements();
        int i = 0;
        while (i < this.elements.length) {
            if (this.sumR[i] != this.sumP[i]) {
                balanced = false;
            }
            ++i;
        }
        return balanced;
    }
}