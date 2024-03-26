import org.junit.jupiter.api.Test;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
public class arithmetic {
    static List<List<String>> e = new ArrayList<List<String>>();   // 用于存放题目
    static List<Stack<Node>> e1 = new ArrayList<Stack<Node>>();
    static List<String> a = new ArrayList<String>();   // 用于按题号顺序存放答案
    static String[] opr = {"+", "-", "x", "÷"};
    static List<String> exp = new ArrayList<String>();	// 表达式
    static int  num = 10;    // 题目数量，默认为10道
    static int range = 0;   // 数值范围
    static String error = null;    // 错误信息
    static String efile = null;   // 给定的题目文件：Exercise.txt
    static String afile = null;   // 给定的答案文件：Answer.txt
    static String gfile = "src" + File.separator;   // 结果统计文件：Grade.txt

    public static void main(String[] args) {
        // 根据功能处理参数
        for (int i=0; i<args.length; i++) {
            if (args[i].equals("-r")) {
                range = Integer.parseInt(args[i+1]);
            } else if (args[i].equals("-n"))
            {
                num = Integer.parseInt(args[i+1]);
            } else if (args[i].equals("-h")) {  // 帮助信息
                System.out.println("-r 参数: 控制题目中数值（自然数、真分数和真分数分母）的范围 \n例如: Myapp.exe -r 10 将生成10以内(不包括10)的四则运算题目");
                System.out.println("-n 参数: 控制生成题目的个数 \n例如: Myapp.exe -n 10  将生成10个题目");
                System.out.println("支持对给定的题目文件和答案文件，判定答案中的对错并进行数量统计 具体参数如下:\nMyapp.exe -e <exercisefile>.txt -a <answerfile>.txt");
            } else if (args[i].equals("-e")) {
                efile = "src" + File.separator + args[i+1];
                range = 10; // 防止报错
            } else if (args[i].equals("-a")) {
                afile = "src" + File.separator + args[i+1];
                range = 10;
            }
        }
        if ((efile == null && afile != null) || (efile != null && afile == null)) {
            error = "请给定完整参数，题目文件和答案文件均需给出！";
        }
        if (able(num, range) && efile == null && afile == null) {
            for (int i=0; i<num; i++) {
                subject();
                String an = count(e.get(i));
                a.add(an);
            }
            save(e);
            answer(a);
        } else if (efile != null && afile != null){
            check(new File(efile), new File(afile), new File(gfile));
        } else {
            System.out.println(error);
        }
    }

    // 检查输入的题目数量与数值范围是否可实现,并修改
    static boolean able(int n, int r) {
        if (n>10000) {
            error = "最大题数为10000";
            return false;
        } else if (n<=0) {
            error = "题数不合法";
            return false;
        } else if (range == 0) {
            error = "请给定数值范围，详细操作通过 -h 查看";
            return false;
        } else if (range<0) {
            error = "数值范围不合法";
            return false;
        } else if (range<5) {
            error = "数值范围过小，至少为5";
            return false;
        }
        return true;
    }

    // 生成表达式，并存入题目列表
    static void subject() {
        // 运算符个数 1-3 个
        List<String> exp = new ArrayList<String>();
        int opr_n = (int) (Math.random()*3 + 1);
        switch(opr_n) {
            case 1:
                exp.add(createnum());
                exp.add(createopr1());
                exp.add(createnum());
                break;
            case 2:
                // 括号起始位置
                int bkt_s = (int) (Math.random()*3);
                // 括号结束位置
                int bkt_e = 0;
                // 无括号
                if (bkt_s == 0) {
                    bkt_e = 0;
                } else {
                    bkt_e = bkt_s + 1;
                }
                for (int i = 1; i <= 3; i++) {
                    if (bkt_s == i) {
                        exp.add("(");
                    }
                    exp.add(createnum());
                    if (bkt_e == i) {
                        exp.add(")");
                    }
                    exp.add(createopr1());
                }
                // 处理括号无意义情况
                checkbkt(bkt_s, bkt_e,exp);
                exp.remove(exp.size()-1);	// 删除最后多加入的一个运算符
                break;
            case 3:
                // 括号起始位置
                bkt_s = (int) (Math.random()*4);
                // 无括号
                if (bkt_s == 0) {
                    bkt_e = 0;
                } else if (bkt_s == 3){
                    bkt_e = 4;
                } else {
                    bkt_e = bkt_s + (int) (Math.random()*2 + 1);    // [1,3)
                }
                for (int i = 1; i <= 4; i++) {
                    if (bkt_s == i) {
                        exp.add("(");
                    }
                    exp.add(createnum());
                    if (bkt_e == i) {
                        exp.add(")");
                    }
                    exp.add(createopr1());
                }
                // 处理括号无意义情况
                checkbkt(bkt_s, bkt_e,exp);
                exp.remove(exp.size()-1);	// 删除最后多加入的一个运算符
                break;
        }
        e.add(exp);
        System.out.println(toString(exp));
        //  exp.clear();
    }

