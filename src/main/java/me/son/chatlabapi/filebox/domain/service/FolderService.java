package me.son.chatlabapi.filebox.domain.service;

import me.son.chatlabapi.filebox.domain.entity.Folder;

import java.util.List;

public interface FolderService {
    Folder createFolder(Long userId, String name, Long parentId);

    List<Folder> getMyFolders(Long userId);

    Folder getOwnedFolder(Long userId, Long folderId);

    void deleteFolder(Long userId, Long folderId);

    /**
     * 회원 삭제 시 사용자의 모든 폴더와 파일을 삭제한다.
     */
    void deleteAllByUser(Long userId);
}
