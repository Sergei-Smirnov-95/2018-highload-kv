package ru.mail.polis.sergei;

import one.nio.http.HttpServer;
import one.nio.http.HttpServerConfig;
import one.nio.server.AcceptorConfig;
import org.apache.log4j.Logger;
import ru.mail.polis.KVDao;
import ru.mail.polis.KVService;

import java.io.IOException;
import java.util.Set;

public class MyKVService implements KVService {

    private static final Logger logger = Logger.getLogger(MyKVService.class);


    private HttpServerConfig config;
    private ServerAPI server;
    private KVDao dao;
    private Set<String> topology;
    private int port;

    private MyKVService() {
    }

    public static KVService create(int port, KVDao dao, Set<String> topology) {
        HttpServerConfig config = createConfig(port);
        MyKVService kvService = new MyKVService();
        kvService.config = config;
        kvService.dao = dao;
        kvService.topology = topology;
        kvService.port = port;
        logger.debug("server created");
        return kvService;
    }

    public void start() {
        try {
            server = new ServerAPI(port, dao, topology, config);
            server.start();
            logger.debug("server started");
        } catch (IOException e) {
            logger.error(e);
            throw new RuntimeException(e);
        }
    }

    public void stop() {
        if (server != null) {
            server.stop();
            logger.debug("server stopped");
            return;
        }
        logger.debug("server not existed");
    }

    private static HttpServerConfig createConfig(int port) {
        HttpServerConfig config = new HttpServerConfig();
        AcceptorConfig acceptorConfig = new AcceptorConfig();
        acceptorConfig.port = port;
        config.acceptors = new AcceptorConfig[]{acceptorConfig};
        return config;
    }
}