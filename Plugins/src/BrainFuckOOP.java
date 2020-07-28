import com.company.IPlugin;
import com.company.Interpreter;

public class BrainFuckOOP implements IPlugin {
    @Override
    public int interpret(char[] arr, int i, Interpreter ip) throws Exception {
        boolean returnValue = false;
        switch(arr[i]){
            case '/':
                i++;
                StringBuilder numberTmp = new StringBuilder();
                while(arr[i] != '\\'){
                    numberTmp.append(arr[i]);
                    i++;
                }
                int methodId = Integer.parseInt(numberTmp.toString());
                ip.interpret(ip.getBfo().getMethods()[methodId].toCharArray(),ip.getBfo());
                break;
            case '&':
                returnValue = true;
                i++;
            case '{':
                numberTmp = new StringBuilder();
                i++;
                while(arr[i] != '}'){
                    numberTmp.append(arr[i]);
                    i++;
                }
                int objIndex;
                if(numberTmp.toString().contains("*")){
                    String tmp = numberTmp.toString().replace("*","");
                    objIndex = ip.getFields()[Integer.parseInt(tmp)];
                }else {
                    objIndex = Integer.parseInt(numberTmp.toString());
                }
                i++;
                numberTmp = new StringBuilder();
                i++;
                while(i < arr.length && arr[i] != '}' ){
                    numberTmp.append(arr[i]);
                    i++;
                }
                ++i;
                if(i >= arr.length){i = arr.length-1;}
                if(arr[i] == '('){
                    i++;
                    int[] tmp = ip.getBfo().getObject(objIndex).getVariables();
                    int a = 0;
                    while(arr[i] != ')'){

                        StringBuilder varNumberTmp = new StringBuilder();
                        while(arr[i] != '|' && arr[i] != ')'){
                            varNumberTmp.append(arr[i]);
                            i++;
                        }

                        if(!varNumberTmp.toString().isEmpty()) {
                            tmp[a] = ip.getFields()[Integer.parseInt(varNumberTmp.toString())];
                            a++;
                        }
                        if(arr[i] == '|'){
                            i++;
                        }
                    }
                    i++;
                }
                if(!numberTmp.toString().isEmpty()) {
                    int methodIndex;
                    if(numberTmp.toString().contains("*")){
                        String tmp = numberTmp.toString().replace("*","");
                        methodIndex = ip.getFields()[Integer.parseInt(tmp)];
                    }else {
                        methodIndex = Integer.parseInt(numberTmp.toString());
                    }
                    Interpreter interpreter = new Interpreter(ip.getIn(), ip.getOut(), ip.getFields().length - 1, ip.getObjectDefinitions(),ip.getPlugins());
                    interpreter.interpret(ip.getBfo().getObject(objIndex).getMethods()[methodIndex].toCharArray(), ip.getBfo().getObject(objIndex));
                    if (returnValue) {
                        ip.getFields()[ip.getPointer()] = ip.getBfo().getObject(objIndex).getVariables()[0];
                    }
                    i--;
                }
                break;
            case '@':
                ip.setPointer(0);
                break;
            case '$':
                ip.setFields(new int[ip.getFields().length-1]);
                break;
            case '?':
                i++;
                if(arr[i] == '{'){
                    numberTmp = new StringBuilder();
                    i++;
                    while(arr[i] != '}'){
                        numberTmp.append(arr[i]);
                        i++;
                    }
                    int number = Integer.parseInt(numberTmp.toString());
                    ip.getBfo().addObject(ip.createObject(ip.getObjectDefinitions()[number]));
                }
                break;
        }
        return i;
    }
}
