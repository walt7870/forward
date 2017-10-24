package com.ntech.model;

public class LibraryKey {
    private String userName;

    private String libraryName;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName == null ? null : userName.trim();
    }

    public String getLibraryName() {
        return libraryName;
    }

    public void setLibraryName(String libraryName) {
        this.libraryName = libraryName == null ? null : libraryName.trim();
    }
}