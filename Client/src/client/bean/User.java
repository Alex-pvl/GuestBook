package client.bean;

import java.io.Serializable;

public class User implements Serializable {
    private int id;
    private String firstName;
    private String lastName;
    private String email;
    private String userName;
    private String password;
    private int role_id;

    public User(int id, String firstname, String lastname,
                String login, String email, String password, int role_id) {
        this.id = id;
        this.firstName = firstname;
        this.lastName = lastname;
        this.email = email;
        this.userName = login;
        this.password = password;
        this.role_id = role_id;
    }

    public User(User user){
        this(user.getId(), user.getFirstName(), user.getLastName(),
                user.getLogin(), user.getEmail(), user.getPassword(), user.getId());
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getLogin() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public int getRole() {
        return role_id;
    }

    public int getId() {
        return id;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setUserName(String login) {
        this.userName = login;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRole(int role_id) {
        this.role_id = role_id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
