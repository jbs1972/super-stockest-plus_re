package dto;

import java.util.ArrayList;

public class OperatorOperand {
    
    private ArrayList<String> operatorList;
    private ArrayList<Float> operandList;

    public ArrayList<String> getOperatorList() {
        return operatorList;
    }

    public void setOperatorList(ArrayList<String> operatorList) {
        this.operatorList = operatorList;
    }

    public ArrayList<Float> getOperandList() {
        return operandList;
    }

    public void setOperandList(ArrayList<Float> operandList) {
        this.operandList = operandList;
    }

}
