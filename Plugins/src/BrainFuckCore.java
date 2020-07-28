import com.company.IPlugin;
import com.company.Interpreter;

import java.io.IOException;

public class BrainFuckCore implements IPlugin {
    @Override
    public int interpret(char[] arr, int i, Interpreter ip) throws IOException {
        switch(arr[i]){
            case '+':
                ip.getFields()[ip.getPointer()] +=1;
                if(ip.getFields()[ip.getPointer()] > 255){
                    ip.getFields()[ip.getPointer()] = 0;
                }
                break;
            case '-':
                ip.getFields()[ip.getPointer()] -=1;
                if(ip.getFields()[ip.getPointer()] < 0){
                    ip.getFields()[ip.getPointer()] = 255;
                }
                break;
            case '<':
                ip.setPointer(ip.getPointer()-1);
                if(ip.getPointer() < 0){
                    ip.setPointer( ip.getFields().length - 1);
                }
                break;
            case '>':
                ip.setPointer(ip.getPointer()+1);
                if(ip.getPointer() >= ip.getFields().length){
                    ip.setPointer(0);
                }
                break;
            case '[':
                if(ip.getFields()[ip.getPointer()] == 0){
                    i++;
                    for(int cnt = 1;; i++){
                        if(arr[i] == ']'){
                            cnt--;
                        }else if (arr[i] == '['){
                            cnt++;
                        }

                        if(cnt == 0){
                            break;
                        }
                    }
                }else{
                    ip.getOpenedLoops().offerFirst(i);
                }
                break;
            case ']':
                i = ip.getOpenedLoops().pollFirst() - 1;
                break;
            case '.':
                ip.getOut().write(ip.getFields()[ip.getPointer()]);
                ip.getOut().flush();
                break;
            case ',':
                ip.getFields()[ip.getPointer()] = ip.getIn().read();
                if(ip.getFields()[ip.getPointer()] > 255){
                    ip.getFields()[ip.getPointer()] = 255;
                }else if(ip.getFields()[ip.getPointer()] < 0){
                    ip.getFields()[ip.getPointer()] = 0;
                }
                break;
        }
        return i;
    }
}