    // 产生随机数
    public static String createnum() {
        ThreadLocalRandom rand = ThreadLocalRandom.current();
        int a = rand.nextInt(range);
        int b = rand.nextInt(range);
        if (a == 0) {
            a += (int) (Math.random()*range + 1);
        }
        if (b == 0) {
            b += (int) (Math.random()*range + 1);
        }
        if (b == range) {
            b = b-1;
        }
        num n = new num(a,b);
        easy(n);
        if (n.a == n.b) {
            return Integer.toString(1);
        }
        if (n.b == 1) {
            return Integer.toString(n.a);
        }
        return n.tostring();
    }

    public static num easy(num n) {    // 约分
        if (n.a == 0) {
            return new num(0, 1);
        } else {
            int m = n.gcd(n.a, n.b);
            if (m == 1) {
                return n;
            } else  {
                n.a = n.a/m;
                n.b = n.b/m;
                return n;
            }
        }
    }

    // 随机产生运算符
    public static String createopr1() {
        return opr[(int) (Math.random()*4)];
    }

    // 只产生 x 或 ÷
    public static String createopr2() {
        return opr[(int) (Math.random()*2 + 2)];
    }

    // 只产生 + 或 -
    public static String createopr3() {
        return opr[(int) (Math.random()*2)];
    }

    // 处理括号无意义情况
    public static void checkbkt(int bkt_s, int bkt_e,List<String>exp) {
        String f;
        String s;
        String t;
        if (bkt_e - bkt_s == 1) {
            if (bkt_s == 1) {
                f = exp.get(2);
                s = exp.get(5);
                if (!(f.equals("+") || (f.equals("-"))) && !(s.equals("x") || s.equals("÷"))) {
                    exp.set(2, createopr3());
                    exp.set(5, createopr2());
                }
            } else if (bkt_s == 2) {
                f = exp.get(1);
                s = exp.get(4);
                if (exp.size()<8) {
                    if ((f.equals("+") || (f.equals("-"))) && (s.equals("x") || (s.equals("÷")))) {	// 排除 a + ( b x c ) 此类括号无意义的情况
                        exp.set(1, createopr2());
                    }
                }
            } else if (bkt_s == 3) {
                f = exp.get(3);
                s = exp.get(6);
                if ((f.equals("+") || (f.equals("-"))) && (s.equals("x") || s.equals("÷"))) {
                    exp.set(3, createopr2());
                } else if (((f.equals("+") || f.equals("-"))) && (s.equals("+") || s.equals("-"))) {
                    exp.set(3, createopr2());
                }
            }
        } else {
            if (bkt_s == 1) {
                f = exp.get(2);
                s = exp.get(4);
                t = exp.get(7);
                if (!(t.equals("x") || t.equals("÷"))) {
                    exp.set(7, createopr2());
                    if (f.equals(s) && (f.equals("+") || f.equals("÷"))) {
                        exp.set(4, createopr2());
                    }
                }
            } else if (bkt_s == 2) {
                f = exp.get(1);
                s = exp.get(4);
                t = exp.get(6);
                if ((f.equals("+") || f.equals("-")) && (t.equals("x") || t.equals("÷"))) {
                    exp.set(1, createopr2());
                }
            }
        }
    }

