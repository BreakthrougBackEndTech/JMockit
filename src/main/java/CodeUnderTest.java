/**
 * @description:
 * @author: zhegong
 * @create: 2019-06-10 15:05
 **/
public class CodeUnderTest {
    public int testMethod() {
        Dependency dependency = new Dependency();
        return dependency.mockMethod();
    }

    public String fetchData(String name) {
        System.out.println("call MyService.fetchData");
        return fetchDataFromDB(name);
    }

    private String fetchDataFromDB(String name) {
        throw new RuntimeException("Not implemented yet!");

    }

    private String url;

    public String getUrl() {
        return url;
    }

}

