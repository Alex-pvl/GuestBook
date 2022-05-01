package client.bean;

public class Message  {
    private int id;
    private String email;
    private String message;
    private String date;

    public Message(int id, String date, String email, String message) {
        this.id = id;
        this.date = date;
        this.email = email;
        this.message = message;
    }

    public int getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getMessage() {
        return message;
    }

    public String getDate() {
        return date;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
