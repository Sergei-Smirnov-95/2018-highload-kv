package ru.mail.polis.sergei;


import org.jetbrains.annotations.NotNull;
import org.rocksdb.RocksDBException;
import ru.mail.polis.KVDao;

import java.io.File;
import java.io.IOException;
import java.util.NoSuchElementException;

import org.rocksdb.RocksDB;
import org.rocksdb.Options;

public class RocksDBDao implements KVDao {
    //@NotNull
    private RocksDB db;

    public RocksDBDao(@NotNull final File folder_path){

    //load the RocksDB C++ library.
     RocksDB.loadLibrary();

    // the Options class contains a set of configurable DB options
    // that determines the behaviour of the database.
    try (final Options options = new Options().setCreateIfMissing(true)) {

        // a factory method that returns a RocksDB instance
        try (final RocksDB db = RocksDB.open(options, folder_path.getPath())) {
            this.db= db;
            /*db.put("key".getBytes(),"value".getBytes());
            byte[] val=db.get("key".getBytes());
            System.out.println(new String(val));*/
        }
    } catch (RocksDBException e) {
        System.out.println("error db"+e);
    }

    }
    @NotNull
    @Override
    public byte[] get(@NotNull byte[] key) throws NoSuchElementException{
        final byte[] value =new byte[10000];
        try {
            int err = db.get(key,value);
            if(err == RocksDB.NOT_FOUND)
            {
                throw new NoSuchElementException();
            }
            return value;
        } catch (RocksDBException ex){
            System.out.println("error with RocksDB"+ex);
            throw new NoSuchElementException();
        }
    }

    @Override
    public void upsert(@NotNull byte[] key, @NotNull byte[] value) throws IOException {
        try {
            db.put(key, value);
        }
        catch (RocksDBException ex)
        {
            System.out.println("error rocksdb"+ex);
        }

    }

    @Override
    public void remove(@NotNull byte[] key) throws IOException {
    try{
        db.delete(key);//remove(key);
    }
        catch (RocksDBException ex)
    {
        throw  new IOException("Rocksdb failed");
    }
    }

    @Override
    public void close() throws IOException {
        db.close();
    }
}
