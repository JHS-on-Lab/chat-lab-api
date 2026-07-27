package me.son.chatlabapi.filebox.domain.service.impl;

import lombok.RequiredArgsConstructor;

import me.son.chatlabapi.filebox.domain.entity.FileItem;
import me.son.chatlabapi.filebox.domain.entity.Folder;
import me.son.chatlabapi.filebox.domain.repository.FileItemRepository;
import me.son.chatlabapi.filebox.domain.service.FileItemService;
import me.son.chatlabapi.filebox.domain.service.FolderService;
import me.son.chatlabapi.filebox.dto.CreateFileRequest;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class FileItemServiceImpl implements FileItemService {
    private final FileItemRepository fileItemRepository;
    private final FolderService folderService;

    @Override
    public FileItem createFile(Long userId, Long folderId, CreateFileRequest request) {
        Folder folder = folderService.getOwnedFolder(userId, folderId);

        FileItem fileItem = new FileItem(
                folder,
                request.title(),
                request.extension(),
                request.content() == null ? "" : request.content()
        );

        return fileItemRepository.save(fileItem);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FileItem> getFiles(Long userId, Long folderId) {
        folderService.getOwnedFolder(userId, folderId);

        return fileItemRepository.findAllByFolder_IdOrderByCreatedAtAsc(folderId);
    }
}