    // 转换为字符串
    static String toString(Stack s) {
        Iterator<Object> i = s.iterator();
        String st = "";
        while (i.hasNext()) {
            st += i.next().toString();
        }
        return st;
    }

    // 转换为字符串
    static String toString(List<String> list) {
        String s = "";
        for (int i=0; i<list.size(); i++) {
            s += (list.get(i).toString() + " ");
        }
        return s;
    }

    // 构建树计算表达式，并存入题目列表
    public static String count(List<String> list) {
        Stack<Node> a1 = new Stack<>();    // 数值栈
        Stack<String> b = new Stack<>();    // 未处理的运算符栈
//        Stack<Node> c = new Stack<>();    // 处理后的运算符栈
        for (int i=0; i<list.size(); i++) {
            // 当前指针为数值
            String string = list.get(i);
            if (!isop(string)) {
                a1.push(new Node(string, null, null, null));
            } else {
                //比较符号栈中的顶层符号如果需要出栈
                while (!b.isEmpty() && !(string.equals("(") || (prefer(string)==2 && prefer(b.peek())==1) ||
                        (!string.equals(")") && b.peek().equals("(")))) {
                    String symbol = b.pop();

                    if (symbol.equals("(") && string.equals(")")) {
                        break;
                    }
                    push(symbol, a1);

                }
                //如果符号不是")"就进栈
                if (!string.equals(")")) {
                    b.push(string);
                }
            }
        }

        while (!b.isEmpty()) {
            push(b.pop(), a1);
        }
        negative(a1);
//        a.add(a1.peek().result);
        return a1.peek().result;
    }

    // 处理负数
    public static void negative(Stack<Node> c) {
        if (!c.isEmpty()) {
            for (int i=0; i<c.size(); i++) {
                Node n = c.get(i);
                if (n.op.equals("-")) {
                    num l = new num(n.left.result);
                    num r = new num(n.right.result);
                    if (l.a*r.b < r.a*l.b) {
                        Node m = n.left;
                        n.left = n.right;
                        n.right = m;
                        n.setResult();
                    }
                }
            }
        }
    }

    public static void push(String op, Stack<Node> a) {
        if (!op.equals("(")) {
            Node r = a.pop();
            Node l = a.pop();
            Node o = new Node(null, r, l, op);
            o.result = count(r.result, l.result, op);
            a.push(o);
        }
    }

    public static String count(String r, String l, String op) {
        num left = new num(l);
        num right = new num(r);
        switch (op) {
            case "+":
                return left.add(right).tostring();
            case "-":
                return left.sub(right).tostring();
            case "x":
                return left.mul(right).tostring();
            case "÷":
                return left.div(right).tostring();
        }
        return null;
    }

    static class Node {
        String result;
        Node right;
        Node left;
        String op;

        public Node(String result, Node right, Node left, String op) {
            this.result = result;
            this.right = right;
            this.left = left;
            this.op = op;
        }

        public void setResult() {
            if (op!=null) {
                result = count(right.result, left.result, op);
            }
        }

        public void changelr() {
            Node m = left;
            right = left;
            left = m;
        }

    }

    public static int prefer(String op) {  // 判断运算符优先级
        if (op.equals("-") || op.equals("+")) {
            return 1;
        } else if (op.equals("x") || op.equals("÷")) {
            return 2;
        } else {
            return 3;
        }
    }

    public static boolean isop(String op) { // 判断是否为运算符
        if (op.equals("+") || op.equals("-")
                || op.equals("x") || op.equals("÷")
                || op.equals("(") || op.equals(")")) {
            return true;
        } else  {
            return false;
        }
    }

    // 检查题目列表中是否有相同的题目，并处理
    static void ifExist(ArrayList<String> s) {
        for (int i=0; i<s.size(); i++) {
            for (int j=i+1; j<s.size();) {
                Stack<Node> f = e1.get(i);
                Stack<Node> se = e1.get(j);
                if (f.size() == se.size() && equals(f.pop(), se.pop())) {
                    s.remove(j);
                } else {
                    j++;
                }
            }
        }
    }

