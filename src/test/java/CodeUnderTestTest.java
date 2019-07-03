import mockit.*;
import mockit.integration.junit4.JMockit;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@RunWith(JMockit.class)
public class CodeUnderTestTest {
//    @Mocked
//    Dependency dependency;

    //Junit 的测试框架
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    /**
     * 部分mock   如果在Expectation中没有进行record，则会调用原有代码
     */

    @Test
    public void partiallyMockingASingleInstance() {
        final Dependency dependency = new Dependency();

        new Expectations(dependency) {{
            dependency.mockMethod1();
            result = "123";

            // 静态方法也可以
            Dependency.staticMethod(anyString);
            result = "mock static";
        }};

        // Mocked:
        assertEquals("123", dependency.mockMethod1());
        assertEquals("mock static", Dependency.staticMethod("test"));

        // Not mocked:
        assertEquals("realMethod", dependency.mockMethod2());
    }

    /**
     * 放在参数里面， 全部mock， 如果没有record的方法返回  null或者原始类型的初始值
     */
    @Test
    public void allMethodsMock(@Mocked final Dependency dependency) throws Exception {
        //常规准备代码

        //record
        new NonStrictExpectations() {{
            dependency.mockMethod();
            result = 2;
        }};

        //replay 调用测试代码
        CodeUnderTest codeUnderTest = new CodeUnderTest();
        assertEquals(2, codeUnderTest.testMethod());

        //verify
        new Verifications() {{
            dependency.mockMethod();
            times = 1;
            //times，minTimes，maxTimes
        }};
    }

    /**
     * Mock 私有方法或变量
     */
    @Test
    public void privateMethodAndField() {

        final CodeUnderTest codeUnderTest = new CodeUnderTest();

        new Expectations(codeUnderTest) {
            {
                Deencapsulation.invoke(codeUnderTest, "fetchDataFromDB", "Unmi");
                result = "http://luffy.com";

                Deencapsulation.setField(codeUnderTest, "url", "http://luffy.com.url");
            }
        };

        String actual = codeUnderTest.fetchData("Unmi");
        Assert.assertEquals("http://luffy.com", actual);

        Assert.assertEquals("http://luffy.com.url", codeUnderTest.getUrl());
    }

    @Test/*(expected = IllegalArgumentException.class)*/
    public void throwException(@Mocked final Dependency dependency) throws Exception {
        //常规准备代码

        //record
        new NonStrictExpectations() {{
            dependency.mockMethod();
            result = new IllegalArgumentException("test info for junit **");
        }};

        //replay 调用测试代码
        CodeUnderTest codeUnderTest = new CodeUnderTest();
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("info for junit");
        codeUnderTest.testMethod();
    }


    /**
     * 按顺序返回一组值
     */
    @Test
    public void returnDiffValuesInOrder(@Mocked final Dependency dependency) throws Exception {
        new NonStrictExpectations() {{
            dependency.mockMethod();
            returns(1, 2, 3);
        }};

        Dependency dependency1 = new Dependency();
        assertEquals(1, dependency1.mockMethod());

        Dependency dependency2 = new Dependency();
        assertEquals(2, dependency2.mockMethod());

        Dependency dependency3 = new Dependency();
        assertEquals(3, dependency3.mockMethod());
    }

    /**
     * 根据参数返回不同值
     */
    @Test
    public void returnDiffValueWithPara(@Mocked final Dependency mock) {
        new Expectations() {{
            mock.retMethod(anyInt, null);
            result = new Delegate() {
                String delegateMethod(int i, String s) {
                    return i == 0 ? "-1" : s;
                }
            };
        }};

        // Calls to "intReturningMethod(int, String)" will execute the delegate method above.
        assertEquals("-1", new Dependency().retMethod(0, "123"));
        assertEquals("123", new Dependency().retMethod(10, "123"));
    }

    /**
     * 只mock其中某个对象，其它的并不mock
     */
    @Test
    public void mockOnlyOneObject(@Injectable final Dependency mockDependency) {
        new NonStrictExpectations() {{
            mockDependency.mockMethod1();
            result = "mockMethod";
        }};
        assertEquals("mockMethod", mockDependency.mockMethod1());
        assertEquals("realMethod", new Dependency().mockMethod1());
    }

