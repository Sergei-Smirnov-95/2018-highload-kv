package ru.mail.polis.sergei;

import one.nio.http.*;
import one.nio.server.AcceptorConfig;
import ru.mail.polis.KVDao;
import ru.mail.polis.KVService;

import java.io.IOException;
import java.util.NoSuchElementException;

public class ServerAPI extends HttpServer implements KVService {
    private final KVDao dao;
    public ServerAPI(final int port, final KVDao dao) throws IOException {
        super(from(port));
        this.dao =  dao;
    }

    @Path("/v0/status")
    public Response status() {
        return Response.ok("Ok");
    }

    @Override
    public  void  handleDefault(final Request request, final HttpSession session) throws IOException{
        if(!request.getPath().equals("/v0/entity")){
            session.sendResponse(new Response(Response.BAD_REQUEST, Response.EMPTY));
            return;
        }
        final String id = request.getParameter("id=");
        if(id == null || id.isEmpty()){
            session.sendResponse(new Response(Response.BAD_REQUEST, Response.EMPTY));
            return;
        }
        switch (request.getMethod()){
            case Request.METHOD_GET:
                try {
                    final byte[] value = dao.get(id.getBytes());
                    session.sendResponse(Response.ok(value));
                } catch (NoSuchElementException e) {
                    session.sendResponse(new Response(Response.NOT_FOUND, Response.EMPTY));
                }
                catch (Exception e){
                    session.sendResponse(new Response(Response.INTERNAL_ERROR, e.toString().getBytes()));
                }
                break;

            case Request.METHOD_PUT:
                try {
                    dao.upsert(id.getBytes(),request.getBody());
                    session.sendResponse(new Response(Response.CREATED,Response.EMPTY));
                }catch (Exception e){
                    session.sendResponse(new Response(Response.INTERNAL_ERROR, e.toString().getBytes()));
                }
                break;
            case Request.METHOD_DELETE:
                try {
                    dao.remove(id.getBytes());
                    session.sendResponse(new Response(Response.ACCEPTED,Response.EMPTY));
                }catch (Exception e){
                    session.sendResponse(new Response(Response.INTERNAL_ERROR, e.toString().getBytes()));
                }
                break;
            default:
                    session.sendResponse(new Response(Response.METHOD_NOT_ALLOWED, Response.EMPTY));
        }
    }

    private static HttpServerConfig from(final int port) {
        final AcceptorConfig ac = new AcceptorConfig();
        ac.port = port;

        HttpServerConfig config = new HttpServerConfig();
        config.acceptors = new AcceptorConfig[]{ac};
        return config;
    }

}
/*важно делать атомарные операции, чтобы работало параллельно*/