    static boolean equals(Node f, Node s) {

        if (fullequals(f, s)) {
            return true;
        } else if (f.op.equals(s.op) && f.result.equals(s.result)){
            if (change(f, s)) {
                return true;
            } else {
                return equals(f.left, s.left) && equals(f.right, s.right);
            }
        }
        return false;
    }

    static boolean change(Node f, Node s) {
        if (f.op.equals("+") || f.op.equals("x")) {
            f.changelr();
            f.setResult();
            if (nodeequal(f.left, s.left) && nodeequal(f.right, s.right)) {
                return true;
            }
        }
        return false;
    }

    static boolean nodeequal(Node f, Node s) {
        return f.op.equals(s.left.op) && f.result.equals(s.left.result);
    }

    static boolean fullequals(Node f, Node s) {
        if (f.op.equals(s.op) && f.result.equals(s.result)) {
            return fullequals(f.left, s.left) && fullequals(f.right, s.right);
        }
        return false;
    }

    static void check(File e, File a, File g) {
        // 用于对给定的题目文件和答案文件判断答案文件中的对错并统计
        File g1 = new File("src/Grade.txt");
        if (!g1.exists()){
            try {
                g1.createNewFile();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        try (BufferedReader exReader = new BufferedReader(new FileReader(e));
             BufferedReader anReader = new BufferedReader(new FileReader(a));
             BufferedWriter gradeWriter = new BufferedWriter(new FileWriter(g1))
        ) {
            String ex, an;
            int c = 0, w = 0;
            StringBuilder correct = new StringBuilder("Correct: %d (");
            StringBuilder wrong = new StringBuilder("Wrong: %d (");
            while ((ex = exReader.readLine()) != null && (an = anReader.readLine()) != null) {
                int exPoint = ex.indexOf(".");
                int anPoint = an.indexOf(".");
                if (exPoint != -1 && anPoint != -1) {
                    int i = Integer.valueOf(ex.substring(0,exPoint).trim());
                    String expression = ex.substring(exPoint + 2);

                    String[] exp = expression.split(" ");
                    List<String> al = new ArrayList<String>();
                    al = Arrays.asList(exp);
                    String realanswer = count(al);
                    String answer = an.substring(anPoint + 2);
                    if (realanswer.equals(answer.toString())) {
                        c++;
                        correct.append(" ").append(i);
                        if (c % 20 == 0) {
                            correct.append("\n");
                        }
                    } else {
                        w++;
                        wrong.append(" ").append(i);
                        if (w % 20 == 0) {
                            wrong.append("\n");
                        }
                    }
                }
            }
            gradeWriter.write(String.format(correct.append(" )\n").toString(),c));
            gradeWriter.write(String.format(wrong.append(" )\n").toString(),w));
            gradeWriter.flush();
        } catch (IOException e1) {
            e1.printStackTrace();
        }

    }

    static void save(List<List<String>> s) {
        // 用于将题目存入当前目录下的Exercises.txt文件
        File question = new File("src/Exercises.txt");
        if (!question.exists()) {
            System.out.println("文件不存在，创建文件: Exercises.txt" );
            try {
                question.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("文件已存在，文件为: Exercises.txt" );
        }
        FileWriter fw;
        try {
            fw=new FileWriter(question);
            BufferedWriter bw = new BufferedWriter(fw);
            for(int i=0;i<s.size();i++) {
                String temp="";
                for(int j=0;j<s.get(i).size();j++) {
                    temp+=s.get(i).get(j)+" ";
                }
                String t = (i + 1) + ". " + temp;
                bw.write(t);
                bw.newLine();
            }
            bw.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    static void answer(List<String> a) {
        // 用于将题目答案存入当前目录下的Answer.txt文件
        File answer = new File("src/Answer.txt");
        if (!answer.exists()) {
            System.out.println("文件不存在，创建文件: Answer.txt" );
            try {
                answer.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("文件已存在，文件为: Answer.txt" );
        }
        FileWriter fw;
        try {
            fw = new FileWriter(answer);
            BufferedWriter bw =new BufferedWriter(fw);
            for(int i=0;i<a.size();i++){
                String t =i+1+". "+ a.get(i);
                bw.write(t);
                bw.newLine();
                bw.flush();
            }
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}
