package com.ntech.service.inf;

import com.ntech.model.LibraryKey;

import java.util.List;

public interface ILibraryService {
    int insert(LibraryKey libraryKey);
    int create(LibraryKey libraryKey);
    int delete(LibraryKey libraryKey);
    int modify(LibraryKey libraryKey);
    List<LibraryKey> findAll();
    List<String> findByUserName(String userName);
    List<LibraryKey> findLKByUserName(String userName);
    List<LibraryKey> findByToken(String token);
    boolean checkLibrary(String userName,String libraryName);
}
