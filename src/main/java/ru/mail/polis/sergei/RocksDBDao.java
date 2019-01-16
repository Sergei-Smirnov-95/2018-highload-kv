package ru.mail.polis.sergei;


import org.jetbrains.annotations.NotNull;
import org.rocksdb.FlushOptions;
import org.rocksdb.RocksDBException;
import ru.mail.polis.KVDao;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.NoSuchElementException;

import org.rocksdb.RocksDB;
import org.rocksdb.Options;

public class RocksDBDao implements KVDao {
    @NotNull
    private final RocksDB db;
    @NotNull
    private final Logger logger;

    public RocksDBDao(@NotNull final File folder_path) throws IOException {

        this.logger = Logger.getLogger(RocksDBDao.class);
        RocksDB.loadLibrary();
        logger.debug("RocksDB library loaded");
        try {
            Options options = new Options();
            options.setCreateIfMissing(true);
            options.setAllowMmapWrites(false);
            options.setAllowMmapReads(true);
            options.setMaxOpenFiles(-1);
            this.db = RocksDB.open(options, folder_path.getAbsolutePath());
        } catch (RocksDBException ex) {

            throw new IOException(ex);
        }

    }

    @NotNull
    @Override
    public byte[] get(@NotNull byte[] key) throws NoSuchElementException {
        final byte[] value = new byte[1024];
        try {

            int err = db.get(key, value);
            if (err == RocksDB.NOT_FOUND) {
                throw new NoSuchElementException();
            }
            if (err == 0) {
                return new byte[0];
            }
            return value;
        } catch (RocksDBException ex) {
            logger.error("RocksDB error in get method" + ex);
            throw new NoSuchElementException();
        }
    }

    @Override
    public void upsert(@NotNull byte[] key, @NotNull byte[] value) throws IOException {
        try {
            db.put(key, value);
        } catch (RocksDBException ex) {
            logger.error("RocksDB error in upsert method" + ex);
            throw new IOException("RocksDB error in upsert method" + ex);
        }

    }

    @Override
    public void remove(@NotNull byte[] key) throws IOException {
        try {
            db.singleDelete(key);
        } catch (RocksDBException ex) {
            logger.error("RocksDB error in remove method" + ex);
            throw new IOException("Rocksdb failed on remove");
        }
    }

    @Override
    public void close() throws IOException {
        try {
            db.flush(new FlushOptions());
        } catch (RocksDBException e) {
            logger.error("RocksDB error in close method" + e);
            throw new IOException("Failed to flush db", e);
        }
        db.close();
        logger.debug("RocksDB was closed");
    }
}
