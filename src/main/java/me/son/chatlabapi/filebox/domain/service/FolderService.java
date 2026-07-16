package me.son.chatlabapi.filebox.domain.service;

import me.son.chatlabapi.filebox.domain.entity.Folder;

import java.util.List;

public interface FolderService {
    Folder createFolder(Long userId, String name);

    List<Folder> getMyFolders(Long userId);

    Folder getOwnedFolder(Long userId, Long folderId);

    void deleteFolder(Long userId, Long folderId);
}
