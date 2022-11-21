package design_mode.prototype.exercises1;

import design_mode.prototype.exercises1.framework.Product;

/**
 * @author yujh
 * @date 2022/11/12 16:55
 */
public class MessageBox extends Product {
    private char decochar;
    public MessageBox(char decochar) {
        this.decochar = decochar;
    }
    @Override
    public void use(String s) {
        int length = s.getBytes().length;
        for (int i = 0; i < length + 4; i++) {
            System.out.print(decochar);
        }
        System.out.println("");
        System.out.println(decochar + " " + s + " " + decochar);
        for (int i = 0; i < length + 4; i++) {
            System.out.print(decochar);
        }
        System.out.println("");
    }

}
