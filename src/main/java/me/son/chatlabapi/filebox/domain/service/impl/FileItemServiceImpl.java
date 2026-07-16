package me.son.chatlabapi.filebox.domain.service.impl;

import lombok.RequiredArgsConstructor;

import me.son.chatlabapi.filebox.domain.entity.FileItem;
import me.son.chatlabapi.filebox.domain.entity.Folder;
import me.son.chatlabapi.filebox.domain.repository.FileItemRepository;
import me.son.chatlabapi.filebox.domain.service.FileItemService;
import me.son.chatlabapi.filebox.domain.service.FolderService;
import me.son.chatlabapi.filebox.dto.CreateFileRequest;
import me.son.chatlabapi.global.exception.BusinessException;
import me.son.chatlabapi.global.exception.GlobalErrorCode;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

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

    @Override
    @Transactional(readOnly = true)
    public byte[] downloadFolderAsZip(Long userId, Long folderId) {
        folderService.getOwnedFolder(userId, folderId);

        List<FileItem> files = fileItemRepository.findAllByFolder_IdOrderByCreatedAtAsc(folderId);

        Map<String, Integer> nameCounts = new HashMap<>();
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        try (ZipOutputStream zos = new ZipOutputStream(buffer, StandardCharsets.UTF_8)) {
            for (FileItem file : files) {
                String entryName = resolveUniqueEntryName(file, nameCounts);

                zos.putNextEntry(new ZipEntry(entryName));
                zos.write(file.getContent().getBytes(StandardCharsets.UTF_8));
                zos.closeEntry();
            }
        } catch (IOException e) {
            throw new BusinessException(GlobalErrorCode.INTERNAL_SERVER_ERROR);
        }

        return buffer.toByteArray();
    }

    private String resolveUniqueEntryName(FileItem file, Map<String, Integer> nameCounts) {
        String baseName = file.getTitle() + "." + file.getExtension();
        int count = nameCounts.merge(baseName, 1, Integer::sum);

        if (count == 1) {
            return baseName;
        }

        return file.getTitle() + "(" + count + ")." + file.getExtension();
    }
}
