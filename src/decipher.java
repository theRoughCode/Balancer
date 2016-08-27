import java.util.ArrayList;

public class decipher {
    private String equation;
    protected ArrayList<String> compoundsR = new ArrayList();
    protected ArrayList<String> compoundsP = new ArrayList();
    protected ArrayList<int[]> arraysR = new ArrayList();
    protected ArrayList<int[]> arraysP = new ArrayList();
    String[] elements;

    decipher(String equation) throws Exception {
        this.equation = equation.trim().replaceAll("\\s", "").replace("\u2192", "=");
        if (this.equation.charAt(0) == 'e') {
            throw new Exception();
        }
        this.getCompounds();
        this.getElements();
        this.getNumElements();
    }

    ArrayList<String> debug() {
        int j;
        ArrayList<String> debugArray = new ArrayList<String>();
        debugArray.add("-- Compounds --\r\nReactants");
        int i = 0;
        while (i < this.compoundsR.size()) {
            debugArray.add(String.valueOf(this.compoundsR.get(i)) + " has:");
            j = 0;
            while (j < this.elements.length) {
                debugArray.add("  " + this.elements[j] + ":  " + this.arraysR.get(j)[i]);
                ++j;
            }
            ++i;
        }
        debugArray.add("\r\nProducts");
        i = 0;
        while (i < this.compoundsP.size()) {
            debugArray.add(String.valueOf(this.compoundsP.get(i)) + " has:");
            j = 0;
            while (j < this.elements.length) {
                debugArray.add("\t" + this.elements[j] + ":  " + this.arraysP.get(j)[i]);
                ++j;
            }
            ++i;
        }
        return debugArray;
    }

    String getEquation() {
        return this.equation;
    }

    private void getCompounds() {
        String compound = "";
        boolean reactantsDone = false;
        int i = 0;
        while (i < this.equation.length()) {
            if (this.equation.charAt(i) == '=' || this.equation.charAt(i) == '+') {
                compound = this.removeCoeff(compound);
                if (!reactantsDone) {
                    this.compoundsR.add(compound);
                } else {
                    this.compoundsP.add(compound);
                }
                compound = "";
                if (this.equation.charAt(i) == '=') {
                    reactantsDone = true;
                }
            } else {
                compound = String.valueOf(compound) + this.equation.charAt(i);
            }
            ++i;
        }
        this.compoundsP.add(compound);
    }

    private String removeCoeff(String compound) {
        if (compound.charAt(0) <= '@' || compound.charAt(0) >= '[') {
            return this.removeCoeff(compound.substring(1));
        }
        return compound;
    }

    private void getElements() {
        ArrayList<String> ele = new ArrayList<String>();
        String element = "";
        int i = 0;
        while (i < this.equation.length()) {
            if (this.equation.charAt(i) > '@' && this.equation.charAt(i) < '[') {
                if (!this.checkDuplicates(ele, element) && i > 0) {
                    ele.add(element);
                }
                element = Character.toString(this.equation.charAt(i));
            } else if (this.equation.charAt(i) > '`' && this.equation.charAt(i) < 'z') {
                element = String.valueOf(element) + this.equation.charAt(i);
            }
            ++i;
        }
        if (!this.checkDuplicates(ele, element)) {
            ele.add(element);
        }
        this.elements = ele.toArray(new String[ele.size()]);
    }

    private boolean checkDuplicates(ArrayList<String> ele, String element) {
        return ele.contains(element);
    }

    void outputElements() {
        int i = 0;
        while (i < this.elements.length) {
            System.out.println(this.elements[i]);
            ++i;
        }
    }

    void outputCompounds() {
        System.out.println("Reactants: ");
        int i = 0;
        while (i < this.compoundsR.size()) {
            System.out.println("\t" + this.compoundsR.get(i));
            ++i;
        }
        System.out.println("Products: ");
        i = 0;
        while (i < this.compoundsP.size()) {
            System.out.println("\t" + this.compoundsP.get(i));
            ++i;
        }
    }

