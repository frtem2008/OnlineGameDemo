package Control;//класс для работы с клавиатурой (обработка нескольких нажатий одновременно)

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Keyboard implements KeyListener {
    //все клавиши
    private static final boolean[] keys = new boolean[66568];

    //переменные под каждую из них
    private static boolean a;
    private static boolean b;
    private static boolean c;
    private static boolean d;
    private static boolean e;
    private static boolean f;
    private static boolean g;
    private static boolean h;
    private static boolean i;
    private static boolean j;
    private static boolean k;
    private static boolean l;
    private static boolean m;
    private static boolean n;
    private static boolean o;
    private static boolean p;
    private static boolean q;
    private static boolean r;
    private static boolean s;
    private static boolean t;
    private static boolean u;
    private static boolean v;
    private static boolean w;
    private static boolean x;
    private static boolean y;
    private static boolean z;

    private static boolean up;
    private static boolean down;
    private static boolean left;
    private static boolean right;

    private static boolean one;
    private static boolean two;
    private static boolean three;
    private static boolean four;
    private static boolean five;
    private static boolean six;
    private static boolean seven;
    private static boolean eight;
    private static boolean nine;
    private static boolean zero;

    private static boolean tab;
    private static boolean shift;
    private static boolean ctrl;
    private static boolean esc;
    private static boolean win;
    private static boolean alt;

    private static boolean space;

    private static boolean scLock;
    private static boolean capsLock;
    private static boolean numLock;

    private static boolean pauseBreak;
    private static boolean printScr;
    private static boolean insert;
    private static boolean home;
    private static boolean end;
    private static boolean pgUp;
    private static boolean pgDown;
    private static boolean backSpace;
    private static boolean del;
    private static boolean plus;
    private static boolean minus;
    private static boolean star;
    private static boolean bSlash;
    private static boolean equals;
    private static boolean dot;
    private static boolean comma;
    private static boolean f1;
    private static boolean f2;
    private static boolean f3;
    private static boolean f4;
    private static boolean f5;
    private static boolean f6;
    private static boolean f7;
    private static boolean f8;
    private static boolean f9;
    private static boolean f10;
    private static boolean f11;
    private static boolean f12;

    public Keyboard() {

    }

    //получение инфы о клавишах

    public static boolean getBackSpace() {
        return backSpace;
    }

    public static boolean getEsc() {
        return esc;
    }

    public static boolean getPrintScr() {
        return printScr;
    }

    public static boolean getScLock() {
        return scLock;
    }

    public static boolean getPauseBreak() {
        return pauseBreak;
    }

    public static boolean getInsert() {
        return insert;
    }

    public static boolean getHome() {
        return home;
    }

    public static boolean getEnd() {
        return end;
    }

    public static boolean getPgUp() {
        return pgUp;
    }

    public static boolean getPgDown() {
        return pgDown;
    }

    public static boolean getDel() {
        return del;
    }

    public static boolean getNumLock() {
        return numLock;
    }

    public static boolean getbSlash() {
        return bSlash;
    }

    public static boolean getPlus() {
        return plus;
    }

    public static boolean getMinus() {
        return minus;
    }

    public static boolean getStar() {
        return star;
    }

    public static boolean getEquals() {
        return equals;
    }

    public static boolean getCapsLock() {
        return capsLock;
    }

    public static boolean getWin() {
        return win;
    }

    public static boolean getAlt() {
        return alt;
    }

    public static boolean getDot() {
        return dot;
    }

    public static boolean getComma() {
        return comma;
    }

    public static boolean getF1() {
        return f1;
    }

    public static boolean getF2() {
        return f2;
    }

    public static boolean getF3() {
        return f3;
    }

    public static boolean getF4() {
        return f4;
    }

    public static boolean getF5() {
        return f5;
    }

    public static boolean getF6() {
        return f6;
    }

    public static boolean getF7() {
        return f7;
    }

    public static boolean getF8() {
        return f8;
    }

    public static boolean getF9() {
        return f9;
    }

    public static boolean getF10() {
        return f10;
    }

    public static boolean getF11() {
        return f11;
    }

    public static boolean getF12() {
        return f12;
    }

    public static boolean getTab() {
        return tab;
    }

    public static boolean getShift() {
        return shift;
    }

    public static boolean getCtrl() {
        return ctrl;
    }

    public static boolean getSpace() {
        return space;
    }

    public static boolean getUp() {
        return up;
    }

    public static boolean getDown() {
        return down;
    }

    public static boolean getLeft() {
        return left;
    }

    public static boolean getRight() {
        return right;
    }

    public static boolean getOne() {
        return one;
    }

    public static boolean getTwo() {
        return two;
    }

    public static boolean getThree() {
        return three;
    }

    public static boolean getFour() {
        return four;
    }

    public static boolean getFive() {
        return five;
    }

    public static boolean getSix() {
        return six;
    }

    public static boolean getSeven() {
        return seven;
    }

    public static boolean getEight() {
        return eight;
    }

    public static boolean getNine() {
        return nine;
    }

    public static boolean getZero() {
        return zero;
    }

    public static boolean getA() {
        return a;
    }

    public static boolean getB() {
        return b;
    }

    public static boolean getC() {
        return c;
    }

    public static boolean getD() {
        return d;
    }

    public static boolean getE() {
        return e;
    }

    public static boolean getF() {
        return f;
    }

    public static boolean getG() {
        return g;
    }

    public static boolean getH() {
        return h;
    }

    public static boolean getI() {
        return i;
    }

    public static boolean getJ() {
        return j;
    }

    public static boolean getK() {
        return k;
    }

    public static boolean getL() {
        return l;
    }

    public static boolean getM() {
        return m;
    }

    public static boolean getN() {
        return n;
    }

    public static boolean getO() {
        return o;
    }

    public static boolean getP() {
        return p;
    }

    public static boolean getQ() {
        return q;
    }

    public static boolean getR() {
        return r;
    }

    public static boolean getS() {
        return s;
    }

    public static boolean getT() {
        return t;
    }

    public static boolean getU() {
        return u;
    }

    public static boolean getV() {
        return v;
    }

    public static boolean getW() {
        return w;
    }

    public static boolean getX() {
        return x;
    }

    public static boolean getY() {
        return y;
    }

    public static boolean getZ() {
        return z;
    }

    //обновление клавиатуры
    public void update() {
        a = keys[KeyEvent.VK_A];
        b = keys[KeyEvent.VK_B];
        c = keys[KeyEvent.VK_C];
        d = keys[KeyEvent.VK_D];
        e = keys[KeyEvent.VK_E];
        f = keys[KeyEvent.VK_F];
        g = keys[KeyEvent.VK_G];
        h = keys[KeyEvent.VK_H];
        i = keys[KeyEvent.VK_I];
        j = keys[KeyEvent.VK_J];
        k = keys[KeyEvent.VK_K];
        l = keys[KeyEvent.VK_L];
        m = keys[KeyEvent.VK_M];
        n = keys[KeyEvent.VK_N];
        o = keys[KeyEvent.VK_O];
        p = keys[KeyEvent.VK_P];
        q = keys[KeyEvent.VK_Q];
        r = keys[KeyEvent.VK_R];
        s = keys[KeyEvent.VK_S];
        t = keys[KeyEvent.VK_T];
        u = keys[KeyEvent.VK_U];
        v = keys[KeyEvent.VK_V];
        w = keys[KeyEvent.VK_W];
        x = keys[KeyEvent.VK_X];
        y = keys[KeyEvent.VK_Y];
        z = keys[KeyEvent.VK_Z];

        up = keys[KeyEvent.VK_UP];
        down = keys[KeyEvent.VK_DOWN];
        left = keys[KeyEvent.VK_LEFT];
        right = keys[KeyEvent.VK_RIGHT];

        one = keys[KeyEvent.VK_1];
        two = keys[KeyEvent.VK_2];
        three = keys[KeyEvent.VK_3];
        four = keys[KeyEvent.VK_4];
        five = keys[KeyEvent.VK_5];
        six = keys[KeyEvent.VK_6];
        seven = keys[KeyEvent.VK_7];
        eight = keys[KeyEvent.VK_8];
        nine = keys[KeyEvent.VK_9];
        zero = keys[KeyEvent.VK_0];

        esc = keys[KeyEvent.VK_ESCAPE];
        tab = keys[KeyEvent.VK_TAB];
        shift = keys[KeyEvent.VK_SHIFT];
        ctrl = keys[KeyEvent.VK_CONTROL];
        win = keys[KeyEvent.VK_WINDOWS];
        alt = keys[KeyEvent.VK_ALT];
        space = keys[KeyEvent.VK_SPACE];

        printScr = keys[KeyEvent.VK_PRINTSCREEN];
        insert = keys[KeyEvent.VK_INSERT];
        pauseBreak = keys[KeyEvent.VK_PAUSE];

        numLock = keys[KeyEvent.VK_NUM_LOCK];
        capsLock = keys[KeyEvent.VK_CAPS_LOCK];
        scLock = keys[KeyEvent.VK_SCROLL_LOCK];

        home = keys[KeyEvent.VK_HOME];
        end = keys[KeyEvent.VK_END];
        pgUp = keys[KeyEvent.VK_PAGE_UP];
        pgDown = keys[KeyEvent.VK_PAGE_DOWN];

        backSpace = keys[KeyEvent.VK_BACK_SPACE];
        del = keys[KeyEvent.VK_DELETE];

        plus = keys[KeyEvent.VK_PLUS];
        minus = keys[KeyEvent.VK_MINUS];
        star = keys[KeyEvent.VK_ASTERISK];
        bSlash = keys[KeyEvent.VK_BACK_SLASH];
        equals = keys[KeyEvent.VK_EQUALS];


        dot = keys[KeyEvent.VK_PERIOD];
        comma = keys[KeyEvent.VK_COMMA];

        f1 = keys[KeyEvent.VK_F1];
        f2 = keys[KeyEvent.VK_F2];
        f3 = keys[KeyEvent.VK_F3];
        f4 = keys[KeyEvent.VK_F4];
        f5 = keys[KeyEvent.VK_F5];
        f6 = keys[KeyEvent.VK_F6];
        f7 = keys[KeyEvent.VK_F7];
        f8 = keys[KeyEvent.VK_F8];
        f9 = keys[KeyEvent.VK_F9];
        f10 = keys[KeyEvent.VK_F10];
        f11 = keys[KeyEvent.VK_F11];
        f12 = keys[KeyEvent.VK_F12];
    }

    public void keyPressed(KeyEvent event) {
        keys[event.getKeyCode()] = true;
    }

    public void keyReleased(KeyEvent event) {
        keys[event.getKeyCode()] = false;
    }

    public void keyTyped(KeyEvent event) {
    }
}
