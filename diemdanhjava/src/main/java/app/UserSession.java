// src/main/java/app/UserSession.java
package app;

public class UserSession {

    private static String email;
    private static String fullName;

    public static void set(String email, String fullName) {
        UserSession.email = email;
        UserSession.fullName = fullName;
    }

    public static String getEmail() {
        return email;
    }

    public static String getFullName() {
        return fullName;
    }

    public static void clear() {
        email = null;
        fullName = null;
    }
}
