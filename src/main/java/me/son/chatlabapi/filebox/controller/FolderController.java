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
import me.son.chatlabapi.global.response.ApiResponse;
import me.son.chatlabapi.global.security.CustomUserDetails;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
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
        Folder folder = folderService.createFolder(userDetails.getId(), request.name());
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

    @GetMapping("/{folderId}/download")
    public ResponseEntity<byte[]> downloadFolder(@PathVariable Long folderId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        log.info("downloadFolder - user {} downloads folder {}", userDetails.getId(), folderId);
        Folder folder = folderService.getOwnedFolder(userDetails.getId(), folderId);
        byte[] zip = fileItemService.downloadFolderAsZip(userDetails.getId(), folderId);

        String encodedName = URLEncoder.encode(folder.getName() + ".zip", StandardCharsets.UTF_8)
                .replace("+", "%20");

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + encodedName)
                .body(zip);
    }
}
