package sejong.smartnotice.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final Path FILE_ROOT = Paths.get("./storage").toAbsolutePath();

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/storage/**")
                .addResourceLocations(FILE_ROOT.toUri().toString());

        CacheControl cacheControl = CacheControl.maxAge((3600 * 24 * 30), TimeUnit.SECONDS);

        registry.addResourceHandler("/resources/**")
                .addResourceLocations("classpath:/static/")
                .setCacheControl(cacheControl);
    }
}
