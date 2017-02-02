package com.wots.lutmaar.GetterSetter;

/**
 * Created by Super Star on 10-06-2015.
 */
public class SignUpGetterSetter {
    String name;
    String mail;

    public SignUpGetterSetter(String name, String mail) {
        this.name = name;
        this.mail = mail;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }
}
