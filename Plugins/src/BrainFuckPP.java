import com.company.IPlugin;
import com.company.Interpreter;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class BrainFuckPP implements IPlugin {
    @Override
    public int interpret(char[] arr, int i, Interpreter ip) {
        switch(arr[i]){
            case '#':
            case ';':
            case ':':
            case '%':
            case '^':
            case '!':
                throw new NotImplementedException();
        }
        return i;
    }
}
