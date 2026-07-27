package me.son.chatlabapi.filebox.domain.service;

import me.son.chatlabapi.filebox.domain.entity.FileItem;
import me.son.chatlabapi.filebox.dto.CreateFileRequest;
import me.son.chatlabapi.filebox.dto.UpdateFileRequest;

import java.util.List;

public interface FileItemService {
    FileItem createFile(Long userId, Long folderId, CreateFileRequest request);

    List<FileItem> getFiles(Long userId, Long folderId);

    FileItem updateFile(Long userId, Long folderId, Long fileId, UpdateFileRequest request);

    void deleteFile(Long userId, Long folderId, Long fileId);
}
