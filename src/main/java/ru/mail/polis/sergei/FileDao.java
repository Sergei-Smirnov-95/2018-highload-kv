package ru.mail.polis.sergei;

import org.jetbrains.annotations.NotNull;
import ru.mail.polis.KVDao;

import javax.xml.bind.DatatypeConverter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.NoSuchElementException;

public class FileDao  implements KVDao {
    @NotNull
    private final File base;

    public FileDao(@NotNull File base) {
        this.base = base;
    }

    @NotNull
    private File from(@NotNull final byte[] key){

        return new File(base, DatatypeConverter.printHexBinary(key));
    }

    @NotNull
    @Override
    public byte[] get(@NotNull byte[] key) throws NoSuchElementException, IOException {
        final File file = from(key);
        if (file.exists()){
            return Files.readAllBytes(file.toPath());
        }else{
            throw new NoSuchElementException();
        }
    }

    @Override
    public void upsert(@NotNull byte[] key, @NotNull byte[] value) throws IOException {
        Files.write(from(key).toPath(),value);
    }

    @Override
    public void remove(@NotNull byte[] key){
        from(key).delete();
    }

    @Override
    public void close() {
        //Nothing
    }
}
