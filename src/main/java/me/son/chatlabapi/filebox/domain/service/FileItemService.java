package me.son.chatlabapi.filebox.domain.service;

import me.son.chatlabapi.filebox.domain.entity.FileItem;
import me.son.chatlabapi.filebox.dto.CreateFileRequest;

import java.util.List;

public interface FileItemService {
    FileItem createFile(Long userId, Long folderId, CreateFileRequest request);

    List<FileItem> getFiles(Long userId, Long folderId);

    byte[] downloadFolderAsZip(Long userId, Long folderId);
}
