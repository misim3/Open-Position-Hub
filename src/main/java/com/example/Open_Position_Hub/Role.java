package com.example.Open_Position_Hub;

import java.util.List;

public enum Role {

    BACKEND(List.of("백엔드", "Backend", "backend", "서버", "Server", "server", "BE", "Back-End", "Back-end")),
    FRONTEND(List.of("프론트엔드", "FrontEnd", "FE", "Front-End", "Front-end")),
    ANDROID(List.of("안드로이드", "Android", "AOS", "App", "APP", "앱", "모바일")),
    IOS(List.of("IOS", "iOS", "App", "APP", "앱", "모바일")),
    DATA(List.of()),
    AI(List.of()),
    DEVOPS(List.of()),
    QA(List.of()),
    BLOCKCHAIN(List.of()),
    ;

    public final List<String> includes;

    Role(List<String> inc) {
        this.includes = inc;
    }
}
