package com.android.become_a_farmer.user;

public class UserDTO {
    private String email;
    private String password;
    private String name;
    private String nickname;
    private int age;
    private String planningType;
    private String preferredType;

    public UserDTO() {}
    public UserDTO(String email, String password, String name, String nickname) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.nickname = nickname;
        this.age = 0;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getPlanningType() {
        return planningType;
    }

    public void setPlanningType(String planningType) {
        this.planningType = planningType;
    }

    public String getPreferredType() {
        return preferredType;
    }

    public void setPreferredType(String preferredType) {
        this.preferredType = preferredType;
    }
}
