package com.example.Open_Position_Hub;

import java.util.List;

public enum Role {

    BACKEND(List.of("백엔드", "Backend", "backend", "서버", "Server", "server", "BE", "Back-End", "Back-end", "Full Stack", "FullStack", "Full stack", "Full-stack", "Full-Stack", "풀스택")),
    FRONTEND(List.of("프론트엔드", "FrontEnd", "FE", "Front-End", "Front-end", "Full Stack", "FullStack", "Full stack", "Full-stack", "Full-Stack", "풀스택")),
    ANDROID(List.of("안드로이드", "Android", "AOS", "App", "APP", "앱", "모바일")),
    IOS(List.of("IOS", "iOS", "App", "APP", "앱", "모바일")),
    DATA(List.of("DATA", "Data", "데이터")),
    AI(List.of("AI", "ML", "머신러닝", "LLM", "Machine Learning", "딥러닝", "Deep Learning")),
    DEVOPS(List.of("DEVOPS", "DevOps", "Devops", "devops", "데브옵스", "SRE", "INFRA", "Infra", "System")),
    QA(List.of("QA", "Qa", "테스트", "Test", "Quality")),
    BLOCKCHAIN(List.of("볼록체인", "BLOCKCHAIN", "BlockChain", "디파이", "DApp", "코인", "Web3", "컨트랙트", "메인넷")),
    GAME(List.of("GAME", "게임", "클라이언트", "Unity")),
    ;

    public final List<String> includes;

    Role(List<String> inc) {
        this.includes = inc;
    }
}
