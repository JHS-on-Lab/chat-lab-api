package me.son.chatlabapi.filebox.domain.service.impl;

import lombok.RequiredArgsConstructor;

import me.son.chatlabapi.filebox.domain.entity.Folder;
import me.son.chatlabapi.filebox.domain.repository.FileItemRepository;
import me.son.chatlabapi.filebox.domain.repository.FolderRepository;
import me.son.chatlabapi.filebox.domain.service.FolderService;
import me.son.chatlabapi.filebox.exception.FolderErrorCode;
import me.son.chatlabapi.global.exception.BusinessException;
import me.son.chatlabapi.user.domain.entity.User;
import me.son.chatlabapi.user.domain.repository.UserRepository;
import me.son.chatlabapi.user.exception.UserErrorCode;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class FolderServiceImpl implements FolderService {
    private final FolderRepository folderRepository;
    private final FileItemRepository fileItemRepository;
    private final UserRepository userRepository;

    @Override
    public Folder createFolder(Long userId, String name, Long parentId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));

        Folder parentFolder = parentId == null ? null : getOwnedFolder(userId, parentId);

        Folder folder = new Folder(name, user, parentFolder);

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

    @Override
    public void deleteFolder(Long userId, Long folderId) {
        Folder folder = getOwnedFolder(userId, folderId);

        List<Folder> subtree = collectSubtree(userId, folder);

        // 자기참조 FK 제약 때문에 자식(리프)부터 지워야 함 -> BFS 순서(부모가 항상 자식보다 먼저)를 뒤집는다.
        for (int i = subtree.size() - 1; i >= 0; i--) {
            Folder target = subtree.get(i);
            fileItemRepository.deleteAllByFolder_Id(target.getId());
            folderRepository.delete(target);
        }
    }

    // BFS로 folder 자신 + 모든 하위 폴더를 부모 -> 자식 순서로 모은다.
    private List<Folder> collectSubtree(Long userId, Folder folder) {
        Map<Long, List<Folder>> childrenByParentId = folderRepository.findAllByUser_IdOrderByCreatedAtDesc(userId)
                .stream()
                .filter(f -> f.getParentFolderId() != null)
                .collect(Collectors.groupingBy(Folder::getParentFolderId));

        List<Folder> subtree = new ArrayList<>();
        Deque<Folder> queue = new ArrayDeque<>();
        queue.add(folder);

        while (!queue.isEmpty()) {
            Folder current = queue.poll();
            subtree.add(current);
            queue.addAll(childrenByParentId.getOrDefault(current.getId(), List.of()));
        }

        return subtree;
    }
}
