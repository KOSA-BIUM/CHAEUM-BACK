package com.bium.chaeum.domain.model.entity;

import com.bium.chaeum.domain.model.vo.UserId;
import lombok.Getter;

@Getter
public class User {

    private UserId id;
    private String email;
    private String password;
    private String name;

    private User(UserId id, String email, String password, String name) {
        if (id == null) throw new IllegalArgumentException("id is required");
        if (email == null) throw new IllegalArgumentException("email is required");
        if (password == null) throw new IllegalArgumentException("password is required");
        if (name == null) throw new IllegalArgumentException("name is required");

        this.id = id;
        this.email = email;
        this.password = password;
        this.name = name;
    }

    public static User create(String email, String password, String name){
        return new User(UserId.newId(), email, password, name);
    }

    // 인프라 복원용(레코드 → 도메인).
    public static User reconstruct(UserId id, String email, String password,
                                   String name) {

        return new User(id, email, password, name);
    }
}
