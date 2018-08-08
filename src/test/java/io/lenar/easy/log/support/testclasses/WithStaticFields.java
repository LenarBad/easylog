package io.lenar.easy.log.support.testclasses;

public class WithStaticFields {

    public static String staticField = "PUBLIC STATIC FIELD VALUE";

    private String name;

    private String password;

    public WithStaticFields() {
        this.name = "TestName";
        this.password = "TestPassword";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
