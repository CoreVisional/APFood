package com.apu.apfood.db.models;

import com.apu.apfood.db.models.common.BaseModel;

/**
 *
 * @author Alex
 */
public class User extends BaseModel {

    private String name;
    private String email;
    private char[] password;
    private String role;

    public User(int userId, String name, String email, char[] password, String role) {
        setId(userId);
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public User(String name, String email, char[] password, String role) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    @Override
    public String toString() {
        return "Id: " + getId() + "\n" + "Name: " + name + "\n" + "Email: " + email + "\n" + "Password: " + new String(password) + "\n" + "Role: " + role + "\n";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public char[] getPassword() {
        return password;
    }

    public void setPassword(char[] password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
