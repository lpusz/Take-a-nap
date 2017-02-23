package com.wordpress.antycode.nappster;

import java.util.Random;

class RandomEquationFactory {
    private static RandomEquationFactory instance = null;
    private Random random = new Random();
    private int operation;

    private RandomEquationFactory() {

    }

    static RandomEquationFactory getInstance() {
        if (instance == null) {
            instance = new RandomEquationFactory();
        }
        return instance;
    }

    int[] getRandomEquationData() {
        int x = random.nextInt(50) - 25;
        int y = random.nextInt(50) - 25;
        operation = random.nextInt(3);

        int result;
        switch (operation) {
            case 0:
                result = x + y;
                break;
            case 1:
                result = x - y;
                break;
            case 2:
                result = x * y;
                break;
            default:
                result = x + y;
                break;
        }
        return new int[]{x, y, result};
    }

    private String getOperationSymbol() {
        String operationSymbol;
        switch (operation) {
            case 1:
                operationSymbol = "-";
                break;
            case 2:
                operationSymbol = "\u00B7";
                break;
            default:
                operationSymbol = "+";
                break;
        }
        return operationSymbol;
    }

    String getFormattedEquationString(int[] equationData) {
        StringBuilder builder = new StringBuilder(Integer.toString(equationData[0]));
        builder.append(" ").append(this.getOperationSymbol()).append(" ");
        if (equationData[1] < 0) {
            builder.append("(").append(Integer.toString(equationData[1])).append(")");
        } else {
            builder.append(Integer.toString(equationData[1]));
        }
        return builder.toString();
    }

    int getRandomNumber(int root) {
        boolean operation = random.nextBoolean();
        if (operation)
            return random.nextInt(50) - (root / 2);
        else
            return random.nextInt(50) + (root / 2);
    }

    int getNormalRandom(int integer) {
        return random.nextInt(integer);
    }
}
