package dev.triomph.kies.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app")
public class AppProperties {
    
    private final Jwt jwt = new Jwt();
    private final File file = new File();
    
    public static class Jwt {
        private String secret;
        private int expirationMs;
        
        public String getSecret() {
            return secret;
        }
        
        public void setSecret(String secret) {
            this.secret = secret;
        }
        
        public int getExpirationMs() {
            return expirationMs;
        }
        
        public void setExpirationMs(int expirationMs) {
            this.expirationMs = expirationMs;
        }
    }
    
    public static class File {
        private String uploadDir;
        
        public String getUploadDir() {
            return uploadDir;
        }
        
        public void setUploadDir(String uploadDir) {
            this.uploadDir = uploadDir;
        }
    }
    
    public Jwt getJwt() {
        return jwt;
    }
    
    public File getFile() {
        return file;
    }
}
