package net.zevrant.services.zevrant.oauth2.service.config;

import org.springframework.core.io.Resource;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;

public class SecretResource implements Resource {

    private final File secretResource;

    public SecretResource(File secretResource) {
        this.secretResource = secretResource;
    }

    @Override
    public boolean exists() {
        return secretResource != null;
    }

    @Override
    public URL getURL() throws IOException {
        throw new UnsupportedOperationException("Secret resource urls are secrets");
    }

    @Override
    public URI getURI() throws IOException {
        throw new UnsupportedOperationException("Secret resource urls are secrets");
    }

    @Override
    public File getFile() throws IOException {
        return secretResource;
    }

    @Override
    public long contentLength() throws IOException {
        return secretResource.getTotalSpace();
    }

    @Override
    public long lastModified() throws IOException {
        throw new UnsupportedOperationException("Grabbed at runtime");
    }

    @Override
    public Resource createRelative(String relativePath) throws IOException {
        return null;
    }

    @Override
    public String getFilename() {
        return secretResource.getName();
    }

    @Override
    public String getDescription() {
        return "Secret retrieved from AWS Secret Store";
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return new FileInputStream(secretResource);
    }
}
