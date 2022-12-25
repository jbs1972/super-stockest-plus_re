package utilities;

import dto.OperatorOperand;
import java.awt.List;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class SimplificationSpliter {
    
    public static void main(String args[]) {
        simplificationSpliter("1000/1.5/1.28/1.02");
    }
    
    public static OperatorOperand simplificationSpliter(String myString) {
        ArrayList<String> operatorList = new ArrayList<String>();
        ArrayList<Float> operandList = new ArrayList<Float>();
        
        StringTokenizer st = new StringTokenizer(myString, "+-*/", true);
        
        while (st.hasMoreTokens()) {
           String token = st.nextToken();

           if ("+-/*".contains(token)) {
              operatorList.add(token);
           } else {
              operandList.add(Float.parseFloat(token));
           }
        }

        System.out.println("Operators:" + operatorList);
        System.out.println("Operands:" + operandList);

        OperatorOperand oo = new OperatorOperand();
        oo.setOperatorList(operatorList);
        oo.setOperandList(operandList);
        
        return oo;
    }
    
}
