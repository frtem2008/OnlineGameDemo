package UserInput;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.Serializable;

public class Keyboard implements KeyListener, Serializable {
    public boolean active;

    public boolean[] keys = new boolean[66568];
    //все клавиши
    //переменные под каждую из них
    private boolean a;
    private boolean b;
    private boolean c;
    private boolean d;
    private boolean e;
    private boolean f;
    private boolean g;
    private boolean h;
    private boolean i;
    private boolean j;
    private boolean k;
    private boolean l;
    private boolean m;
    private boolean n;
    private boolean o;
    private boolean p;
    private boolean q;
    private boolean r;
    private boolean s;
    private boolean t;
    private boolean u;
    private boolean v;
    private boolean w;
    private boolean x;
    private boolean y;
    private boolean z;

    private boolean up;
    private boolean down;
    private boolean left;
    private boolean right;

    private boolean one;
    private boolean two;
    private boolean three;
    private boolean four;
    private boolean five;
    private boolean six;
    private boolean seven;
    private boolean eight;
    private boolean nine;
    private boolean zero;

    private boolean tab;
    private boolean shift;
    private boolean ctrl;
    private boolean esc;
    private boolean win;
    private boolean alt;

    private boolean space;

    private boolean scLock;
    private boolean capsLock;
    private boolean numLock;

    private boolean pauseBreak;
    private boolean printScr;
    private boolean insert;
    private boolean home;
    private boolean end;
    private boolean pgUp;
    private boolean pgDown;
    private boolean backSpace;
    private boolean del;
    private boolean plus;
    private boolean minus;
    private boolean star;
    private boolean bSlash;
    private boolean equals;
    private boolean dot;
    private boolean comma;
    private boolean f1;
    private boolean f2;
    private boolean f3;
    private boolean f4;
    private boolean f5;
    private boolean f6;
    private boolean f7;
    private boolean f8;
    private boolean f9;
    private boolean f10;
    private boolean f11;
    private boolean f12;

    public Keyboard() {

    }

    public Keyboard(boolean[] keys) {
        this.keys = keys;
    }

    //получение инфы о клавишах

    public boolean getBackSpace() {
        return backSpace;
    }

    public boolean getEsc() {
        return esc;
    }

    public boolean getPrintScr() {
        return printScr;
    }

    public boolean getScLock() {
        return scLock;
    }

    public boolean getPauseBreak() {
        return pauseBreak;
    }

    public boolean getInsert() {
        return insert;
    }

    public boolean getHome() {
        return home;
    }

    public boolean getEnd() {
        return end;
    }

    public boolean getPgUp() {
        return pgUp;
    }

    public boolean getPgDown() {
        return pgDown;
    }

    public boolean getDel() {
        return del;
    }

    public boolean getNumLock() {
        return numLock;
    }

    public boolean getbSlash() {
        return bSlash;
    }

    public boolean getPlus() {
        return plus;
    }

    public boolean getMinus() {
        return minus;
    }

    public boolean getStar() {
        return star;
    }

    public boolean getEquals() {
        return equals;
    }

    public boolean getCapsLock() {
        return capsLock;
    }

    public boolean getWin() {
        return win;
    }

    public boolean getAlt() {
        return alt;
    }

    public boolean getDot() {
        return dot;
    }

    public boolean getComma() {
        return comma;
    }

    public boolean getF1() {
        return f1;
    }

    public boolean getF2() {
        return f2;
    }

    public boolean getF3() {
        return f3;
    }

    public boolean getF4() {
        return f4;
    }

    public boolean getF5() {
        return f5;
    }

    public boolean getF6() {
        return f6;
    }

    public boolean getF7() {
        return f7;
    }

    public boolean getF8() {
        return f8;
    }

    public boolean getF9() {
        return f9;
    }

    public boolean getF10() {
        return f10;
    }

    public boolean getF11() {
        return f11;
    }

    public boolean getF12() {
        return f12;
    }

    public boolean getTab() {
        return tab;
    }

    public boolean getShift() {
        return shift;
    }

    public boolean getCtrl() {
        return ctrl;
    }

    public boolean getSpace() {
        return space;
    }

    public boolean getUp() {
        return up;
    }

    public boolean getDown() {
        return down;
    }

    public boolean getLeft() {
        return left;
    }

    public boolean getRight() {
        return right;
    }

    public boolean getOne() {
        return one;
    }

    public boolean getTwo() {
        return two;
    }

    public boolean getThree() {
        return three;
    }

    public boolean getFour() {
        return four;
    }

    public boolean getFive() {
        return five;
    }

    public boolean getSix() {
        return six;
    }

    public boolean getSeven() {
        return seven;
    }

    public boolean getEight() {
        return eight;
    }

    public boolean getNine() {
        return nine;
    }

    public boolean getZero() {
        return zero;
    }

    public boolean getA() {
        return a;
    }

    public boolean getB() {
        return b;
    }

    public boolean getC() {
        return c;
    }

    public boolean getD() {
        return d;
    }

    public boolean getE() {
        return e;
    }

    public boolean getF() {
        return f;
    }

    public boolean getG() {
        return g;
    }

    public boolean getH() {
        return h;
    }

    public boolean getI() {
        return i;
    }

    public boolean getJ() {
        return j;
    }

    public boolean getK() {
        return k;
    }

    public boolean getL() {
        return l;
    }

    public boolean getM() {
        return m;
    }

    public boolean getN() {
        return n;
    }

    public boolean getO() {
        return o;
    }

    public boolean getP() {
        return p;
    }

    public boolean getQ() {
        return q;
    }

    public boolean getR() {
        return r;
    }

    public boolean getS() {
        return s;
    }

    public boolean getT() {
        return t;
    }

    public boolean getU() {
        return u;
    }

    public boolean getV() {
        return v;
    }

    public boolean getW() {
        return w;
    }

    public boolean getX() {
        return x;
    }

    public boolean getY() {
        return y;
    }

    public boolean getZ() {
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
        if (!active) {
            active = true;
            //System.out.println("On");
        }
        keys[event.getKeyCode()] = true;
    }

    public void keyReleased(KeyEvent event) {
        if (active) {
            active = false;
            //System.out.println("Off");
        }
        keys[event.getKeyCode()] = false;
    }

    public void keyTyped(KeyEvent event) {
    }
}
