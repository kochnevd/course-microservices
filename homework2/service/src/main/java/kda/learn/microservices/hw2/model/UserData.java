package kda.learn.microservices.hw2.model;

public class UserData {
    private Long id;
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;

    public Long getId() {
        return id;
    }

    public UserData setId(Long id) {
        this.id = id;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public UserData setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getFirstName() {
        return firstName;
    }

    public UserData setFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public String getLastName() {
        return lastName;
    }

    public UserData setLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public UserData setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getPhone() {
        return phone;
    }

    public UserData setPhone(String phone) {
        this.phone = phone;
        return this;
    }
}
