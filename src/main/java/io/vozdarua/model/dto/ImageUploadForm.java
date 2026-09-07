package io.vozdarua.model.dto;

import org.jboss.resteasy.reactive.RestForm;
import org.jboss.resteasy.reactive.multipart.FileUpload;

public class ImageUploadForm {

    @RestForm("file")
    public FileUpload file;

}