package org.superbiz.moviefun.blobstore;

import com.google.common.io.ByteStreams;
import org.apache.tika.Tika;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Optional;

public class FileStore implements BlobStore {

    private final File root;

    public FileStore(File root) {
        this.root = root;
        this.root.mkdirs();
    }

    @Override
    public void put(Blob blob) throws IOException {
        File file = new File(root, blob.name);

        ByteStreams.copy(blob.inputStream, new FileOutputStream(file));
    }

    @Override
    public Optional<Blob> get(String name) throws IOException {
        File file = new File(root, name);
        if (file.exists()) {
            return Optional.of(new Blob(name, new FileInputStream(file), null));
        } else {
            return Optional.empty();
        }
    }

    @Override
    public void deleteAll() {
        for (File f : root.listFiles()) {
            f.delete();
        }
    }
}