package org.yasukusury.onlinedocument;

import com.spring4all.swagger.EnableSwagger2Doc;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.MultipartAutoConfiguration;

/**
 * @author 30254
 */
//@EnableSwagger2Doc
@SpringBootApplication//(exclude = {MultipartAutoConfiguration.class})
public class OnlineDocumentApplication {

    public static void main(String[] args) {
        SpringApplication.run(OnlineDocumentApplication.class, args);
    }
}
