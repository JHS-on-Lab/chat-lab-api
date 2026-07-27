package me.son.chatlabapi.filebox.controller;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import me.son.chatlabapi.filebox.domain.entity.Folder;
import me.son.chatlabapi.filebox.domain.service.FileItemService;
import me.son.chatlabapi.filebox.domain.service.FolderService;
import me.son.chatlabapi.filebox.dto.CreateFileRequest;
import me.son.chatlabapi.filebox.dto.CreateFolderRequest;
import me.son.chatlabapi.filebox.dto.FileResponse;
import me.son.chatlabapi.filebox.dto.FolderResponse;
import me.son.chatlabapi.filebox.dto.UpdateFileRequest;
import me.son.chatlabapi.global.response.ApiResponse;
import me.son.chatlabapi.global.security.CustomUserDetails;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/folders")
@RequiredArgsConstructor
public class FolderController {

    private final FolderService folderService;
    private final FileItemService fileItemService;

    @PostMapping
    public ApiResponse<FolderResponse> createFolder(@Valid @RequestBody CreateFolderRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        log.info("createFolder - user {} creates folder named {}", userDetails.getId(), request.name());
        Folder folder = folderService.createFolder(userDetails.getId(), request.name(), request.parentId());
        return ApiResponse.success(FolderResponse.from(folder));
    }

    @GetMapping
    public ApiResponse<List<FolderResponse>> getMyFolders(@AuthenticationPrincipal CustomUserDetails userDetails) {
        List<FolderResponse> response = folderService.getMyFolders(userDetails.getId())
                .stream()
                .map(FolderResponse::from)
                .toList();
        return ApiResponse.success(response);
    }

    @PostMapping("/{folderId}/files")
    public ApiResponse<FileResponse> createFile(@PathVariable Long folderId,
            @Valid @RequestBody CreateFileRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        log.info("createFile - user {} creates file in folder {}", userDetails.getId(), folderId);
        FileResponse response = FileResponse.from(fileItemService.createFile(userDetails.getId(), folderId, request));
        return ApiResponse.success(response);
    }

    @GetMapping("/{folderId}/files")
    public ApiResponse<List<FileResponse>> getFiles(@PathVariable Long folderId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        List<FileResponse> response = fileItemService.getFiles(userDetails.getId(), folderId)
                .stream()
                .map(FileResponse::from)
                .toList();
        return ApiResponse.success(response);
    }

    @PutMapping("/{folderId}/files/{fileId}")
    public ApiResponse<FileResponse> updateFile(@PathVariable Long folderId, @PathVariable Long fileId,
            @Valid @RequestBody UpdateFileRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        log.info("updateFile - user {} updates file {} in folder {}", userDetails.getId(), fileId, folderId);
        FileResponse response = FileResponse.from(fileItemService.updateFile(userDetails.getId(), folderId, fileId, request));
        return ApiResponse.success(response);
    }

    @DeleteMapping("/{folderId}/files/{fileId}")
    public ApiResponse<Void> deleteFile(@PathVariable Long folderId, @PathVariable Long fileId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        log.info("deleteFile - user {} deletes file {} in folder {}", userDetails.getId(), fileId, folderId);
        fileItemService.deleteFile(userDetails.getId(), folderId, fileId);
        return ApiResponse.success(null);
    }

    @DeleteMapping("/{folderId}")
    public ApiResponse<Void> deleteFolder(@PathVariable Long folderId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        log.info("deleteFolder - user {} deletes folder {}", userDetails.getId(), folderId);
        folderService.deleteFolder(userDetails.getId(), folderId);
        return ApiResponse.success(null);
    }
}
