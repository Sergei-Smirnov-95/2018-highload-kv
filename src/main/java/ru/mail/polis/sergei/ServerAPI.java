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
        session.sendResponse(handleEntity(request,id));

    }
    public Response handleEntity(Request request, String id){
        if(id == null || id.isEmpty()){
            return new Response(Response.BAD_REQUEST, Response.EMPTY);
        }
        switch (request.getMethod()){
            case Request.METHOD_GET:
                return getEntity(id);
            case Request.METHOD_PUT:
                return putEntity(id,request);
            case Request.METHOD_DELETE:
                return deleteEntity(id);
            default:
                return new Response(Response.METHOD_NOT_ALLOWED, Response.EMPTY);
        }
    }
    private Response getEntity(String id){
        try {
            final byte[] value = dao.get(id.getBytes());
            return Response.ok(value);
        } catch (NoSuchElementException e) {
            return new Response(Response.NOT_FOUND, Response.EMPTY);
        }
        catch (Exception e){
            return new Response(Response.INTERNAL_ERROR, e.toString().getBytes());
        }
    }
    private Response putEntity(String id,Request request){
        try {
            dao.upsert(id.getBytes(),request.getBody());
            return new Response(Response.CREATED,Response.EMPTY);
        }catch (Exception e){
            return new Response(Response.INTERNAL_ERROR, e.toString().getBytes());
        }
    }
    private Response deleteEntity(String id){
        try {
            dao.remove(id.getBytes());
            return new Response(Response.ACCEPTED,Response.EMPTY);
        }catch (Exception e){
            return new Response(Response.INTERNAL_ERROR, e.toString().getBytes());
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