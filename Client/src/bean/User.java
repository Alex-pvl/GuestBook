package bean;

import java.io.Serializable;

public class User implements Serializable {

    private int id;
    private final String firstName;
    private final String lastName;
    private final String email;
    private final String login;
    private final String password;
    private final int role;

    public User(int id, String firstname, String lastname,
                String login, String email, String password, int role) {
        this.id = id;
        this.firstName = firstname;
        this.lastName = lastname;
        this.email = email;
        this.login = login;
        this.password = password;
        this.role = role;
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
        return login;
    }

    public String getPassword() {
        return password;
    }

    public int getRole() {
        return role;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
