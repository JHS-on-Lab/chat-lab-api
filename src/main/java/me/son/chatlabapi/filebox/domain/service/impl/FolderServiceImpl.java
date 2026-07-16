package me.son.chatlabapi.filebox.domain.service.impl;

import lombok.RequiredArgsConstructor;

import me.son.chatlabapi.filebox.domain.entity.Folder;
import me.son.chatlabapi.filebox.domain.repository.FolderRepository;
import me.son.chatlabapi.filebox.domain.service.FolderService;
import me.son.chatlabapi.filebox.exception.FolderErrorCode;
import me.son.chatlabapi.global.exception.BusinessException;
import me.son.chatlabapi.user.domain.entity.User;
import me.son.chatlabapi.user.domain.repository.UserRepository;
import me.son.chatlabapi.user.exception.UserErrorCode;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class FolderServiceImpl implements FolderService {
    private final FolderRepository folderRepository;
    private final UserRepository userRepository;

    @Override
    public Folder createFolder(Long userId, String name) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));

        Folder folder = new Folder(name, user);

        return folderRepository.save(folder);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Folder> getMyFolders(Long userId) {
        return folderRepository.findAllByUser_IdOrderByCreatedAtDesc(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public Folder getOwnedFolder(Long userId, Long folderId) {
        return folderRepository.findByIdAndUser_Id(folderId, userId)
                .orElseThrow(() -> new BusinessException(FolderErrorCode.FOLDER_NOT_FOUND));
    }
}