    @Test
    public void checkInvokeOrder(@Mocked final Dependency mock) {
        mock.mockMethod1();
        mock.mockMethod2();
        mock.mockMethod3();
        mock.mockMethod4();

        new VerificationsInOrder() {{
            mock.mockMethod1();
            unverifiedInvocations();
            mock.mockMethod4();
        }};

        new Verifications() {{
            mock.mockMethod3();
            mock.mockMethod2();
        }};
    }

    /**
     * mock 构造函数  三种方式
     */
    @Test
    public void mockConstructor(@Mocked Dependency mockDependency1, @Mocked Dependency mockDependency2) {

        new NonStrictExpectations() {{
            //1
            Dependency dependency1 = new Dependency("method 1");
            dependency1.mockMethod();
            result = 1;

            //2
            new Dependency("method 2");
            result = mockDependency1;

            mockDependency1.mockMethod();
            result = 2;

            //3
            Dependency.getInstance();
            result = mockDependency2;

            mockDependency2.mockMethod();
            result = 3;
        }};


        assertEquals(1, new Dependency("method 1").mockMethod());
        assertEquals(2, new Dependency("method 2").mockMethod());
        assertEquals(3, Dependency.getInstance().mockMethod());
    }

    /**
     * 任何的基本类型都有对应的anyXyz，anyString对应任意字符串。
     * any对应任意的对象，在使用时需要进行显式类型转换: (CastClass) any
     * <p>
     * any的限制太宽松，with可以选择特定的子集
     * <p>
     * null可以与任何对象匹配，好处是避免类型转换，但是需要有一个any或者with。
     */
    @Test
    public void fillParameter(@Mocked Dependency mockDependency) {
        new StrictExpectations() {{


            new Dependency(anyString);
            result = mockDependency;

            mockDependency.mockMethod();
            result = 2;
        }};

        assertEquals(2, new Dependency("any para").mockMethod());
    }


    /**
     * 使用withCapture()捕获最后一次调用的参数
     */
    @Test
    public void capturingArgumentsFromLastInvocation(@Mocked final Dependency mock) {
        new Dependency().staticMethod("test");

        new Verifications() {{
            String s;
            mock.staticMethod(s = withCapture());

            assertEquals("test", s);
        }};
    }

    /**
     * 使用withCapture(List)捕获所有调用的参数。
     */
    @Test
    public void capturingArgumentsFromMultipleInvocations(@Mocked final Dependency mock) {
        new Dependency().staticMethod("test");
        new Dependency().staticMethod("test1");

        new Verifications() {{
            List<String> dataObjects = new ArrayList<>();
            mock.staticMethod(withCapture(dataObjects));

            assertEquals(2, dataObjects.size());
            assertEquals("test", dataObjects.get(0));
            assertEquals("test1", dataObjects.get(1));
        }};
    }

    /**
     * 使用withCapture(new XX())
     */
    @Test
    public void capturingNewInstances(@Mocked Dependency mock) {
        new Dependency("Paul");
        new Dependency("Mary");
        Dependency dependency = new Dependency("Joe");

        new Verifications() {{
            List<Dependency> dependencyInstantiated = withCapture(new Dependency(anyString));

            assertEquals(dependencyInstantiated.size(), 3);

            assertEquals(dependencyInstantiated.get(2), dependency);

        }};
    }

    /**
     * @Capturing标注基类/接口，所有实现类会被mock
     */
    @Capturing
    Service anyService;

    @Test
    public void mockingImplementationClassesFromAGivenBaseType() {
        new Expectations() {{
            anyService.doSomething();
            returns(3);
        }};

        int result = new Dependency().businessOperation();
        assertEquals(3, result);
    }

    /**
     * 忽略静态代码块
     */
    @Test
    public void ignoreStaticBlock(){
        new MockUp<Dependency>() {
            @Mock
            void $clinit(){}
        };

        Dependency dependency = Dependency.getInstance();
        assertNull(dependency);
    }
}