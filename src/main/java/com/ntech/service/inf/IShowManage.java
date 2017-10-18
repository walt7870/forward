package com.ntech.service.inf;

import java.io.IOException;
import java.util.List;

public interface IShowManage {
    boolean createGallery(String galleryName) throws IOException;
    List<String> getGalleries() throws IOException;
    boolean deleteGallery(String galleryName) throws IOException;
    boolean containsGallery(String galleryName) throws IOException;
}
