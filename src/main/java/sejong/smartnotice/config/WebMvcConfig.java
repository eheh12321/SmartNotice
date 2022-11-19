package sejong.smartnotice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

@EnableAsync
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Value("${resources.location}")
    private String resourceLocation;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        Path path = Paths.get(resourceLocation).toAbsolutePath();

        registry.addResourceHandler("/storage/**")
                .addResourceLocations(path.toUri().toString());

        CacheControl cacheControl = CacheControl.maxAge((3600 * 24 * 30), TimeUnit.SECONDS);

        registry.addResourceHandler("/resources/**")
                .addResourceLocations("classpath:/static/")
                .setCacheControl(cacheControl);
    }
}
