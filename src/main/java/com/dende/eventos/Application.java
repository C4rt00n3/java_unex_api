package com.dende.eventos;

import com.dende.eventos.controllers.EventController;
import com.dende.eventos.controllers.UserController;
import com.dende.eventos.repositories.EventRepository;
import com.dende.eventos.repositories.UserRepository;
import com.dende.eventos.services.EventService;
import com.dende.eventos.services.UserService;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import repositories.util.ConfigProperties;
import repositories.util.ConnectionPool;

public class Application {
    public static void main(String[] args) throws IOException {
        UserRepository userRepository = new UserRepository();
        EventRepository eventRepository = new EventRepository();
        UserService userService = new UserService(userRepository);
        EventService eventService = new EventService(eventRepository, userService);

        int port = ConfigProperties.getInt("server.port", 8080);
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/users", new UserController(userService));
        server.createContext("/organizers", new UserController(userService));
        server.createContext("/organizers/", new EventController(eventService));
        server.createContext("/events", new EventController(eventService));
        server.setExecutor(null);

        Runtime.getRuntime().addShutdownHook(new Thread(ConnectionPool::close));
        server.start();
        System.out.println("Dedê Eventos API em http://localhost:" + port);
    }
}
