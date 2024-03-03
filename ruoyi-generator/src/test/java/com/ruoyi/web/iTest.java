package com.ruoyi.web;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.junit.jupiter.api.Test;

import javax.script.ScriptException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @Author
 * @Date 2023/12/12 9:18
 * @Description
 */
public class iTest {

    //监管平台上报，屏蔽科室（“护理门诊-1260300”、“药学门诊-3240100”）
    private static final List<String> TREATDEPTCODES = Arrays.asList("1260300","3240100");

    @Test
    public void test() {
        System.out.println("123");
        System.out.println("123");
        for (int i = 0; i < 5; i++) {
            try {
                am();
                System.out.println("123");
            }catch (Exception e){
                System.out.println("-------->"+e);
            }
        }

    }

    public void am(){
        String commonDeptCode = "3240100";
        if(TREATDEPTCODES.contains(commonDeptCode)){
            System.out.println("-------");
            throw new AccessRuntimeException("2", "停止上传【" + commonDeptCode + "】科室，已屏蔽");
        }
    }

    @Data
    @AllArgsConstructor
    class Bean{
        String id;
        String name;
    }

    @Test
    public void test2(){
        List<Bean> docList = new ArrayList<>();
        m1(docList);
        m2(docList);

        System.out.println(docList);
    }
    //门诊医生数据
    private void m1(List<Bean> docList) {
        docList.add(new Bean("1","zs"));
        docList.add(new Bean("2","ls"));
    }
    //olt医生数据
    private void m2(List<Bean> docList) {
        docList.add(new Bean("3","w5"));
        docList.add(new Bean("2","ls"));
    }


    @Test
    public void testt(){
        for (int i = 0; i < 10; i++) {
            try{
                ssss();
            }catch (Exception e){
                System.out.println("-------->"+e);
            }
            System.out.println("123123123123");
        }
    }

    private void ssss() throws ScriptException {
        throw new ScriptException("1");
    }
}
