public class num {public int a;  // 分子
    public int b;  // 分母

    public num() {

    }
    public num(String s) {  // 处理数值
        s = s.trim();

        int a,b;
        int x = s.indexOf("'");
        int y = s.indexOf("/");

        if (x != -1) {  // 代分数处理
            int c = Integer.parseInt(s.substring(0, x));    // 代分数的整数部分
            b = Integer.parseInt(s.substring(y+1));
            a = c * b + Integer.parseInt(s.substring(x+1,y));
        } else if (y != -1) {   // 分数处理
            a = Integer.parseInt(s.substring(0,y));
            b = Integer.parseInt(s.substring(y+1));
        } else {  // 整数处理
            a = Integer.parseInt(s);
            b = 1;
        }
        check(a,b);
    }

    public num(int a, int b) {
        check(a,b);
    }

    public void check(int a, int b) {
        if (a>=0 && b>0) {
            this.a = a;
            this.b = b;
        }
    }

    public num add(num n) { // 加法 a + b
        return new num((this.b*n.a + this.a*n.b), this.b*n.b);
    }

    public num sub(num n) { // 减法 a - b
        return new num((this.a*n.b - this.b*n.a), this.b*n.b);
    }

    public num mul(num n) { // 乘法 a * b
        return new num(this.a*n.a, this.b*n.b);
    }

    public num div(num n) { // 除法 a / b
        return new num(this.a*n.b, this.b*n.a);
    }

    public int gcd(int a, int b) { // 求分数最大公约数
        if (a<b) {
            int x = b % a;
            return x == 0 ? a : gcd(a,x);
        } else if (a>b) {
            int y = a % b;
            return y == 0 ? b : gcd(b,y);
        } else return 1;
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

    public String change(int n, int d) {
        // 将假分数转换为真分数，第一个参数为分子，第二个为分母
        //假分数转真分数方法 求 最大整除数+余数/分母
        num a = easy(new num(n,d));
        int left=a.a/a.b;
        int up=a.a-left*a.b;
        int down=a.b;
        if(left==0) {
            return up+"/"+down;
        }
        else if(down == 1) {
            return Integer.toString(n);
        }else {
            return left+"'"+up+"/"+down;
        }
    }

    public String tostring() {  // 转换为字符串
        if (this.a>this.b) {
            return this.change(a,b);
        } else if (this.b ==1){
            return Integer.toString(this.a);
        } else {
            return this.a + "/" + this.b;
        }
    }
}