    protected double[][] getCoefficients() {
        double[][] coeffArray = new double[this.elements.length][];
        int i = 0;
        while (i < this.elements.length) {
            double[] elementArray = new double[this.compoundsR.size() + this.compoundsP.size()];
            int j = 0;
            while (j < this.compoundsR.size()) {
                elementArray[j] = this.arraysR.get(i)[j];
                ++j;
            }
            j = 0;
            while (j < this.compoundsP.size()) {
                elementArray[j + this.compoundsR.size()] = this.arraysP.get(i)[j] * -1;
                ++j;
            }
            coeffArray[i] = elementArray;
            ++i;
        }
        return coeffArray;
    }

    void output() {
        int j;
        int i = 0;
        while (i < this.compoundsR.size()) {
            System.out.println(String.valueOf(this.compoundsR.get(i)) + " has:");
            j = 0;
            while (j < this.elements.length) {
                System.out.println("\t" + this.elements[j] + ":  " + this.arraysR.get(j)[i]);
                ++j;
            }
            ++i;
        }
        i = 0;
        while (i < this.compoundsP.size()) {
            System.out.println(String.valueOf(this.compoundsP.get(i)) + " has:");
            j = 0;
            while (j < this.elements.length) {
                System.out.println("\t" + this.elements[j] + ":  " + this.arraysP.get(j)[i]);
                ++j;
            }
            ++i;
        }
    }

    private int inBrackets(String compound, String element, int coeff) {
        int begIndex = compound.indexOf(element);
        int openBracket = -1;
        int closeBracket = -1;
        int totalCoeff = coeff;
        boolean inBracket = false;
        int i = 0;
        while (i < compound.length()) {
            if (compound.charAt(i) == '(') {
                openBracket = i;
            } else if (compound.charAt(i) == ')') {
                closeBracket = i;
            }
            ++i;
        }
        if (begIndex > openBracket && begIndex + element.length() - 1 < closeBracket) {
            inBracket = true;
        }
        if (inBracket) {
            totalCoeff *= Integer.parseInt(Character.toString(compound.charAt(closeBracket + 1)));
        }
        return totalCoeff;
    }

    private void getNumElements() {
        int i = 0;
        while (i < this.elements.length) {
            int h;
            String numberStr;
            int no;
            String compound;
            int number;
            int[] noR = new int[this.compoundsR.size()];
            int[] noP = new int[this.compoundsP.size()];
            int j = 0;
            while (j < noR.length) {
                compound = this.compoundsR.get(j);
                numberStr = "";
                number = 0;
                if (compound.contains(this.elements[i])) {
                    h = no = compound.indexOf(this.elements[i]) + this.elements[i].length();
                    while (h < compound.length()) {
                        if (compound.charAt(h) <= '/' || compound.charAt(h) >= ':') break;
                        numberStr = String.valueOf(numberStr) + compound.charAt(h);
                        ++h;
                    }
                    number = numberStr == "" ? 1 : Integer.parseInt(numberStr);
                }
                noR[j] = this.inBrackets(compound, this.elements[i], number);
                ++j;
            }
            this.arraysR.add(noR);
            j = 0;
            while (j < noP.length) {
                compound = this.compoundsP.get(j);
                numberStr = "";
                number = 0;
                if (compound.contains(this.elements[i])) {
                    h = no = compound.indexOf(this.elements[i]) + this.elements[i].length();
                    while (h < compound.length()) {
                        if (compound.charAt(h) <= '/' || compound.charAt(h) >= ':') break;
                        numberStr = String.valueOf(numberStr) + compound.charAt(h);
                        ++h;
                    }
                    number = numberStr == "" ? 1 : Integer.parseInt(numberStr);
                }
                noP[j] = this.inBrackets(compound, this.elements[i], number);
                ++j;
            }
            this.arraysP.add(noP);
            ++i;
        }
    }
}