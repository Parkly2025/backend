package pw.react.backend.utils;

public class Utils {
    public static Boolean roleAdminOrUser(String userRole){
        if (userRole == null)
            return false;
        return userRole.equalsIgnoreCase("admin") || userRole.equalsIgnoreCase("user");
    }

    public static Boolean roleUser(String userRole){
        if (userRole == null)
            return false;
        return userRole.equalsIgnoreCase("user");
    }

    public static Boolean roleAdmin(String userRole){
        if (userRole == null)
            return false;
        return userRole.equalsIgnoreCase("admin");
    }
}
