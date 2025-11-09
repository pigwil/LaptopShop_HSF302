package project.laptopshop.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // ✅ Lấy đường dẫn tuyệt đối tới thư mục uploads/laptop-images trong project
        String uploadPath = System.getProperty("user.dir") + File.separator + "uploads" + File.separator + "laptop-images" + File.separator;

        // ✅ Cho phép Spring Boot phục vụ ảnh qua URL /uploads/laptop-images/...
        registry.addResourceHandler("/uploads/laptop-images/**")
                .addResourceLocations("file:" + uploadPath);
    }
}
