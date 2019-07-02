/**
 * @description:
 * @author: zhegong
 * @create: 2019-06-10 15:05
 **/
public class Dependency {

    private static Dependency dependency = new Dependency(null, null);

    public static Dependency getInstance() {
        return dependency;
    }

    private Dependency(String str1, String str2) {

    }

    public Dependency(String str) {

    }

    public Dependency() {

    }


    public int mockMethod() {
        return privateMethod();
    }

    private int privateMethod() {
        return 1;
    }

    public static String staticMethod(String str) {
        return str;
    }

    public String mockMethod1() {
        return "realMethod";
    }

    public String mockMethod2() {
        return "realMethod";
    }

    public String mockMethod3() {
        return "realMethod";
    }

    public String mockMethod4() {
        return "realMethod";
    }

    public String retMethod(int i, String str) {

        return i + str;
    }

    private final Service service = new Service() {
        public int doSomething() {
            return 2;
        }
    };

    public int businessOperation() {
        return service.doSomething();
    }
}

