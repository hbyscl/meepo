package org.cheng.meepo.taskadmin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

/**
 * Created by Administrator on 2017/9/21.
 */
@SpringBootApplication
@ServletComponentScan
public class RunMain {
    public static void main(String[] args) {
        SpringApplication.run(RunMain.class);
    }
}
