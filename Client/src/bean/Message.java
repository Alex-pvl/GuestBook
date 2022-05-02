package bean;

public class Message {
    private int id;
    private final String date;
    private final String email;
    private final String message;

    public Message(int id, String date, String email, String message) {
        this.id = id;
        this.date = date;
        this.email = email;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public String getEmail() {
        return email;
    }

    public String getDate() {
        return date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
